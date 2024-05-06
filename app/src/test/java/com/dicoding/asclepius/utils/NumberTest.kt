package com.dicoding.asclepius.utils

import org.junit.Assert
import org.junit.Test

class NumberTest {
    @Test
    fun decimalToPercentage() {
        val decimal = 0.123f
        val expected = "12%"
        val result = Number.decimalToPercentage(decimal)
        Assert.assertEquals(expected, result)
    }
}