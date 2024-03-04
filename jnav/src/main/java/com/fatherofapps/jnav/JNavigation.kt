package com.fatherofapps.jnav

interface JNavigation {

    val route: String
    
    val destination: String

    val isTopDestination:Boolean
}