package com.example.baby_shop

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

@Composable
fun ProductListCommon(loggedInUserId: Long, filterByUserId: Long?) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load data from Database
    LaunchedEffect(filterByUserId) {
        isLoading = true
        products = withContext(Dispatchers.IO) {
            if (filterByUserId != null) {
                dbHelper.getListingsByUser(filterByUserId)
            } else {
                dbHelper.getAllListings()
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products found.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductItem(product, loggedInUserId)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, loggedInUserId: Long) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Product Image Handling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val imageResId = if (!product.imageUrl.isNullOrEmpty()) {
                    context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
                } else {
                    0
                }

                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = product.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder if image not found
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "No Image",
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                }
            }

            Text(text = product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = product.price, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Text(text = "Condition: ${product.condition}", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (product.userId == loggedInUserId) {
                Button(
                    onClick = { /* Navigate to Edit Listing */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Edit My Listing")
                }
            } else {
                Button(
                    onClick = {
                        val intent = Intent(context, CheckoutActivity::class.java).apply {
                            putExtra("USER_ID", loggedInUserId)
                            putExtra("PRODUCT_ID", product.id)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Buy Now")
                }
            }
        }
    }
}

@Composable
fun AccountScreen(userId: Long) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        user = withContext(Dispatchers.IO) {
            dbHelper.getUserById(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Profile Information", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            IconButton(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                (context as? Activity)?.finish()
            }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
            }
        }

        user?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow(label = "Full Name", value = it.name)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(label = "Email Address", value = it.email)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(label = "Shipping Address", value = it.address ?: "Not set")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(label = "Payment Method", value = it.paymentInfo ?: "Not set")
                }
            }
        } ?: Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* Navigate to Edit Profile */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Profile")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
