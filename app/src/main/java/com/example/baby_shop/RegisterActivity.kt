package com.example.baby_shop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baby_shop.ui.theme.Baby_ShopTheme

class RegisterActivity : ComponentActivity() {
    // Khởi tạo DatabaseHelper
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        setContent {
            Baby_ShopTheme {
                // Truyền instance của db vào Composable
                RegisterScreen(dbHelper = db)
            }
        }
    }
}

@Composable
fun RegisterScreen(dbHelper: DatabaseHelper) {
    // Các biến để lưu trạng thái của các trường nhập liệu
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create an Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        // Trường nhập liệu cho Tên (Name)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập liệu cho Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập liệu cho Mật khẩu (Password)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Nút Đăng ký
        Button(
            onClick = {
                // Kiểm tra xem các trường có trống không
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    // Gọi hàm addUser từ DatabaseHelper để thêm người dùng
                    val result = dbHelper.addUser(name, email, password, "user") // Mặc định role là 'user'

                    if (result != -1L) {
                        // Nếu thêm thành công
                        Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                        // Tùy chọn: Chuyển người dùng về màn hình đăng nhập
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        // Nếu thêm thất bại (ví dụ: email đã tồn tại nếu bạn cài đặt UNIQUE)
                        Toast.makeText(context, "Registration Failed. Email might already exist.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Nếu có trường nào đó bị bỏ trống
                    Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    Baby_ShopTheme {
        // Trong preview, chúng ta không có context thực, nên cần tạo một db giả
        // Hoặc đơn giản là không truyền db và comment out logic trong onClick để xem giao diện
        val context = LocalContext.current
        RegisterScreen(dbHelper = DatabaseHelper(context))
    }
}