package com.example.ui.localization

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    BANGLA("bn", "বাংলা", "🇧🇩"),
    ENGLISH("en", "English", "🇬🇧");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: BANGLA
        }
    }
}

object BengaliNumberFormatter {
    private val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBengaliDigits(input: String): String {
        var result = input
        for (i in englishDigits.indices) {
            result = result.replace(englishDigits[i], bengaliDigits[i])
        }
        return result
    }

    fun formatNumber(number: Number, isBengali: Boolean): String {
        val str = number.toString()
        return if (isBengali) toBengaliDigits(str) else str
    }

    fun formatCurrency(amount: Int, isBengali: Boolean): String {
        return if (isBengali) {
            "৳ ${toBengaliDigits(amount.toString())}"
        } else {
            "৳ $amount"
        }
    }
}
