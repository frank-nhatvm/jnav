package com.fatherofapps.jnav.sample

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fatherofapps.jnav.sample.data.AddressNavType

@Composable
fun HostScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeScreenNavigation.route) {

        composable(route = HomeScreenNavigation.route) {
            HomeScreen(openCategory = { cateId, cateName ->
                navController.navigate(
                    CategoryNavigation.createRoute(
                        categoryId = cateId, categoryName = cateName, parentCategoryId = null
                    )
                )
            }, openAddress = { address ->
                navController.navigate(AddressDetailNavigation.createRoute(address))
            })
        }

        composable(route = CategoryNavigation.route, arguments = CategoryNavigation.arguments()) {

            CategoryScreen(
                categoryId = CategoryNavigation.categoryId(it),
                categoryName = CategoryNavigation.categoryName(it),
                parentCateId = CategoryNavigation.parentCategoryId(it)
            )
        }

        composable(
            route = AddressDetailNavigation.route,
            arguments = AddressDetailNavigation.arguments()
        ) {

            val address = AddressDetailNavigation.address(it)

            AddressDetailScreen(address = address)
        }


    }
}