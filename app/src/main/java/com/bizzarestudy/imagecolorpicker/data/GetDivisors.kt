package com.bizzarestudy.imagecolorpicker.data

object GetDivisors {
    private const val LIMIT_SIZE: Int = 500
    private const val MINIMUM_PIXEL_LEVEL = 8

    fun by(num: Int): ArrayList<Int> {
        val mid = num / 2
        val tempSet = HashSet<Int>()
        for (i in 3..mid) {
            if (num % i == 0 && i < LIMIT_SIZE) {
                tempSet.add(i - 1)
            }
        }

        if (tempSet.size < MINIMUM_PIXEL_LEVEL) {
            tempSet.addAll(by(num-1))
        }
        val result = ArrayList<Int>()
        tempSet.forEach { result.add(it) }
        result.sort()
        return result
    }
}