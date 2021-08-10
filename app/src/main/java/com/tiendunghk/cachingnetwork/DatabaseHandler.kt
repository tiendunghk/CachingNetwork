package com.tiendunghk.cachingnetwork

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kotlin.collections.ArrayList

//work with SQLite
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "CachingDatabase"

        private val TABLE_CACHING = "CachingTable"

        private val KEY_ID = "_id"
        private val KEY_URL = "url"
        private val KEY_BYTES = "bytes"
        private val KEY_SIZE = "size"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_CACHING_TABLE = ("CREATE TABLE " + TABLE_CACHING + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_URL + " TEXT,"
                + KEY_BYTES + " BLOB ,"
                + KEY_SIZE + " FLOAT" + ")")

        db?.execSQL(CREATE_CACHING_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CACHING")
        onCreate(db)
    }

    fun addCaching(model: CachingModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_URL, model.url)
        contentValues.put(KEY_BYTES, model.bytes)
        contentValues.put(KEY_SIZE, model.size)

        val success = db.insert(TABLE_CACHING, null, contentValues)

        db.close()
        return success
    }

    fun getCaching(url: String): CachingModel? {

        var caching: CachingModel? = null

        val selectQuery = "SELECT *  FROM $TABLE_CACHING WHERE url = \"$url\""

        val db = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return null
        }

        var id: Int
        var url: String
        var bytes: ByteArray

        if (cursor.moveToFirst()) {

            id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            url = cursor.getString(cursor.getColumnIndex(KEY_URL))
            bytes = cursor.getBlob(cursor.getColumnIndex(KEY_BYTES))

            caching = CachingModel(id, url, bytes, 0.0F)
        }

        cursor.close()
        return caching
    }


    fun deleteCaching(url: String): Int {
        val db = this.writableDatabase

        val success = db.delete(TABLE_CACHING, KEY_URL + "=" + url, null)

        db.close()
        return success
    }

    fun getCachings(): ArrayList<CachingModel> {

        val cachingList: ArrayList<CachingModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_CACHING ORDER BY $KEY_ID ASC"

        val db = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var size: Float

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                size = cursor.getFloat(cursor.getColumnIndex(KEY_SIZE))

                val caching = CachingModel(id, null, null, size)
                cachingList.add(caching)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return cachingList
    }
}
