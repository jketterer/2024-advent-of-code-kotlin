import Day04.Part1
import Day04.Part1.SearchDirection.BACKWARD
import Day04.Part1.SearchDirection.FORWARD
import Day04.Part2

// https://adventofcode.com/2024/day/4
fun main() {
    val input = readInput(2024, 4)
    println("Part 1: ${Part1.findXmasCount(input)}")
    println("Part 2: ${Part2.findXmasCount(input)}")
}

private object Day04 {
    object Part1 {
        enum class SearchDirection(val searchString: String) {
            FORWARD("XMAS"),
            BACKWARD("SAMX")
        }

        fun findXmasCount(input: List<String>): Int {
            var count = 0
            for (rowIdx in input.indices) {
                for (colIdx in 0..<input[rowIdx].length) {
                    val letter = input[rowIdx][colIdx]
                    count += checkLetter(letter, input, rowIdx, colIdx, FORWARD)
                    count += checkLetter(letter, input, rowIdx, colIdx, BACKWARD)
                }
            }
            return count
        }

        fun checkLetter(
            letter: Char,
            input: List<String>,
            rowIdx: Int,
            pos: Int,
            direction: SearchDirection,
        ): Int {
            var count = 0
            if (letter == direction.searchString[0]) {
                if (searchHorizontal(input[rowIdx], pos, direction)) count++
                if (searchVertical(input, rowIdx, pos, direction)) count++
                if (searchDiagonalUp(input, rowIdx, pos, direction)) count++
                if (searchDiagonalDown(input, rowIdx, pos, direction)) count++
            }
            return count
        }

        fun searchHorizontal(row: String, pos: Int, direction: SearchDirection): Boolean {
            if (pos + 3 >= row.length) return false
            return row[pos + 1] == direction.searchString[1] &&
                    row[pos + 2] == direction.searchString[2] &&
                    row[pos + 3] == direction.searchString[3]
        }

        fun searchVertical(
            matrix: List<String>,
            rowIndex: Int,
            pos: Int,
            direction: SearchDirection,
        ): Boolean {
            if (rowIndex + 3 >= matrix.size) return false
            return matrix[rowIndex + 1][pos] == direction.searchString[1] &&
                    matrix[rowIndex + 2][pos] == direction.searchString[2] &&
                    matrix[rowIndex + 3][pos] == direction.searchString[3]
        }

        fun searchDiagonalDown(
            matrix: List<String>,
            rowIndex: Int,
            pos: Int,
            direction: SearchDirection,
        ): Boolean {
            if (pos + 3 >= matrix[rowIndex].length || rowIndex + 3 >= matrix.size) return false
            return matrix[rowIndex + 1][pos + 1] == direction.searchString[1] &&
                    matrix[rowIndex + 2][pos + 2] == direction.searchString[2] &&
                    matrix[rowIndex + 3][pos + 3] == direction.searchString[3]
        }

        fun searchDiagonalUp(
            matrix: List<String>,
            rowIndex: Int,
            pos: Int,
            direction: SearchDirection,
        ): Boolean {
            if (pos + 3 >= matrix[rowIndex].length || rowIndex < 3) return false
            return matrix[rowIndex - 1][pos + 1] == direction.searchString[1] &&
                    matrix[rowIndex - 2][pos + 2] == direction.searchString[2] &&
                    matrix[rowIndex - 3][pos + 3] == direction.searchString[3]
        }
    }

    object Part2 {
        const val SEARCH_STRING = "MAS"

        fun findXmasCount(input: List<String>): Int {
            var count = 0
            for (rowIdx in 1..<input.size - 1) {
                for (colIdx in 1..<input[rowIdx].length - 1) {
                    val letter = input[rowIdx][colIdx]
                    if (letter == SEARCH_STRING[1]) {
                        if (searchDiagonalDown(input, rowIdx, colIdx) &&
                            searchDiagonalUp(input, rowIdx, colIdx)
                        ) {
                            count++
                        }
                    }
                }
            }
            return count
        }


        private fun searchDiagonalDown(
            matrix: List<String>,
            rowIndex: Int,
            pos: Int,
        ): Boolean {
            return (matrix[rowIndex - 1][pos - 1] == SEARCH_STRING[0] &&
                    matrix[rowIndex + 1][pos + 1] == SEARCH_STRING[2]) ||
                    (matrix[rowIndex - 1][pos - 1] == SEARCH_STRING[2] &&
                            matrix[rowIndex + 1][pos + 1] == SEARCH_STRING[0])
        }

        private fun searchDiagonalUp(
            matrix: List<String>,
            rowIndex: Int,
            pos: Int,
        ): Boolean {
            return (matrix[rowIndex + 1][pos - 1] == SEARCH_STRING[0] &&
                    matrix[rowIndex - 1][pos + 1] == SEARCH_STRING[2]) ||
                    (matrix[rowIndex + 1][pos - 1] == SEARCH_STRING[2] &&
                            matrix[rowIndex - 1][pos + 1] == SEARCH_STRING[0])
        }
    }
}