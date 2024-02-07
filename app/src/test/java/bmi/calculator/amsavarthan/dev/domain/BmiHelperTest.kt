package bmi.calculator.amsavarthan.dev.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class BmiHelperTest {

    @Test
    fun `calculateBmi should return correct value`() {
        val weightInKg = 50
        val heightInCm = 200
        assertEquals(12.5, BmiHelper.calculateBmi(weightInKg, heightInCm), 0.0)
    }
}