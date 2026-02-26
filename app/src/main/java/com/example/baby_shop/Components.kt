package com.example.baby_shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ProductItem(product: Product) {
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
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = product.title,
                modifier = Modifier.size(100.dp)
            )
            Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = product.description)
            Text(text = "Price: ${product.price}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProductListCommon(userId: Long? = null) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(userId) {
        productList = withContext(Dispatchers.IO) {
            if (userId != null) {
                dbHelper.getListingsByUser(userId)
            } else {
                dbHelper.getAllListings()
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(productList) { product ->
            ProductItem(product = product)
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
