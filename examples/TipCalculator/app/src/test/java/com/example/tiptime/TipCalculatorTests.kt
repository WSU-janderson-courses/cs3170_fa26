package com.example.tiptime

import org.junit.Test
import java.text.NumberFormat
import org.junit.Assert.assertEquals

class TipCalculatorTests {

    @Test
    fun calculateTip_20percentNoRoundUp() {
        val amount = 10.0;
        val percent = 20.0;
        val expectedTip = NumberFormat.getCurrencyInstance().format(2)
        val actualTip = calculateTip(amount, percent, false)
        assertEquals(expectedTip, actualTip)
    }
}