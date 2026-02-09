package com.example.baby_shop

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        private const val COLUMN_ID = "id" // <-- FIX: Define COLUMN_ID here
    }

    override fun onCreate(db: SQLiteDatabase) {

        val createUsersTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + // Use the constant
                "name TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "role TEXT)")
        db.execSQL(createUsersTable)

        val createListingsTable = ("CREATE TABLE $TABLE_LISTINGS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + // Use the constant
                "title TEXT, " +
                "description TEXT, " +
                "price TEXT, " +
                "category TEXT, " +
                "condition TEXT, " +
                "userId INTEGER, " +
                "FOREIGN KEY(userId) REFERENCES $TABLE_USERS($COLUMN_ID))") // Use the constant
        db.execSQL(createListingsTable)


        val createCartTable = ("CREATE TABLE $TABLE_CART_ITEMS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + // Use the constant
                "listingId INTEGER, " +
                "quantity INTEGER, " +
                "FOREIGN KEY(listingId) REFERENCES $TABLE_LISTINGS($COLUMN_ID))") // Use the constant
        db.execSQL(createCartTable)
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
    fun getAllListings(): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LISTINGS", null)
        // ... logic to parse cursor into a list ...
        cursor.close()
        return listOf()
    }

    fun getUser(userId: Long): List<String> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LISTINGS WHERE userId = ?", arrayOf(userId.toString()))
        // ... logic to parse cursor into a list ...
        cursor.close()
        return listOf()
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

        // Use the defined constant
        return db.update(TABLE_LISTINGS, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    //fun updateUser

    // DELETE
    fun deleteListing(id: Int): Int {
        val db = this.writableDatabase
        // Use the defined constant
        return db.delete(TABLE_LISTINGS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}