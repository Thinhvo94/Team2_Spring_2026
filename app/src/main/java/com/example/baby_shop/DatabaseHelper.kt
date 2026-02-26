package com.example.baby_shop

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val price: String,
    val category: String,
    val condition: String,
    val userId: Long,
    val imageUrl: String?,
    val quantity: Int = 1
)

// Extends SQLiteOpenHelper to manage DB
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ListingsDatabase.db"
        private const val DATABASE_VERSION = 5

        // Table Names
        private const val TABLE_USERS = "users"
        private const val TABLE_LISTINGS = "listings"
        private const val TABLE_CART_ITEMS = "cart_items"

        // Common column name
        private const val COLUMN_ID = "id"
        private const val COLUMN_QUANTITY = "quantity"
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
                "imageUrl TEXT, " +
                "userId INTEGER, " +
                "$COLUMN_QUANTITY INTEGER DEFAULT 1, " +
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

        // Add user thinh.vo@gmail.com
        val thinhUser = ContentValues()
        thinhUser.put("name", "Thinh Vo")
        thinhUser.put("email", "thinh.vo@gmail.com")
        thinhUser.put("password", "123")
        thinhUser.put("role", "user")
        val thinhUserId = db.insert(TABLE_USERS, null, thinhUser)

        // Add sample listings for the user
        if (userId != -1L) {
            val sampleData = ContentValues()
            sampleData.put("title", "Baby Pram")
            sampleData.put("description", "A comfortable pram for your baby.")
            sampleData.put("price", "$150.00")
            sampleData.put("category", "Strollers")
            sampleData.put("condition", "New")
            sampleData.put("imageUrl", "baby_pram")
            sampleData.put("userId", userId)
            sampleData.put(COLUMN_QUANTITY, 1)
            db.insert(TABLE_LISTINGS, null, sampleData)

            sampleData.clear()
            sampleData.put("title", "Baby Crib")
            sampleData.put("description", "A safe and cozy crib.")
            sampleData.put("price", "$250.00")
            sampleData.put("category", "Furniture")
            sampleData.put("condition", "New")
            sampleData.put("imageUrl", "baby_crib")
            sampleData.put("userId", userId)
            sampleData.put(COLUMN_QUANTITY, 1)
            db.insert(TABLE_LISTINGS, null, sampleData)
        }

        // Add some listings for Thinh Vo
        if (thinhUserId != -1L) {
            val thinhData = ContentValues()
            thinhData.put("title", "Baby Shoes")
            thinhData.put("description", "Soft shoes for newborns.")
            thinhData.put("price", "$20.00")
            thinhData.put("category", "Clothing")
            thinhData.put("condition", "New")
            thinhData.put("imageUrl", "baby_shoes")
            thinhData.put("userId", thinhUserId)
            thinhData.put(COLUMN_QUANTITY, 1)
            db.insert(TABLE_LISTINGS, null, thinhData)
        }
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_LISTINGS ADD COLUMN imageUrl TEXT;")
        }
        if (oldVersion < 3) {
            val thinhUser = ContentValues()
            thinhUser.put("name", "Thinh Vo")
            thinhUser.put("email", "thinh.vo@gmail.com")
            thinhUser.put("password", "123")
            thinhUser.put("role", "user")
            db.insert(TABLE_USERS, null, thinhUser)
        }
        if (oldVersion < 4) {
            db.delete(TABLE_USERS, "email = ?", arrayOf("thinh.vo@gamil.com"))
            val thinhUser = ContentValues()
            thinhUser.put("name", "Thinh Vo")
            thinhUser.put("email", "thinh.vo@gmail.com")
            thinhUser.put("password", "123")
            thinhUser.put("role", "user")
            val thinhUserId = db.insert(TABLE_USERS, null, thinhUser)

            if (thinhUserId != -1L) {
                val thinhData = ContentValues()
                thinhData.put("title", "Baby Shoes")
                thinhData.put("description", "Soft shoes for newborns.")
                thinhData.put("price", "$20.00")
                thinhData.put("category", "Clothing")
                thinhData.put("condition", "New")
                thinhData.put("imageUrl", "baby_shoes")
                thinhData.put("userId", thinhUserId)
                db.insert(TABLE_LISTINGS, null, thinhData)
            }
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE $TABLE_LISTINGS ADD COLUMN $COLUMN_QUANTITY INTEGER DEFAULT 1;")
        }
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

    @SuppressLint("Range")
    fun checkUser(email: String, pass: String): Long {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_ID FROM $TABLE_USERS WHERE email = ? AND password = ?", arrayOf(email, pass))
        var userId: Long = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
        }
        cursor.close()
        db.close()
        return userId
    }

    fun addListing(title: String, description: String, price: String, category: String, condition: String, imageUrl: String, userId: Long, quantity: Int = 1): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", title)
        contentValues.put("description", description)
        contentValues.put("price", price)
        contentValues.put("category", category)
        contentValues.put("condition", condition)
        contentValues.put("imageUrl", imageUrl)
        contentValues.put("userId", userId)
        contentValues.put(COLUMN_QUANTITY, quantity)

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
                    imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl")) ?: "",
                    userId = cursor.getLong(cursor.getColumnIndex("userId")),
                    quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    @SuppressLint("Range")
    fun getListingsByUser(userId: Long): List<Product> {
        val productList = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_LISTINGS WHERE userId = ?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndex("title")),
                    description = cursor.getString(cursor.getColumnIndex("description")),
                    price = cursor.getString(cursor.getColumnIndex("price")),
                    category = cursor.getString(cursor.getColumnIndex("category")),
                    condition = cursor.getString(cursor.getColumnIndex("condition")),
                    imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl")) ?: "",
                    userId = cursor.getLong(cursor.getColumnIndex("userId")),
                    quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
                )
                productList.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    // UPDATE
    fun updateListing(id: Long, title: String, description: String, price: String, condition: String, imageUrl: String, userId: Long): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", title)
        contentValues.put("description", description)
        contentValues.put("price", price)
        contentValues.put("condition", condition)
        contentValues.put("imageUrl", imageUrl)
        contentValues.put("userId", userId)

        return db.update(TABLE_LISTINGS, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateQuantity(id: Long, newQuantity: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_QUANTITY, newQuantity)
        val result = db.update(TABLE_LISTINGS, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // DELETE
    fun deleteListing(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_LISTINGS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getListingByUserAndTitle(userId: Long, title: String): Product? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LISTINGS WHERE userId = ? AND title = ?", arrayOf(userId.toString(), title))
        var product: Product? = null
        if (cursor.moveToFirst()) {
            product = Product(
                id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndex("title")),
                description = cursor.getString(cursor.getColumnIndex("description")),
                price = cursor.getString(cursor.getColumnIndex("price")),
                category = cursor.getString(cursor.getColumnIndex("category")),
                condition = cursor.getString(cursor.getColumnIndex("condition")),
                imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl")) ?: "",
                userId = cursor.getLong(cursor.getColumnIndex("userId")),
                quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))
            )
        }
        cursor.close()
        db.close()
        return product
    }
}
