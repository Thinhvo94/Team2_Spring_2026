package com.example.baby_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baby_shop.ui.theme.Baby_ShopTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(userId: Long) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    
    var shippingAddress by remember { mutableStateOf("") }
    var isEditingAddress by remember { mutableStateOf(false) }
    var newAddress by remember { mutableStateOf("") }
    
    var paymentMethod by remember { mutableStateOf("") }
    var isEditingPayment by remember { mutableStateOf(false) }
    var newPaymentMethod by remember { mutableStateOf("") }
    
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(userId, refreshTrigger) {
        withContext(Dispatchers.IO) {
            user = dbHelper.getUserById(userId)
            productList = dbHelper.getListingsByUser(userId)
        }
        if (refreshTrigger == 0) {
            shippingAddress = user?.address ?: "Not set"
            newAddress = shippingAddress
            paymentMethod = user?.paymentInfo ?: "Not set"
            newPaymentMethod = paymentMethod
        }
    }

    // Helper to parse price string like "$150.00"
    fun parsePrice(price: String): Double {
        return price.replace("$", "").toDoubleOrNull() ?: 0.0
    }

    val subtotal = productList.sumOf { parsePrice(it.price) * it.quantity }
    val shippingHandling = if (subtotal > 50.0 || subtotal == 0.0) 0.0 else 10.0
    val estimatedTax = subtotal * 0.1 // Let's assume 10% tax
    val orderTotal = subtotal + shippingHandling + estimatedTax

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Shipping Information", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditingAddress) {
                        OutlinedTextField(
                            value = newAddress,
                            onValueChange = { newAddress = it },
                            label = { Text("Enter New Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isEditingAddress = false }) {
                                Text("Cancel")
                            }
                            Button(onClick = {
                                if (newAddress.isNotBlank()) {
                                    shippingAddress = newAddress
                                    dbHelper.updateUserAddress(userId, newAddress)
                                    isEditingAddress = false
                                }
                            }) {
                                Text("Save")
                            }
                        }
                    } else {
                        Text(text = "Deliver to:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(text = shippingAddress, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { isEditingAddress = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Change")
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(text = "Payment Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditingPayment) {
                        OutlinedTextField(
                            value = newPaymentMethod,
                            onValueChange = { newPaymentMethod = it },
                            label = { Text("Enter Payment Method") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { isEditingPayment = false }) {
                                Text("Cancel")
                            }
                            Button(onClick = {
                                if (newPaymentMethod.isNotBlank()) {
                                    paymentMethod = newPaymentMethod
                                    dbHelper.updateUserPaymentInfo(userId, newPaymentMethod)
                                    isEditingPayment = false
                                }
                            }) {
                                Text("Save")
                            }
                        }
                    } else {
                        Text(text = "Method:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(text = paymentMethod, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { isEditingPayment = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Change")
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Order Summary and calculations (rest of the code remains same)
            Text(text = "Order Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(productList) { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.title, style = MaterialTheme.typography.bodyLarge)
                            Text(text = product.price, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (product.quantity > 1) {
                                    dbHelper.updateQuantity(product.id, product.quantity - 1)
                                } else {
                                    dbHelper.deleteListing(product.id)
                                }
                                refreshTrigger++
                            }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(text = product.quantity.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            IconButton(onClick = {
                                dbHelper.updateQuantity(product.id, product.quantity + 1)
                                refreshTrigger++
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(label = "Shipping & Handling:", value = "$${"%.2f".format(shippingHandling)}")
                SummaryRow(label = "Estimated tax to be collected:", value = "$${"%.2f".format(estimatedTax)}")
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(label = "Order Total:", value = "$${"%.2f".format(orderTotal)}", isBold = true, fontSize = 20)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Confirm logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Confirm and Pay")
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isBold: Boolean = false, fontSize: Int = 16) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, fontSize = fontSize.sp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal, fontSize = fontSize.sp))
    }
}
