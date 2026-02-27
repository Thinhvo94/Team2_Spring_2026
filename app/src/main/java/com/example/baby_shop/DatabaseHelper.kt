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
    val role: String, // Thêm role để phân biệt
    val address: String?,
    val paymentInfo: String?
)

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

        // Listing columns
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_CONDITION = "condition"
        private const val COLUMN_IMAGE_URL = "imageUrl"
        private const val COLUMN_USER_ID = "userId"

        // Cart items columns
        private const val COLUMN_LISTING_ID = "listingId"
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
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_PRICE TEXT, " +
                "$COLUMN_CATEGORY TEXT, " +
                "$COLUMN_CONDITION TEXT, " +
                "$COLUMN_IMAGE_URL TEXT, " +
                "$COLUMN_USER_ID INTEGER, " +
                "$COLUMN_QUANTITY INTEGER DEFAULT 1, " +
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID))")
        db.execSQL(createListingsTable)

        val createCartTable = ("CREATE TABLE $TABLE_CART_ITEMS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_LISTING_ID INTEGER, " +
                "$COLUMN_QUANTITY INTEGER, " +
                "FOREIGN KEY($COLUMN_LISTING_ID) REFERENCES $TABLE_LISTINGS($COLUMN_ID))")
        db.execSQL(createCartTable)

        seedData(db)
    }

    private fun seedData(db: SQLiteDatabase) {
        // Thêm Admin
        db.insert(TABLE_USERS, null, ContentValues().apply {
            put(COLUMN_NAME, "Admin")
            put(COLUMN_EMAIL, "admin@gmail.com")
            put(COLUMN_PASSWORD, "123")
            put(COLUMN_ROLE, "admin")
            put(COLUMN_ADDRESS, "System Office")
            put(COLUMN_PAYMENT_INFO, "Corporate Card")
        })

        val userId = db.insert(TABLE_USERS, null, ContentValues().apply {
            put(COLUMN_NAME, "Test User")
            put(COLUMN_EMAIL, "test@example.com")
            put(COLUMN_PASSWORD, "password")
            put(COLUMN_ROLE, "user")
            put(COLUMN_ADDRESS, "123 Street, City")
            put(COLUMN_PAYMENT_INFO, "Visa **** 1234")
        })

        val thinhUserId = db.insert(TABLE_USERS, null, ContentValues().apply {
            put(COLUMN_NAME, "Thinh Vo")
            put(COLUMN_EMAIL, "thinh.vo@gmail.com")
            put(COLUMN_PASSWORD, "123")
            put(COLUMN_ROLE, "user")
            put(COLUMN_ADDRESS, "Ho Chi Minh City, Vietnam")
            put(COLUMN_PAYMENT_INFO, "Momo: 090xxxxxxx")
        })

        if (userId != -1L) {
            insertListing(db, "Baby Pram", "A comfortable pram for your baby.", "$150.00", "Strollers", "New", "baby_pram", userId)
            insertListing(db, "Baby Crib", "A safe and cozy crib.", "$250.00", "Furniture", "New", "baby_crib", userId)
        }

        if (thinhUserId != -1L) {
            insertListing(db, "Baby Shoes", "Soft shoes for newborns.", "$20.00", "Clothing", "New", "baby_shoes", thinhUserId)
        }
    }

    private fun insertListing(db: SQLiteDatabase, title: String, desc: String, price: String, cat: String, cond: String, img: String, uId: Long) {
        db.insert(TABLE_LISTINGS, null, ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DESCRIPTION, desc)
            put(COLUMN_PRICE, price)
            put(COLUMN_CATEGORY, cat)
            put(COLUMN_CONDITION, cond)
            put(COLUMN_IMAGE_URL, img)
            put(COLUMN_USER_ID, uId)
            put(COLUMN_QUANTITY, 1)
        })
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) db.execSQL("ALTER TABLE $TABLE_LISTINGS ADD COLUMN $COLUMN_IMAGE_URL TEXT")
        if (oldVersion < 5) db.execSQL("ALTER TABLE $TABLE_LISTINGS ADD COLUMN $COLUMN_QUANTITY INTEGER DEFAULT 1")
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_ADDRESS TEXT")
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_PAYMENT_INFO TEXT")
        }
    }

    // --- CRUD OPERATIONS ---
    fun addUser(name: String, email: String, pass: String, role: String): Long {
        return writableDatabase.use { db ->
            db.insert(TABLE_USERS, null, ContentValues().apply {
                put(COLUMN_NAME, name)
                put(COLUMN_EMAIL, email)
                put(COLUMN_PASSWORD, pass)
                put(COLUMN_ROLE, role)
                put(COLUMN_ADDRESS, "Update your address")
                put(COLUMN_PAYMENT_INFO, "Update your payment info")
            })
        }
    }

    fun checkUser(email: String, pass: String): Long {
        readableDatabase.use { db ->
            db.rawQuery("SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?", arrayOf(email, pass)).use { cursor ->
                return if (cursor.moveToFirst()) cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)) else -1
            }
        }
    }

    fun getUserById(userId: Long): User? {
        readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString())).use { cursor ->
                return if (cursor.moveToFirst()) cursor.toUser() else null
            }
        }
    }

    // --- NEW FUNCTIONS FOR ADMIN & ROLE CHECK ---
    @SuppressLint("Range")
    fun getUserRole(userId: Long): String {
        readableDatabase.use { db ->
            db.rawQuery("SELECT $COLUMN_ROLE FROM $TABLE_USERS WHERE $COLUMN_ID = ?", arrayOf(userId.toString())).use { cursor ->
                return if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)) else "user"
            }
        }
    }

    fun addListing(title: String, description: String, price: String, category: String, condition: String, imageUrl: String, userId: Long, quantity: Int = 1): Long {
        return writableDatabase.use { db ->
            db.insert(TABLE_LISTINGS, null, ContentValues().apply {
                put(COLUMN_TITLE, title)
                put(COLUMN_DESCRIPTION, description)
                put(COLUMN_PRICE, price)
                put(COLUMN_CATEGORY, category)
                put(COLUMN_CONDITION, condition)
                put(COLUMN_IMAGE_URL, imageUrl)
                put(COLUMN_USER_ID, userId)
                put(COLUMN_QUANTITY, quantity)
            })
        }
    }

    fun getAllListings(): List<Product> {
        val productList = mutableListOf<Product>()
        readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM $TABLE_LISTINGS", null).use { cursor ->
                while (cursor.moveToNext()) {
                    productList.add(cursor.toProduct())
                }
            }
        }
        return productList
    }

    fun getListingsByUser(userId: Long): List<Product> {
        val productList = mutableListOf<Product>()
        readableDatabase.use { db ->
            db.rawQuery("SELECT * FROM $TABLE_LISTINGS WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString())).use { cursor ->
                while (cursor.moveToNext()) {
                    productList.add(cursor.toProduct())
                }
            }
        }
        return productList
    }

    fun updateListing(id: Long, title: String, description: String, price: String, condition: String, imageUrl: String, userId: Long): Int {
        return writableDatabase.use { db ->
            db.update(TABLE_LISTINGS, ContentValues().apply {
                put(COLUMN_TITLE, title)
                put(COLUMN_DESCRIPTION, description)
                put(COLUMN_PRICE, price)
                put(COLUMN_CONDITION, condition)
                put(COLUMN_IMAGE_URL, imageUrl)
                put(COLUMN_USER_ID, userId)
            }, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }
    }

    fun deleteListing(id: Long): Int {
        return writableDatabase.use { db ->
            db.delete(TABLE_LISTINGS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }
    }

    fun updateUserAddress(userId: Long, address: String): Int {
        return writableDatabase.use { db ->
            db.update(TABLE_USERS, ContentValues().apply {
                put(COLUMN_ADDRESS, address)
            }, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        }
    }

    fun updateUserPaymentInfo(userId: Long, paymentInfo: String): Int {
        return writableDatabase.use { db ->
            db.update(TABLE_USERS, ContentValues().apply {
                put(COLUMN_PAYMENT_INFO, paymentInfo)
            }, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        }
    }

    fun updateQuantity(id: Long, quantity: Int): Int {
        return writableDatabase.use { db ->
            db.update(TABLE_LISTINGS, ContentValues().apply {
                put(COLUMN_QUANTITY, quantity)
            }, "$COLUMN_ID = ?", arrayOf(id.toString()))
        }
    }

    private fun Cursor.toProduct() = Product(
        id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
        title = getString(getColumnIndexOrThrow(COLUMN_TITLE)),
        description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
        price = getString(getColumnIndexOrThrow(COLUMN_PRICE)),
        category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
        condition = getString(getColumnIndexOrThrow(COLUMN_CONDITION)),
        imageUrl = getString(getColumnIndexOrThrow(COLUMN_IMAGE_URL)),
        userId = getLong(getColumnIndexOrThrow(COLUMN_USER_ID)),
        quantity = getInt(getColumnIndexOrThrow(COLUMN_QUANTITY))
    )

    private fun Cursor.toUser() = User(
        id = getLong(getColumnIndexOrThrow(COLUMN_ID)),
        name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
        email = getString(getColumnIndexOrThrow(COLUMN_EMAIL)),
        role = getString(getColumnIndexOrThrow(COLUMN_ROLE)),
        address = getString(getColumnIndexOrThrow(COLUMN_ADDRESS)),
        paymentInfo = getString(getColumnIndexOrThrow(COLUMN_PAYMENT_INFO))
    )
}
