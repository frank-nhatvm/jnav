package com.fatherofapps.jnav.processors

import com.fatherofapps.jnav.annotations.JDataType
import com.fatherofapps.jnav.models.JNavData
import com.fatherofapps.jnav.models.JNavTypeData
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueArgument


fun KSAnnotation.toData(pk: String? = null, fn: String? = null): JNavData? {
    if (shortName.asString() == "JNav") {
        val packageName = pk ?: annotationType.resolve().declaration.packageName.asString()
        val fileName = fn ?: annotationType.resolve().declaration.simpleName.asString()


        val name = arguments.find { it.name?.asString() == "name" }?.value as? String
        val baseRoute = arguments.find { it.name?.asString() == "baseRoute" }?.value as? String
        val destination = arguments.find { it.name?.asString() == "destination" }?.value as? String
        val isTopDestination =
            arguments.find { it.name?.asString() == "isTopDestination" }?.value as? Boolean ?: false
        val isGenerateObject =
            arguments.find { it.name?.asString() == "isGenerateObject" }?.value as? Boolean ?: true

        val argumentsJNavType =
            arguments.find { it.name?.asString() == "arguments" }?.value

        val listOfJNavTypeData = mutableListOf<JNavTypeData>()


        val listParams = argumentsJNavType as ArrayList<KSAnnotation>

        listParams.forEach { paramAnnotation ->
            val navTypeName =
                paramAnnotation.arguments.find { it.name?.asString() == "name" }?.value as String

            val nullableNavType =
                paramAnnotation.arguments.find { it.name?.asString() == "isNullable" }?.value as? Boolean
                    ?: false

            val ksDataType = paramAnnotation.arguments.find {
                it.name?.asString() == "dataType"
            }?.value as KSType

            val dataType = dataType(ksDataType)

            val type = paramAnnotation.arguments.find { it.name?.asString() == "type" }
            if (type != null) {
                val (simpleNameType, packageNameType) = resolveKClass(type)
                val navTypeData = JNavTypeData(
                    name = navTypeName,
                    simpleNameType = simpleNameType,
                    packageNameType = packageNameType,
                    isNullable = nullableNavType,
                    dataType = dataType
                )
                listOfJNavTypeData.add(navTypeData)
            }
        }


        if (name != null && baseRoute != null && destination != null) {
            return JNavData(
                packageName = packageName,
                fileName = fileName,
                name = name,
                baseRoute = baseRoute,
                destination = destination,
                isTopDestination = isTopDestination,
                isGenerateObject = isGenerateObject,
                arguments = listOfJNavTypeData
            )
        }

    }
    return null
}

private fun dataType(ksType: KSType): JDataType {

    val simpleName = ksType.declaration.simpleName.asString()

    return try {

        val classZ = Class.forName(JDataType::class.qualifiedName).enumConstants as Array<JDataType>

        classZ.find { it.name == simpleName } ?: throw Exception("Can not find $simpleName")

    } catch (e: Exception) {
        throw Exception("Can not find the Enum class: ${e.message}")
    }
}

private fun resolveKClass(typeArgument: KSValueArgument): Pair<String, String> {
    val ksType = typeArgument.value as KSType
    val simpleName = ksType.declaration.simpleName.asString()
    val packageName = ksType.declaration.packageName.asString()

    return Pair(simpleName, packageName)
}