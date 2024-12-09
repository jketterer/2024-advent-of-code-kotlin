import kotlin.math.pow

// https://adventofcode.com/2024/day/7
fun main() {
    val input = readInput(2024, 7)
    println("Part 1: ${Day07.Part1.getTotalCalibrationResult(input)}")
    println("Part 2: ${Day07.Part2.getTotalCalibrationResult(input)}")
}

private object Day07 {
    object Part1 {
        fun getTotalCalibrationResult(input: List<String>): Long {
            val equations = parseEquations(input)
            return equations
                .filter { it.isValid() }
                .sumOf { it.result }
        }

        fun Equation.isValid(): Boolean {
            // build a binary string out of all possible combinations, where 0 == "+" and 1 == "*"
            val possibleCombinations = 2.0.pow(operands.size - 1).toInt()
            for (i in 0..<possibleCombinations) {
                val binaryString = i.toUInt().toString(radix = 2).padStart(operands.size - 1, '0')
                val currentResult = binaryString.foldIndexed(operands.first()) { index, acc, c ->
                    when (c) {
                        '0' -> acc + operands[index + 1]
                        '1' -> acc * operands[index + 1]
                        else -> throw IllegalArgumentException("Character in binary string is invalid: $c")
                    }
                }
                if (currentResult == result) return true
            }
            return false
        }
    }

    object Part2 {
        fun getTotalCalibrationResult(input: List<String>): Long {
            val equations = parseEquations(input)
            return equations
                .filter { it.isValid() }
                .sumOf { it.result }
        }

        fun Equation.isValid(): Boolean {
            // build a tertiary string out of all possible combinations, where 0 == "+", 1 == "*", and 2 == "||"
            val possibleCombinations = 3.0.pow(operands.size - 1).toInt()
            for (i in 0..<possibleCombinations) {
                val operatorString = i.toUInt().toString(radix = 3).padStart(operands.size - 1, '0')
                val currentResult = operatorString.foldIndexed(operands.first()) { index, acc, c ->
                    when (c) {
                        '0' -> acc + operands[index + 1]
                        '1' -> acc * operands[index + 1]
                        '2' -> "${acc}${operands[index + 1]}".toLong()
                        else -> throw IllegalArgumentException("Character in binary string is invalid: $c")
                    }
                }
                if (currentResult == result) return true
            }
            return false
        }
    }

    fun parseEquations(input: List<String>): List<Equation> {
        return input.map { line ->
            val result = line.substringBefore(":").toLong()
            val operands = line.substringAfter(": ").split(" ").map { it.toLong() }
            Equation(result, operands)
        }
    }

    data class Equation(val result: Long, val operands: List<Long>)
}