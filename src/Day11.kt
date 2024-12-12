import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

// https://adventofcode.com/2024/day/11
fun main() = runBlocking(Dispatchers.Default.limitedParallelism(64)) {
    val input = readInput(2024, 11)
    val testInput = listOf("125 17")
    println("Part 1: ${Day11.Part1.blink(input, 25)}")
    println("Part 2: ${Day11.Part2.blink(testInput, 75)}")
}

private object Day11 {
    object Part1 {
        fun blink(input: List<String>, times: Int): Int {
            var stoneList = input[0].split(" ")
            for (i in 0..<times) {
                stoneList = stoneList.map { mutateStone(it) }.flatten()
            }
            return stoneList.size
        }

        fun mutateStone(stone: String): List<String> {
            val num = stone.toLong()
            if (num == 0L) {
                return listOf("1")
            } else if (stone.length % 2 == 0) {
                val (left, right) = stone.chunked(stone.length / 2)
                return listOf(left.toLong().toString(), right.toLong().toString())
            } else {
                return listOf((num * 2024).toString())
            }
        }
    }

    // too slow, will not complete
    object Part2 {
        private val radix = 32

        suspend fun blink(input: List<String>, times: Int): Int = coroutineScope {
            val stones = input[0].split(" ")
            stones.map {
                async {
                    println("stone: $it")
                    blinkAsync(it, times)
                }
            }
                .awaitAll()
                .sum()
        }

        fun blink(stone: String, remainingBlinks: Int): Int {
            var numStones = 0
            if (remainingBlinks == 1) {
                return when {
                    stone == "0" -> 1
                    stone.length % 2 == 0 -> 2
                    else -> 1
                }
            }

            val newStones = Part1.mutateStone(stone)
            numStones += newStones.sumOf { blink(it, remainingBlinks - 1) }
            return numStones
        }

        suspend fun blinkAsync(stone: String, remainingBlinks: Int): Int = coroutineScope {
            var numStones = 0
            if (remainingBlinks == 1) {
                return@coroutineScope when {
                    stone == "0" -> 1
                    stone.length % 2 == 0 -> 2
                    else -> 1
                }
            }

            val newStones = Part1.mutateStone(stone)
            numStones += newStones.map {
                async {
                    blink(it, remainingBlinks - 1)
                }
            }.awaitAll().sum()
            numStones
        }

        fun mutateStones(stones: String): String {
            return buildString {
                var numStart = 0
                for (i in stones.indices) {
                    val curr = stones.getOrNull(i) ?: break
                    if (curr == ' ') {
                        val currentNum = stones.substring(numStart, i)
                        val currentNumBase10 = currentNum.toLong(radix = radix).toString()
                        val length = (currentNum.toLong(radix = radix).toString()).length
                        val replacement = if (currentNum == "0") {
                            "1"
                        } else if (length % 2 == 0) {
                            val (left, right) = currentNumBase10.chunked(length / 2)
                            "${
                                left.toLong().toString(radix = radix)
                            } ${right.toLong().toString(radix = radix)}"
                        } else {
                            (currentNum.toLong(radix = radix) * 2024).toString(radix = radix)
                        }
                        append("$replacement ")
                        numStart = i + 1
                    }
                }
            }
        }
    }
}