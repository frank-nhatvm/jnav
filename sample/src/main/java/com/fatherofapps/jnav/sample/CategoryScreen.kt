package com.fatherofapps.jnav.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        ),
        JNavArg(name = "parentCategoryId",
            type = String::class,
            isNullable = true)
    ]
)
fun CategoryScreen(
    categoryId: Int,
    categoryName: String,
    parentCateId:String?
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Category name: $categoryName")
        Spacer(modifier = Modifier.height(12.dp))
        Text("Category id: $categoryId")
        Spacer(modifier = Modifier.height(22.dp))
        if(parentCateId != null){
            Text(text = "Parent category id: $parentCateId")
        }else{
            Text(text = "Root category")
        }
        Spacer(modifier = Modifier.height(24.dp))

    }

}