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

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class ApiKeyDialog(
    private val context: Context,
    private val apiKeyManager: ApiKeyManager,
    private val onApiKeyEntered: (String) -> Unit
) {

    fun show() {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 40)
        }

        val titleText = TextView(context).apply {
            text = "Amazon Login with Amazon API Key"
            textSize = 18f
            setPadding(0, 0, 0, 20)
        }

        val instructionText = TextView(context).apply {
            text = "Please enter your Amazon Login with Amazon API key to continue:"
            setPadding(0, 0, 0, 20)
        }

        val editText = EditText(context).apply {
            hint = "Enter API key"
            setSingleLine(true)
            // Pre-fill with stored API key if available
            apiKeyManager.getStoredApiKey()?.let { storedKey ->
                setText(storedKey)
                setSelection(storedKey.length)
            }
        }

        layout.addView(titleText)
        layout.addView(instructionText)
        layout.addView(editText)

        AlertDialog.Builder(context)
            .setTitle("API Key Required")
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                val apiKey = editText.text.toString().trim()
                if (apiKeyManager.isValidApiKey(apiKey)) {
                    apiKeyManager.saveApiKey(apiKey)
                    onApiKeyEntered(apiKey)
                    dialog.dismiss()
                } else {
                    // Show error and don't dismiss
                    showInvalidApiKeyDialog()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Exit the app if user cancels
                (context as? android.app.Activity)?.finish()
            }
            .show()
    }

    private fun showInvalidApiKeyDialog() {
        AlertDialog.Builder(context)
            .setTitle("Invalid API Key")
            .setMessage("The API key you entered is invalid. Please check your key and try again.\n\nThe API key should be a valid Amazon Login with Amazon API key.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Show the main dialog again
                show()
            }
            .show()
    }
}
