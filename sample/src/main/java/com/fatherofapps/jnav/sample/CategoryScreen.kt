package com.fatherofapps.jnav.sample

import androidx.compose.runtime.Composable
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.annotations.JNavArg


@Composable
@JNav(
    destination = "category_destination",
    baseRoute = "category_route",
    name = "CategoryNavigation",
    arguments = [
        JNavArg(
            name = "categoryId",
            type = Int::class
        ),
        JNavArg(
            name = "categoryName",
            type = String::class
        )
    ]
)
fun CategoryScreen(
    categoryId: Int,
    categoryName: String
) {

}