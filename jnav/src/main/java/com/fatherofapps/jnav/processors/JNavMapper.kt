package com.fatherofapps.jnav.processors

import com.fatherofapps.jnav.annotations.JDataType
import com.fatherofapps.jnav.models.JNavData
import com.fatherofapps.jnav.models.JNavTypeData
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
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


            val argValue = paramAnnotation.arguments
                .find { it.name?.asString() == "dataType" }
                ?.value
            val dataType = resolveJDataTypeFromArgument(argValue)
                ?: throw IllegalArgumentException("Missing or invalid 'dataType' for parameter: $navTypeName")

//            val ksDataType: KSType? = when (argValue) {
//                is KSType -> argValue
//                is KSClassDeclaration -> {
//                    if (argValue.classKind == ClassKind.ENUM_ENTRY) {
//                        // synthesize a KSType for the enum entry’s parent
//                        argValue.asStarProjectedType()
//                    } else null
//                }
//
//                else -> null
//            }
//            val dataType = dataType(ksDataType)

            val type = paramAnnotation.arguments.find { it.name?.asString() == "type" }
            val customNavType =
                paramAnnotation.arguments.find { it.name?.asString() == "customNavType" }
            if (type != null && customNavType != null) {
                val (simpleNameType, packageNameType) = resolveKClass(type)
                val (simpleNameCustomNavType, packageNameCustomNavType) = resolveKClass(
                    customNavType
                )

                val navTypeData = JNavTypeData(
                    name = navTypeName,
                    simpleNameType = simpleNameType,
                    packageNameType = packageNameType,
                    isNullable = nullableNavType,
                    dataType = dataType,
                    simpleNameCustomNavType = simpleNameCustomNavType,
                    packageNameCustomNavType = packageNameCustomNavType
                )
                listOfJNavTypeData.add(navTypeData)
            }


        }


        if (name != null && baseRoute != null && destination != null) {
            return JNavData(
                dependenciesFile = containingFile,
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

private fun resolveJDataTypeFromArgument(argValue: Any?): JDataType? {
    // 1) If it's already a KSType (older KSP may provide KSType)
    if (argValue is KSType) {
        val decl = argValue.declaration
        if (decl is KSClassDeclaration) {
            // If it's an enum ENTRY, use entry name (e.g. "Enum")
            if (decl.classKind == ClassKind.ENUM_ENTRY) {
                val entryName = decl.simpleName.asString()
                return JDataType.entries.find { it.name == entryName }
            }
            // If it's not an enum entry, fallback to declaration simpleName
            val name = decl.simpleName.asString()
            return JDataType.entries.find { it.name == name }
        } else {
            // Fallback: try declaration's simple name
            val name = argValue.declaration.simpleName.asString()
            return JDataType.entries.find { it.name == name }
        }
    }

    // 2) If KSP gave a KSClassDeclaration directly (Kotlin 2.x may do this for enum entries)
    if (argValue is KSClassDeclaration) {
        if (argValue.classKind == ClassKind.ENUM_ENTRY) {
            val entryName = argValue.simpleName.asString()
            return JDataType.entries.find { it.name == entryName }
        } else {
            val name = argValue.simpleName.asString()
            return JDataType.entries.find { it.name == name }
        }
    }

    // 3) unsupported shape
    return null
}

private fun dataType(ksType: KSType): JDataType {

    val simpleName = ksType.declaration.simpleName.asString()
    ksType.declaration.qualifiedName
    return JDataType.entries.find { it.name == simpleName }
        ?: throw IllegalArgumentException("Can not find JDataType for: $simpleName")

//    return try {
//
//        val classZ = Class.forName(JDataType::class.qualifiedName).enumConstants as Array<JDataType>
//
//        classZ.find { it.name == simpleName } ?: throw Exception("Can not find $simpleName")
//
//    } catch (e: Exception) {
//        throw Exception("Can not find the Enum class: ${e.message}")
//    }
}

private fun resolveKClass(typeArgument: KSValueArgument): Pair<String, String> {
//    val ksType = typeArgument.value as KSType
//
//    val simpleName = ksType.declaration.simpleName.asString()
//    val packageName = ksType.declaration.packageName.asString()
//
//    return Pair(simpleName, packageName)
    val value = typeArgument.value

    // Value may be a KSType or KSClassDeclaration depending on KSP version
    val ksType: KSType? = when (value) {
        is KSType -> value
        is KSClassDeclaration -> {
            // create a KSType representing this declaration (no type args)
            try {
                value.asType(emptyList())
            } catch (e: Exception) {
                null
            }
        }

        else -> null
    }

    if (ksType == null) {
        throw IllegalArgumentException("Unsupported type argument: ${value?.javaClass}")
    }

    val simpleName = ksType.declaration.simpleName.asString()
    val packageName = ksType.declaration.packageName.asString()
    return Pair(simpleName, packageName)
}