package com.example.baby_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baby_shop.ui.theme.Baby_ShopTheme

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getLongExtra("USER_ID", -1L)
        enableEdgeToEdge()
        setContent {
            Baby_ShopTheme {
                CheckoutScreen(userId)
            }
        }
    }
}

@Composable
fun CheckoutScreen(userId: Long) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Checkout") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Checkout Page for User ID: $userId", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Processing your payment...")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { /* Handle Payment logic */ }) {
                Text("Confirm Payment")
            }
        }
    }
}
