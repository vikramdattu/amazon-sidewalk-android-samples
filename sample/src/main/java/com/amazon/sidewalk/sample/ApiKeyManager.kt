/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sidewalk.sample

import android.content.Context
import android.content.SharedPreferences
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyManager @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("api_key_prefs", Context.MODE_PRIVATE)
    }

    companion object {
        private const val API_KEY_PREF = "api_key"
        private const val API_KEY_FILE = "api_key.txt"
    }

    fun getStoredApiKey(): String? {
        return sharedPreferences.getString(API_KEY_PREF, null)
    }

    fun saveApiKey(apiKey: String) {
        sharedPreferences.edit().putString(API_KEY_PREF, apiKey).apply()
        // Also update the assets file for the Amazon LWA SDK
        updateAssetsApiKey(apiKey)
    }

    fun isValidApiKey(apiKey: String?): Boolean {
        return !apiKey.isNullOrBlank() &&
               apiKey != "[REPLACE WITH YOUR API KEY]" &&
               apiKey.length > 10 // Basic length validation
    }

    private fun updateAssetsApiKey(apiKey: String) {
        try {
            // Update the api_key.txt file in the app's internal storage
            // Since we can't modify assets at runtime, we'll create a copy in internal storage
            val apiKeyFile = File(context.filesDir, API_KEY_FILE)
            apiKeyFile.writeText(apiKey)
        } catch (e: Exception) {
            // Log error but don't crash
            android.util.Log.e("ApiKeyManager", "Failed to update API key file", e)
        }
    }

    fun getApiKeyFromAssets(): String? {
        return try {
            // First try to read from internal storage
            val internalFile = File(context.filesDir, API_KEY_FILE)
            if (internalFile.exists()) {
                return internalFile.readText().trim()
            }

            // Fallback to assets
            context.assets.open(API_KEY_FILE).bufferedReader().use {
                it.readText().trim()
            }
        } catch (e: Exception) {
            null
        }
    }
}
