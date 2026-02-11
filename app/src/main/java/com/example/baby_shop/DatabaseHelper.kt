package com.example.baby_shop

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Add Product data class
data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val price: String,
    val category: String,
    val condition: String,
    val userId: Long
)

// Extends SQLiteOpenHelper to manage DB
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ListingsDatabase.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        private const val TABLE_USERS = "users"
        private const val TABLE_LISTINGS = "listings"
        private const val TABLE_CART_ITEMS = "cart_items"

        // Common column name
        private const val COLUMN_ID = "id"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createUsersTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "role TEXT)")
        db.execSQL(createUsersTable)

        val createListingsTable = ("CREATE TABLE $TABLE_LISTINGS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "price TEXT, " +
                "category TEXT, " +
                "condition TEXT, " +
                "userId INTEGER, " +
                "FOREIGN KEY(userId) REFERENCES $TABLE_USERS($COLUMN_ID))")
        db.execSQL(createListingsTable)


        val createCartTable = ("CREATE TABLE $TABLE_CART_ITEMS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "listingId INTEGER, " +
                "quantity INTEGER, " +
                "FOREIGN KEY(listingId) REFERENCES $TABLE_LISTINGS($COLUMN_ID))")
        db.execSQL(createCartTable)

        // Add a sample user
        val sampleUser = ContentValues()
        sampleUser.put("name", "Test User")
        sampleUser.put("email", "test@example.com")
        sampleUser.put("password", "password")
        sampleUser.put("role", "user")
        val userId = db.insert(TABLE_USERS, null, sampleUser)

        // Add sample listings for the user
        if (userId != -1L) {
            val sampleData = ContentValues()
            sampleData.put("title", "Baby Pram")
            sampleData.put("description", "A comfortable pram for your baby.")
            sampleData.put("price", "$150.00")
            sampleData.put("category", "Strollers")
            sampleData.put("condition", "New")
            sampleData.put("userId", userId)
            db.insert(TABLE_LISTINGS, null, sampleData)

            sampleData.clear()
            sampleData.put("title", "Baby Crib")
            sampleData.put("description", "A safe and cozy crib.")
            sampleData.put("price", "$250.00")
            sampleData.put("category", "Furniture")
            sampleData.put("condition", "New")
            sampleData.put("userId", userId)
            db.insert(TABLE_LISTINGS, null, sampleData)
        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LISTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART_ITEMS")
        onCreate(db)
    }


    // --- CRUD OPERATIONS ---
    fun addUser(name: String, email: String, pass: String, role: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password", pass)
            put("role", role)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    fun addListing(title: String, description: String, price: String, category: String, condition: String, userId: Long): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", title)
        contentValues.put("description", description)
        contentValues.put("price", price)
        contentValues.put("category", category)
        contentValues.put("condition", condition)
        contentValues.put("userId", userId)

        val success = db.insert(TABLE_LISTINGS, null, contentValues)
        db.close()
        return success
    }

    // READ
    @SuppressLint("Range")
    fun getAllListings(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_LISTINGS", null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndex("title")),
                    description = cursor.getString(cursor.getColumnIndex("description")),
                    price = cursor.getString(cursor.getColumnIndex("price")),
                    category = cursor.getString(cursor.getColumnIndex("category")),
                    condition = cursor.getString(cursor.getColumnIndex("condition")),
                    userId = cursor.getLong(cursor.getColumnIndex("userId"))
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    // UPDATE
    fun updateListing(id: Int, title: String, description: String, price: String, condition: String, userId: Long): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", title)
        contentValues.put("description", description)
        contentValues.put("price", price)
        contentValues.put("condition", condition)
        contentValues.put("userId", userId)

        return db.update(TABLE_LISTINGS, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // DELETE
    fun deleteListing(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_LISTINGS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}