package com.mtjk.utils

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.mtjk.BaseInit
import java.lang.reflect.Type
import java.util.*


object MySpUtis {
    private var MyCache = "MySp-android"

    fun putString(
        key: String,
        value: String
    ) {
//        log("put key:$key  value:$value")
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString(key, value)
        edit.commit()
    }

    fun getString(key: String): String {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.getString(key, "")
        return result!!
    }

    fun putBoolean(
        key: String,
        value: Boolean
    ) {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putBoolean(key, value)
        edit.commit()
    }

    fun getBoolean(key: String): Boolean {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.getBoolean(key, false)
        return result
    }

    fun putInt(
        key: String,
        value: Int
    ) {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putInt(key, value)
        edit.commit()
    }

    fun getInt(key: String): Int {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.getInt(key, 0)
        return result
    }

    fun putLong(
        key: String,
        value: Long
    ) {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putLong(key, value)
        edit.commit()
    }

    fun getLong(key: String): Long {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.getLong(key, 0)
        return result
    }

    fun putFloat(
        key: String,
        value: Float
    ) {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putFloat(key, value)
        edit.commit()
    }

    fun getFloat(key: String): Float {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.getFloat(key, 0f)
        return result
    }

    fun putDouble(
        key: String,
        value: Double
    ) {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putString(key, value.toString())
        edit.commit()
    }

    fun getDouble(key: String): Double {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.getString(key, "0")
        return result!!.toDouble()
    }

    fun clear(key: String) {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val result = sp.edit().remove(key).commit()
    }

    fun clearAll() {
        val sp =
            BaseInit.con!!.getSharedPreferences(MyCache, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.clear()
        edit.commit()
    }

    fun putObject(
        key: String,
        obj: Any?
    ) {
        if (BaseInit.con == null)
            return

        if (obj == null)
            return
        putString(key, obj?.toJson())
    }

    fun <T> getObject(
        key: String,
        clazz: Class<T>?
    ): T? {
        if (BaseInit.con == null)
            return null
        val json =
            getString(key)
        return if (TextUtils.isEmpty(json)) {
            null
        } else try {
            Gson().fromJson<T>(json, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun <T> getObjects(
        context: Context,
        key: String,
        type: Type?
    ): ArrayList<T>? {
        val json =
            getString(key)
        return if (TextUtils.isEmpty(json)) {
            null
        } else try {
            Gson().fromJson<ArrayList<T>>(json, type)
        } catch (e: Exception) {
            null
        }
    }
}