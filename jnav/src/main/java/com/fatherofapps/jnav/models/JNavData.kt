package com.fatherofapps.jnav.models

import com.fatherofapps.jnav.annotations.JDataType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.io.Serializable


data class JNavData(
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

    fun generateCreateRouteFun(): FunSpec{
        val createRouteFun = FunSpec.builder("createRoute").returns(String::class)

        val builder = StringBuilder()
        builder.append(baseRoute)

        if(arguments.isNotEmpty()){
            arguments.sortedBy { it.isNullable }.forEach {
                jNavTypeData ->
                createRouteFun.addParameter(
                    jNavTypeData.name, jNavTypeData.navArgumentData.returnDatatype
                )
            }


            arguments.forEach {jNavTypeData ->
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
    val dataType: JDataType = JDataType.Primitive
) {


     data class NavArgumentData(
        val returnDatatype: TypeName,
        val getterFun: String,
        val navType: String
    )

    private fun initNavArgumentData(): NavArgumentData {

        val (returnDataType, getterFun, navType) = when (simpleNameType) {
            "String" -> {
                Triple(
                    String::class.asTypeName(),
                    "getString(${nameArg()})",
                    "NavType.StringType"
                )
            }

            "Int" -> {
                Triple(
                    Int::class.asTypeName(),
                    "getInt(${nameArg()})",
                    "NavType.IntType"
                )
            }

            "Boolean" -> {
                Triple(
                    Boolean::class.asTypeName(),
                    "getBoolean(${nameArg()})",
                    "NavType.BoolType"
                )
            }

            "Long" -> {
                Triple(
                    Long::class.asTypeName(),
                    "getLong(${nameArg()})",
                    "NavType.LongType"
                )
            }

            "Float" -> {
                Triple(
                    Float::class.asTypeName(),
                    "getFloat(${nameArg()})",
                    "NavType.FloatType"
                )
            }

            "LongArray" -> {
                Triple(
                    LongArray::class.asTypeName(),
                    "getLongArray(${nameArg()})",
                    "NavType.LongArrayType"
                )
            }

            "FloatArray" -> {
                Triple(
                    FloatArray::class.asTypeName(),
                    "getFloatArray(${nameArg()})",
                    "NavType.FloatArrayType"
                )
            }

            else -> {

                val typeGetter = when (dataType) {
                    JDataType.Parcelable -> {
                        "getParcelable(${nameArg()},$simpleNameType::class.java)"
                    }

                    JDataType.Serializable -> {
                        "getSerializable(${nameArg()},$simpleNameType::class.java)"
                    }

                    JDataType.Enum -> {
                        "getSerializable(${nameArg()},$simpleNameType::class.java)"
                    }

                    else -> throw Exception("Do not support data type: $simpleNameType - $dataType")
                }
                Triple(
                    ClassName(packageNameType, simpleNameType), typeGetter,
                    "NavType.fromArgType(\"$simpleNameType\",\"$packageNameType\")"
                )
            }
        }

        return NavArgumentData(returnDataType.copy(nullable = isNullable), getterFun, navType)
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
