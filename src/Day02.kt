import Day02.Part1
import Day02.Part2
import kotlin.math.absoluteValue

// https://adventofcode.com/2024/day/2
fun main() {
    val input = readInput(2024, 2)
    println("Part 1: ${Part1.getNumSafeLevels(input)}")
    println("Part 2: ${Part2.getNumSafeLevels(input)}")
}

private object Day02 {
    object Part1 {
        fun getNumSafeLevels(input: List<String>): Int {
            val levels = parseLevels(input)
            return levels.filter { isSafe(it) }.size
        }

        fun isSafe(levels: List<Int>): Boolean {
            val isIncreasing = levels[0] < levels[1]
            var currentIndex = 1
            while (currentIndex < levels.size) {
                val currLevel = levels[currentIndex]
                val prevLevel = levels[currentIndex - 1]
                if (problemExists(prevLevel, currLevel, isIncreasing)) return false
                currentIndex++
            }
            return true
        }
    }

    object Part2 {
        fun getNumSafeLevels(input: List<String>): Int {
            val levels = parseLevels(input)
            return levels.filter { isSafe(it, dampenerUsed = false) }.size
        }

        fun isSafe(levels: List<Int>, dampenerUsed: Boolean): Boolean {
            val isIncreasing = levels[0] < levels[1]
            var currentIndex = 1
            while (currentIndex < levels.size) {
                val currLevel = levels[currentIndex]
                val prevLevel = levels[currentIndex - 1]

                if (problemExists(prevLevel, currLevel, isIncreasing)) {
                    return if (dampenerUsed) {
                        // we only get one use with the dampener, so stop the recursion here
                        false
                    } else {
                        // check all possible lists with one level removed
                        levels.indices.any { isSafe(levels.minusAt(it), dampenerUsed = true) }
                    }
                }

                currentIndex++
            }
            return true
        }

    }

    private fun parseLevels(input: List<String>): List<List<Int>> {
        return input.map {
            it.split(" ")
                .map { level -> level.toInt() }
        }
    }

    private fun problemExists(prevLevel: Int, currLevel: Int, isIncreasing: Boolean): Boolean {
        if (currLevel == prevLevel) return true
        if (isIncreasing && prevLevel > currLevel) return true
        if (!isIncreasing && prevLevel < currLevel) return true
        if ((currLevel - prevLevel).absoluteValue > 3) return true
        return false
    }

    /**
     * Returns a list containing all elements of the original collection without the element at the specified [index].
     */
    private fun <T> List<T>.minusAt(index: Int): List<T> {
        val mutableList = this.toMutableList()
        mutableList.removeAt(index)
        return mutableList.toList()
    }
}
