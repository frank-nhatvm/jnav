package com.fatherofapps.jnav.models

import com.fatherofapps.jnav.annotations.JDataType
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName


data class JNavData(
    val dependenciesFile: KSFile? = null,
    val packageName: String,
    val fileName: String,
    val name: String = "",
    val baseRoute: String,
    val destination: String,
    val isTopDestination: Boolean = false,
    val isGenerateObject: Boolean = true,
    val arguments: List<JNavTypeData> = emptyList()
) {

    fun destinationProperty(): PropertySpec {
        return PropertySpec.builder("destination", String::class).initializer("\"$destination\"")
            .build()
    }

    fun routeProperty(): PropertySpec {

        val builder = StringBuilder()
        builder.append(baseRoute)

        arguments.forEach {
            if (it.isNullable) {
                builder.append("?")
            } else {
                builder.append("/")
            }
            builder.append("\$${it.nameArg()}={\$${it.nameArg()}}")
        }

        return PropertySpec.builder("route", String::class)
            .initializer("\"${builder.toString()}\"").build()
    }

    fun generateCreateRouteFun(): FunSpec {
        val createRouteFun = FunSpec.builder("createRoute").returns(String::class)

        val builder = StringBuilder()
        builder.append(baseRoute)

        if (arguments.isNotEmpty()) {
            arguments.sortedBy { it.isNullable }.forEach { jNavTypeData ->
                createRouteFun.addParameter(
                    jNavTypeData.name, jNavTypeData.navArgumentData.returnDatatype
                )
            }


            arguments.forEach { jNavTypeData ->
                if (jNavTypeData.isNullable) {
                    builder.append("?")
                } else {
                    builder.append("/")
                }
                builder.append("\$${jNavTypeData.nameArg()}=\$${jNavTypeData.name}")
            }


        }
        createRouteFun.addStatement("return·\"$builder\"")


        return createRouteFun.build()
    }

    fun listArgumentProperties(): List<PropertySpec> {
        return arguments.map { it.generateProperty() }
    }

    fun listImportClasses() = arguments.flatMap { it.navArgumentData.listImportClass }.toSet()

    fun argumentsFunction(): FunSpec {
        val namedNavArgument = ClassName("androidx.navigation", "NamedNavArgument")
        val list = ClassName("kotlin.collections", "List")
        val listOfNamNavArgument = list.parameterizedBy(namedNavArgument)
        val argumentsFun = FunSpec.builder("arguments").returns(listOfNamNavArgument)
        if (arguments.isEmpty()) {
            argumentsFun.addStatement("return listOf()")
        } else {
            val content = arguments.joinToString(",") { it.generateArgument() }
            argumentsFun.addStatement(
                """
                // list of arguments
                return listOf($content)""".trimIndent()
            )
        }

        return argumentsFun.build()
    }

    fun listGetterFunction(): List<FunSpec> {
        return arguments.map { it.generateGetterFunction() }
    }


}


data class JNavTypeData(
    val name: String,
    val simpleNameType: String,
    val packageNameType: String,
    val isNullable: Boolean = false,
    val dataType: JDataType = JDataType.Primitive,
    val debugInfo: String = "",
    val simpleNameCustomNavType: String,
    val packageNameCustomNavType: String
) {


    data class NavArgumentData(
        val returnDatatype: TypeName,
        val getterFun: String,
        val navType: String,
        val listImportClass: List<ClassName> = emptyList()
    )

    private fun initNavArgumentData(): NavArgumentData {

        return when (simpleNameType) {
            "String" -> {
                NavArgumentData(
                    returnDatatype = String::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getString(${nameArg()})", navType = "NavType.StringType"
                )

            }

            "Int" -> {
                NavArgumentData(
                    returnDatatype = Int::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getInt(${nameArg()})",
                    navType = "NavType.IntType"
                )

            }

            "Boolean" -> {
                NavArgumentData(
                    returnDatatype = Boolean::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getBoolean(${nameArg()})",
                    navType = "NavType.BoolType"
                )


            }

            "Long" -> {
                NavArgumentData(
                    returnDatatype = Long::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getLong(${nameArg()})",
                    navType = "NavType.LongType"
                )

            }

            "Float" -> {
                NavArgumentData(
                    returnDatatype = Float::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getFloat(${nameArg()})",
                    navType = "NavType.FloatType"
                )


            }

            "LongArray" -> {
                NavArgumentData(
                    returnDatatype = LongArray::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getLongArray(${nameArg()})",
                    navType = "NavType.LongArrayType"
                )

            }

            "FloatArray" -> {
                NavArgumentData(
                    returnDatatype = FloatArray::class.asTypeName().copy(nullable = isNullable),
                    getterFun = "getFloatArray(${nameArg()})",
                    navType = "NavType.FloatArrayType"
                )

            }

            else -> {

                val typeGetter = when (dataType) {
                    JDataType.Parcelable -> {
                        "getParcelable<$simpleNameType>(${nameArg()})"
                        //"getParcelable(${nameArg()},$simpleNameType::class.java)"
                    }

                    JDataType.Serializable -> {
                        "getSerializable(${nameArg()},$simpleNameType::class.java)"
                    }

                    JDataType.Enum -> {
                        "getSerializable(${nameArg()},$simpleNameType::class.java)"
                    }

                    else -> throw Exception("Do not support data type: $simpleNameType - $dataType")
                }

                NavArgumentData(
                    returnDatatype = ClassName(
                        packageNameType,
                        simpleNameType
                    ).copy(nullable = isNullable),
                    getterFun = typeGetter,
                    navType = "$simpleNameCustomNavType()",
                    listImportClass = listOf(
                        ClassName(packageNameCustomNavType, simpleNameCustomNavType)
                    )
                )

            }
        }


    }

    val navArgumentData: NavArgumentData = initNavArgumentData()

    fun generateArgument(): String {
        return """
            navArgument(${nameArg()}){
             type = ${navArgumentData.navType}
             nullable = $isNullable
            }
        """.trimIndent()
    }

    fun nameArg() = "${name}Arg"

    fun generateProperty() =
        PropertySpec.builder(nameArg(), String::class).addModifiers(KModifier.CONST)
            .initializer("\"${nameArg()}\"").build()

    fun generateGetterFunction(): FunSpec {
//        return navBackStackEntry.arguments?.getString(placeIdArg)
//            ?: throw IllegalArgumentException("placeId is required.")
        val builder = StringBuilder()
        builder.append(
            """
                // $debugInfo
            return·navBackStackEntry.arguments?.${navArgumentData.getterFun}
            """.trimIndent()
        )
        if (!isNullable) {
            val message = "$name·is·required"
            val exceptionCode = CodeBlock.of("·?: throw IllegalArgumentException(%S)", message)
            //  builder.append("?: throw IllegalArgumentException(\"$message\") ")
            builder.append(exceptionCode)
        }


        val navBackEntryClass = ClassName("androidx.navigation", "NavBackStackEntry")
        return FunSpec.builder(name).addParameter("navBackStackEntry", navBackEntryClass)
            .returns(navArgumentData.returnDatatype).addStatement(
                builder.toString()

            ).build()
    }

}
