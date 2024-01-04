package com.fatherofapps.jnav.processors

import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.models.JNavData
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class JNavProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {


    private val visitor = JNavVisitor(environment.logger, codeGenerator = environment.codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(JNav::class.qualifiedName.toString()).forEach {
            it.accept(visitor, Unit)
        }

        return emptyList()
    }

    private fun generateNavigation(jNavData: JNavData) {

        val fileName = jNavData.name.ifEmpty { "${jNavData.fileName}Navigation" }
        val fileSpec = FileSpec.builder(packageName = jNavData.packageName, fileName)

        fileSpec.addImport("androidx.navigation", "NavType")
        fileSpec.addImport("androidx.navigation", "navArgument")
        jNavData.listImportClasses().forEach {
            fileSpec.addImport(it.packageName, it.simpleName)
        }

        val objectBuilder = TypeSpec.objectBuilder(fileName)

        if (jNavData.arguments.isNotEmpty()) {
            objectBuilder.addFunction(jNavData.argumentsFunction())

            jNavData.listArgumentProperties().forEach {
                objectBuilder.addProperty(it)
            }

            jNavData.listGetterFunction().forEach {
                objectBuilder.addFunction(it)
            }
        }
        objectBuilder.addProperty(jNavData.destinationProperty())
        objectBuilder.addProperty(jNavData.routeProperty())
        objectBuilder.addFunction(jNavData.generateCreateRouteFun())
        fileSpec.addType(objectBuilder.build())
        fileSpec.build().writeTo(environment.codeGenerator, false)

    }


    inner class JNavVisitor(
        private val logger: KSPLogger,
        private val codeGenerator: CodeGenerator
    ) : KSVisitorVoid() {


        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            classDeclaration.getDeclaredFunctions().forEach { it.accept(this, Unit) }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

            val packageName = function.packageName.asString()
            val fileName = function.simpleName.asString()

            val listFuncWithJNav: List<KSAnnotation> = function.annotations.filter {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == JNav::class.qualifiedName.toString()
            }.toList()


            if (listFuncWithJNav.isNotEmpty()) {
                val listData = listFuncWithJNav.mapNotNull {
                    it.toData(packageName,fileName)
                }
                listData.forEach { generateNavigation(it) }
            }


        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
        }

    }

}