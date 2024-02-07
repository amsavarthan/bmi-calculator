package bmi.calculator.amsavarthan.dev.domain

object AppConstants {
    val SHARE_MESSAGE = """
        Hey, I just calculated my BMI using this app. You should try it too.
        
        Download the app from here: 
        
        https://play.google.com/store/apps/details?id=%s
    """.trimIndent()

    const val TEMP_IMAGES_FOLDER_NAME = "images"
    const val TEMP_FILE_NAME_WITH_EXTENSION = "shared_image.png"
    const val DECIMAL_FORMAT = "%.1f"

    const val INITIAL_WEIGHT = 65
    const val INITIAL_HEIGHT = 170

    const val BMI_MIN_VALUE = 0.0
    const val BMI_MAX_VALUE = 49.9

    val BMI_UNDERWEIGHT_RANGE = BMI_MIN_VALUE..18.4
    val BMI_NORMAL_RANGE = 18.5..24.9
    val BMI_OVERWEIGHT_RANGE = 25.0..29.9
    val BMI_OBESE_RANGE = 30.0..BMI_MAX_VALUE
}