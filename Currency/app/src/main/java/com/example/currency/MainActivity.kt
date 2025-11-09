package com.example.currency

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerFromCurrency: Spinner
    private lateinit var spinnerToCurrency: Spinner
    private lateinit var editTextFromAmount: EditText
    private lateinit var editTextToAmount: EditText

    // Flag to prevent infinite loop when updating EditTexts
    private var isUpdating = false

    // Currency codes
    private val currencyCodes = listOf(
        "USD", "EUR", "GBP", "JPY", "CNY", "VND",
        "KRW", "AUD", "CAD", "CHF", "INR", "SGD"
    )

    // Exchange rates relative to USD (1 USD = X currency)
    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 149.50,
        "CNY" to 7.24,
        "VND" to 24500.0,
        "KRW" to 1320.0,
        "AUD" to 1.53,
        "CAD" to 1.38,
        "CHF" to 0.88,
        "INR" to 83.20,
        "SGD" to 1.34
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupSpinners()
        setupTextWatchers()
    }

    private fun initializeViews() {
        spinnerFromCurrency = findViewById(R.id.spinnerFromCurrency)
        spinnerToCurrency = findViewById(R.id.spinnerToCurrency)
        editTextFromAmount = findViewById(R.id.editTextFromAmount)
        editTextToAmount = findViewById(R.id.editTextToAmount)
    }

    private fun setupSpinners() {
        // Create adapter for spinners
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter to both spinners
        spinnerFromCurrency.adapter = adapter
        spinnerToCurrency.adapter = adapter

        // Set default selections (USD to VND)
        spinnerFromCurrency.setSelection(0) // USD
        spinnerToCurrency.setSelection(5) // VND

        // Spinner listeners
        spinnerFromCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerToCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertCurrency(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTextWatchers() {
        // TextWatcher for From Amount
        editTextFromAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency(true)
                }
            }
        })

        // TextWatcher for To Amount
        editTextToAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating) {
                    convertCurrency(false)
                }
            }
        })
    }

    private fun convertCurrency(fromSource: Boolean) {
        isUpdating = true

        try {
            val fromCurrencyPosition = spinnerFromCurrency.selectedItemPosition
            val toCurrencyPosition = spinnerToCurrency.selectedItemPosition

            val fromCurrency = currencyCodes[fromCurrencyPosition]
            val toCurrency = currencyCodes[toCurrencyPosition]

            if (fromSource) {
                // Convert from "From Amount" to "To Amount"
                val amountText = editTextFromAmount.text.toString()
                if (amountText.isNotEmpty() && amountText != ".") {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null) {
                        val result = convertAmount(amount, fromCurrency, toCurrency)
                        editTextToAmount.setText(formatAmount(result))
                    }
                } else {
                    editTextToAmount.setText("")
                }
            } else {
                // Convert from "To Amount" to "From Amount"
                val amountText = editTextToAmount.text.toString()
                if (amountText.isNotEmpty() && amountText != ".") {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null) {
                        val result = convertAmount(amount, toCurrency, fromCurrency)
                        editTextFromAmount.setText(formatAmount(result))
                    }
                } else {
                    editTextFromAmount.setText("")
                }
            }
        } catch (e: Exception) {
            // Handle any conversion errors silently
            e.printStackTrace()
        } finally {
            isUpdating = false
        }
    }

    private fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val fromRate = exchangeRates[fromCurrency] ?: 1.0
        val toRate = exchangeRates[toCurrency] ?: 1.0

        // Convert to USD first, then to target currency
        val amountInUSD = amount / fromRate
        return amountInUSD * toRate
    }

    private fun formatAmount(amount: Double): String {
        // Format the amount with appropriate decimal places
        return when {
            amount >= 1000 -> String.format(Locale.US, "%.2f", amount)
            amount >= 1 -> String.format(Locale.US, "%.4f", amount)
            else -> String.format(Locale.US, "%.6f", amount)
        }
    }
}

