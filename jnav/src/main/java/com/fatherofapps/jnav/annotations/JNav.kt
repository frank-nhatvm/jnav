package com.fatherofapps.jnav.annotations
import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class JNav(
    val name: String = "",
    val baseRoute: String,
    val destination: String,
    val isTopDestination: Boolean = false,
    val isGenerateObject: Boolean = true,
    vararg val arguments: JNavArg
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class JNavArg(
    val name: String,
    val type: KClass<*>,
    val isNullable: Boolean = false,
    val dataType: JDataType = JDataType.Primitive
)

enum class JDataType{
    Primitive,
    Parcelable,
    Enum,
    Serializable;
}
