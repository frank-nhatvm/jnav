package com.fatherofapps.jnav.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun HostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeScreenNavigation.route) {

        composable(route = HomeScreenNavigation.route) {
            HomeScreen(openCategory = { cateId, cateName ->
//navController.navigate(CategoryNavigation.)
            })
        }

        composable(route = CategoryNavigation.route, arguments = CategoryNavigation.arguments()) {

            CategoryScreen(
                categoryId = CategoryNavigation.categoryId(it),
                categoryName = CategoryNavigation.categoryName(it)
            )
        }

    }
}