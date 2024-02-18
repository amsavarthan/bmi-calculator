[![GitHub license](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

# BMI Calculator ⚖️
A simple bmi calculator application made using Jetpack compose. This was primarily built with the intention of deepening understanding and practical experience with the Canvas API.

https://medium.com/towardsdev/taking-screenshot-of-a-composable-composable-to-bitmap-4ca1db29bb51
<br />

***Get the latest app from Playstore 👇***

[![BMI Calculator](https://img.shields.io/badge/BMI_Calculator-PLAYSTORE-black.svg?style=for-the-badge&logo=android)](https://play.google.com/store/apps/details?id=bmi.calculator.amsavarthan.dev)

## Screenshots 📸
[Circular Scale](https://github.com/amsavarthan/bmi-calculator/blob/main/app/src/main/java/bmi/calculator/amsavarthan/dev/presentation/components/CircularScale.kt) | [Scale](https://github.com/amsavarthan/bmi-calculator/blob/main/app/src/main/java/bmi/calculator/amsavarthan/dev/presentation/components/Scale.kt) | Result
--- | --- | ---
![](https://github.com/amsavarthan/bmi-calculator/blob/main/art/S1.webp)|![](https://github.com/amsavarthan/bmi-calculator/blob/main/art/S2.webp)|![](https://github.com/amsavarthan/bmi-calculator/blob/main/art/S3.webp)|

<br />


## Built With 🛠
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [Jetpack Compose](https://developer.android.com/jetpack/compose) - Jetpack Compose is Android’s recommended modern toolkit for building native UI.
  - [Stateflow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - StateFlow is a state-holder observable flow that emits the current and new state updates to its collectors. 
  - [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous version of a Sequence, a type of collection whose values are lazily produced.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 

<br />

## Package Structure 📦
    
    
    bmi.calculator.amsavarthan.dev         # Root Package
    ├── domain                     
    │   ├── models                 # Model classes
    │   ├── repository             # Interfaces               
    └── presentation      
        ├── components             # Reuseable composables  
        ├── destination           
        ├── ui.theme   
        └── utils


<br />

## 🧰 Build-tool

- [Android Studio Hedgehog 2023.1.1 or above](https://developer.android.com/studio)

<br />

## 📩 Contact

DM me at 👇

* Twitter: <a href="https://twitter.com/lvamsavarthan" target="_blank">@lvamsavarthan</a>
* Email: amsavarthan.a@gmail.com

<br />

## License 🔖
```
MIT License

Copyright (c) 2023 Amsavarthan Lv

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
