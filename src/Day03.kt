import Day03.Part1
import Day03.Part2

// https://adventofcode.com/2024/day/3
fun main() {
    val input = readInput(2024, 3)
    println("Part 1: ${Part1.getMultiplicationResults(input)}")
    println("Part 2: ${Part2.getMultiplicationResults(input)}")
}

private object Day03 {
    object Part1 {
        fun getMultiplicationResults(input: List<String>): Int {
            return input.sumOf { line ->
                parseUncorruptedInstructions(line).sumOf { it.result }
            }
        }

        fun parseUncorruptedInstructions(line: String): List<Instruction> {
            val regex = Regex("(mul\\()([0-9]{1,3},[0-9]{1,3})(\\))")
            val matches = regex.findAll(line)
            return matches.map { it.value }
                .map(::parseInstruction)
                .toList()
        }

        fun parseInstruction(string: String): Instruction {
            val stripped = string.substring(4, string.length - 1)
            val nums = stripped.split(",")
            return Instruction(nums.first().toInt(), nums.last().toInt())
        }
    }

    // my crusade against for loops continues
    object Part2 {
        const val ENABLED_CONDITIONAL = "do()"

        fun getMultiplicationResults(input: List<String>): Int {
            // grabs everything between a do() and a don't() conditional (or the end of the line)
            val conditionalRegex = Regex("(do\\(\\)).+?(don't\\(\\)|\$)")

            // since the input lines are all treated as one "run" of the program, we *have* to track
            // the state of the conditionals after a line is parsed
            var shouldParseInitialInstructions = true
            return input.sumOf { line ->
                // only parse from the beginning of the line if the last line ended with the do() conditional
                val initialValue = if (shouldParseInitialInstructions) {
                    parseInitialInstructions(line)
                } else {
                    0
                }
                shouldParseInitialInstructions = lineEndedEnabled(line)

                val conditionalMatches = conditionalRegex.findAll(line)
                val lines = conditionalMatches.map { it.value }
                initialValue + Part1.getMultiplicationResults(lines.toList())
            }
        }

        /**
         * Parses the instructions from the beginning of a line until the first conditional instruction
         * and returns the result of those instructions
         */
        fun parseInitialInstructions(line: String): Int {
            val initialRegex = Regex("^.+?(do\\(\\)|don't\\(\\))")
            val initialMatch = initialRegex.findAll(line)
            val initialResults = initialMatch.map { it.value }
            return Part1.getMultiplicationResults(initialResults.toList())
        }

        /**
         * Parses through a line and determines if the last conditional instruction was "do()"
         */
        fun lineEndedEnabled(line: String): Boolean {
            val lastConditionalRegex = Regex(".*(do\\(\\)|don't\\(\\)).*?\$")
            val lastConditional = lastConditionalRegex.findAll(line)
                .first()
                .groupValues
                .last()
            return lastConditional == ENABLED_CONDITIONAL
        }
    }

    data class Instruction(val left: Int, val right: Int) {
        val result = left * right
    }
}