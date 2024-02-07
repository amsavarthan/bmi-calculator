package bmi.calculator.amsavarthan.dev.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class BmiCategoryTest {

    @Test
    fun `from should return correct category`() {
        assertEquals(BmiCategory.Underweight, BmiCategory.from(15.4))
        assertEquals(BmiCategory.Normal, BmiCategory.from(22.5))
        assertEquals(BmiCategory.Overweight, BmiCategory.from(27.4))
        assertEquals(BmiCategory.Obese, BmiCategory.from(40.0))
    }

}