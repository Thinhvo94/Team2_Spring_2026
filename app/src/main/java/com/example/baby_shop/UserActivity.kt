package com.example.baby_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.baby_shop.ui.theme.Baby_ShopTheme

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getLongExtra("USER_ID", -1L)
        enableEdgeToEdge()
        setContent {
            Baby_ShopTheme {
                UserApp(userId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApp(userId: Long) {
    // Chặn nút back để không quay lại màn hình Login
    BackHandler(enabled = true) {
        // Có thể để trống để không làm gì (chặn hoàn toàn)
    }

    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Shop", "My Item", "Account")
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (selectedItem != 2) { // Ẩn Search bar khi đang ở tab Account (index 2)
                TopAppBar(
                    title = {
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Search product...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (item) {
                                "Shop" -> Icon(Icons.Filled.Home, contentDescription = "Shop")
                                "My Item" -> Icon(Icons.Filled.ShoppingCart, contentDescription = "My Item/Cart")
                                "Account" -> Icon(Icons.Filled.AccountCircle, contentDescription = "Account")
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectedItem) {
                0 -> ProductListCommon(loggedInUserId = userId, filterByUserId = null)
                1 -> ProductListCommon(loggedInUserId = userId, filterByUserId = userId)
                else -> AccountScreen(userId = userId)
            }
        }
    }
}
