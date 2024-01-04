package com.fatherofapps.jnav.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fatherofapps.jnav.annotations.JNav
import com.fatherofapps.jnav.sample.data.Address

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@JNav(
    destination = "home_destination",
    baseRoute = "home_route"
)
fun HomeScreen(openCategory: (Int, String) -> Unit, openAddress: (Address) -> Unit) {

    val rememberOpenCategory = remember {
        { cateId: Int, cateName: String ->
            openCategory(cateId, cateName)
        }
    }

    val rememberOpenAddress = remember {
        {address: Address ->
            openAddress(address)
        }
    }

    var cateId by remember {
        mutableStateOf("")
    }

    var cateName by remember {
        mutableStateOf("")
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text("Home")
        }, actions = {
            IconButton(onClick = {
                rememberOpenAddress(Address(id = 10, street = "Hoang Hoa Tham"))
            }) {
                Icon(Icons.Default.AccountCircle, contentDescription = "address icon")
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            TextField(value = cateId, onValueChange = {
                cateId = it
            }, modifier = Modifier.fillMaxWidth(), label = { Text("Category Id") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = cateName,
                onValueChange = {
                    cateName = it
                },
                modifier = Modifier.fillMaxWidth(), label = { Text("Category name") },
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                rememberOpenCategory(cateId.toInt(), cateName)
            }) {
                Text(text = "Go to category")
            }
        }
    }

}