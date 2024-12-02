import Day01.Part1.calculateTotalDistance
import Day01.Part2.calculateSimilarityScore
import kotlin.math.absoluteValue

// https://adventofcode.com/2024/day/1
fun main() {
    val input = readInput(2024, 1)
    println("Part 1: ${calculateTotalDistance(input)}")
    println("Part 2: ${calculateSimilarityScore(input)}")
}

private object Day01 {
    object Part1 {
        fun calculateTotalDistance(input: List<String>): Int {
            val (left, right) = buildLists(input)
            val sortedLeft = left.sorted()
            val sortedRight = right.sorted()
            return sortedLeft.mapIndexed { i, num ->
                (num - sortedRight[i]).absoluteValue
            }.sum()
        }
    }

    object Part2 {
        fun calculateSimilarityScore(input: List<String>): Int {
            val (left, right) = buildLists(input)
            val occurrencesMap: MutableMap<Int, Int> = mutableMapOf()
            return left.sumOf { num ->
                val occurrences =
                    occurrencesMap.computeIfAbsent(num) { i -> right.count { it == i } }
                occurrences * num
            }
        }
    }

    private fun buildLists(input: List<String>): Pair<List<Int>, List<Int>> {
        val firsts: MutableList<Int> = mutableListOf()
        val seconds: MutableList<Int> = mutableListOf()

        input.forEachIndexed { i, s ->
            val pair = s.split("   ")
            if (pair.size > 2) {
                throw IllegalArgumentException("Line ${i + 1} contains more than two numbers: $pair")
            }
            firsts.add(pair.first().toInt())
            seconds.add(pair.last().toInt())
        }

        return Pair(firsts.toList(), seconds.toList())
    }
}
