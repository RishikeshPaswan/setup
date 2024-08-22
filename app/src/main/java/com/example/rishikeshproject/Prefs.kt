package com.example.rishikeshproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.lang.reflect.Type

class Prefs @SuppressLint("CommitPrefEdits") internal constructor(context: Context) {
    private class Builder(context: Context?) {
        private val context: Context

        /**
         * Method that creates an instance of Prefs
         *
         * @return an instance of Prefs
         */
        fun build(): Prefs {
            return Prefs(context)
        }

        init {
            requireNotNull(context) { "Context must not be null." }
            this.context = context.applicationContext
        }
    }

    companion object {
        private const val TAG = "Prefs"
        var singleton: Prefs? = null
        lateinit var preferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        private val GSON: Gson = Gson()
        fun with(context: Context?): Prefs? {
            if (singleton == null) {
                singleton = Builder(context).build()
            }
            return singleton
        }
    }

    init {
        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }
}