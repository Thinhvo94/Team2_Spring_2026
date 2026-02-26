package com.example.baby_shop

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProductItem(
    product: Product,
    showQuantityControls: Boolean = false,
    onIncrease: () -> Unit = {},
    onDecrease: () -> Unit = {},
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(product.imageUrl) {
        if (product.imageUrl != null) {
            val id = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
            if (id != 0) id else R.drawable.ic_launcher_background
        } else {
            R.drawable.ic_launcher_background
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = product.title,
                modifier = Modifier.size(80.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = product.description, maxLines = 1)
                Text(text = "Price: ${product.price}", fontWeight = FontWeight.Bold)
            }

            if (showQuantityControls) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDecrease) {
                        Icon(
                            imageVector = if (product.quantity > 1) Icons.Default.Delete else Icons.Default.Delete,
                            contentDescription = "Decrease",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(text = product.quantity.toString(), fontWeight = FontWeight.Bold)
                    IconButton(onClick = onIncrease) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListCommon(loggedInUserId: Long? = null, filterByUserId: Long? = null) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(filterByUserId, refreshTrigger) {
        productList = withContext(Dispatchers.IO) {
            if (filterByUserId != null) {
                dbHelper.getListingsByUser(filterByUserId)
            } else {
                dbHelper.getAllListings()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (filterByUserId != null && productList.isNotEmpty()) {
            Button(
                onClick = {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra("USER_ID", filterByUserId)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Process to Checkout (${productList.sumOf { it.quantity }} items)")
            }
        }

        if (showDialog && selectedProduct != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Purchase") },
                text = { Text("Bạn muốn mua mặt hàng '${selectedProduct?.title}' này?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            selectedProduct?.let { product ->
                                if (loggedInUserId != null) {
                                    // Check if product already exists in user's cart
                                    val existingProduct = dbHelper.getListingByUserAndTitle(loggedInUserId, product.title)
                                    if (existingProduct != null) {
                                        dbHelper.updateQuantity(existingProduct.id, existingProduct.quantity + 1)
                                        Toast.makeText(context, "Increased quantity in My Items!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        dbHelper.addListing(
                                            product.title,
                                            product.description,
                                            product.price,
                                            product.category,
                                            product.condition,
                                            product.imageUrl ?: "",
                                            loggedInUserId,
                                            1
                                        )
                                        Toast.makeText(context, "Added to My Items!", Toast.LENGTH_SHORT).show()
                                    }
                                    refreshTrigger++
                                } else {
                                    Toast.makeText(context, "Please login to buy!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(productList) { product ->
                ProductItem(
                    product = product,
                    showQuantityControls = filterByUserId != null,
                    onIncrease = {
                        dbHelper.updateQuantity(product.id, product.quantity + 1)
                        refreshTrigger++
                    },
                    onDecrease = {
                        if (product.quantity > 1) {
                            dbHelper.updateQuantity(product.id, product.quantity - 1)
                        } else {
                            dbHelper.deleteListing(product.id)
                        }
                        refreshTrigger++
                    },
                    onClick = {
                        if (filterByUserId == null) { // Only show dialog in main shop
                            selectedProduct = product
                            showDialog = true
                        }
                    }
                )
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
