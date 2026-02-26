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

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val address: String?,
    val paymentInfo: String?
)

// Extends SQLiteOpenHelper to manage DB
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ListingsDatabase.db"
        private const val DATABASE_VERSION = 6

        // Table Names
        private const val TABLE_USERS = "users"
        private const val TABLE_LISTINGS = "listings"
        private const val TABLE_CART_ITEMS = "cart_items"

        // Common column name
        private const val COLUMN_ID = "id"
        private const val COLUMN_QUANTITY = "quantity"
        
        // User columns
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_PAYMENT_INFO = "payment_info"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createUsersTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_ROLE TEXT, " +
                "$COLUMN_ADDRESS TEXT, " +
                "$COLUMN_PAYMENT_INFO TEXT)")
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
        sampleUser.put(COLUMN_NAME, "Test User")
        sampleUser.put(COLUMN_EMAIL, "test@example.com")
        sampleUser.put(COLUMN_PASSWORD, "password")
        sampleUser.put(COLUMN_ROLE, "user")
        sampleUser.put(COLUMN_ADDRESS, "123 Street, City")
        sampleUser.put(COLUMN_PAYMENT_INFO, "Visa **** 1234")
        val userId = db.insert(TABLE_USERS, null, sampleUser)

        // Add user thinh.vo@gmail.com
        val thinhUser = ContentValues()
        thinhUser.put(COLUMN_NAME, "Thinh Vo")
        thinhUser.put(COLUMN_EMAIL, "thinh.vo@gmail.com")
        thinhUser.put(COLUMN_PASSWORD, "123")
        thinhUser.put(COLUMN_ROLE, "user")
        thinhUser.put(COLUMN_ADDRESS, "Ho Chi Minh City, Vietnam")
        thinhUser.put(COLUMN_PAYMENT_INFO, "Momo: 090xxxxxxx")
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
            thinhUser.put(COLUMN_NAME, "Thinh Vo")
            thinhUser.put(COLUMN_EMAIL, "thinh.vo@gmail.com")
            thinhUser.put(COLUMN_PASSWORD, "123")
            thinhUser.put(COLUMN_ROLE, "user")
            db.insert(TABLE_USERS, null, thinhUser)
        }
        if (oldVersion < 4) {
            db.delete(TABLE_USERS, "$COLUMN_EMAIL = ?", arrayOf("thinh.vo@gamil.com"))
            val thinhUser = ContentValues()
            thinhUser.put(COLUMN_NAME, "Thinh Vo")
            thinhUser.put(COLUMN_EMAIL, "thinh.vo@gmail.com")
            thinhUser.put(COLUMN_PASSWORD, "123")
            thinhUser.put(COLUMN_ROLE, "user")
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
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_ADDRESS TEXT;")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_PAYMENT_INFO TEXT;")
            
            // Update existing users with some default data for demonstration
            val values = ContentValues()
            values.put(COLUMN_ADDRESS, "Update your address")
            values.put(COLUMN_PAYMENT_INFO, "Update your payment info")
            db.update(TABLE_USERS, values, null, null)
        }
    }


    // --- CRUD OPERATIONS ---
    fun addUser(name: String, email: String, pass: String, role: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, pass)
            put(COLUMN_ROLE, role)
            put(COLUMN_ADDRESS, "Update your address")
            put(COLUMN_PAYMENT_INFO, "Update your payment info")
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun checkUser(email: String, pass: String): Long {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, pass))
        var userId: Long = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
        }
        cursor.close()
        db.close()
        return userId
    }

    @SuppressLint("Range")
    fun getUserById(userId: Long): User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                paymentInfo = cursor.getString(cursor.getColumnIndex(COLUMN_PAYMENT_INFO))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun updateUserAddress(userId: Long, address: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_ADDRESS, address)
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result
    }

    fun updateUserPaymentInfo(userId: Long, paymentInfo: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PAYMENT_INFO, paymentInfo)
        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result
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
