import Day10.Part1
import Day10.Part2

// https://adventofcode.com/2024/day/10
fun main() {
    val input = readInput(2024, 10)
    println("Part 1: ${Part1.getTrailheadScoreSum(input)}")
    println("Part 2: ${Part2.getTrailheadRatingSum(input)}")
}

private object Day10 {
    object Part1 {
        fun getTrailheadScoreSum(input: List<String>): Int {
            val matrix = Matrix(input)
            var totalScore = 0
            for (y in matrix.rowRange) {
                for (x in matrix.colRange) {
                    val node = matrix.getAt(x, y)!!
                    if (node.symbol == "0") {
                        totalScore += findTrailheadScore(matrix, node)
                    }
                }
            }
            return totalScore
        }

        fun findTrailheadScore(matrix: Matrix, trailhead: Node): Int {
            val trailEnds = mutableSetOf<Node>()
            findNextTrailNode(matrix, trailhead, trailEnds)
            return trailEnds.size
        }

        fun findNextTrailNode(matrix: Matrix, current: Node, trailEnds: MutableSet<Node>) {
            val next = (current.symbol.toInt() + 1).toString()
            if (next == "10") {
                trailEnds.add(current)
            }

            val north = matrix.lookAhead(current, Direction.NORTH)
            if (north != null && north.symbol == next) {
                findNextTrailNode(matrix, north, trailEnds)
            }
            val east = matrix.lookAhead(current, Direction.EAST)
            if (east != null && east.symbol == next) {
                findNextTrailNode(matrix, east, trailEnds)
            }
            val south = matrix.lookAhead(current, Direction.SOUTH)
            if (south != null && south.symbol == next) {
                findNextTrailNode(matrix, south, trailEnds)
            }
            val west = matrix.lookAhead(current, Direction.WEST)
            if (west != null && west.symbol == next) {
                findNextTrailNode(matrix, west, trailEnds)
            }
        }
    }

    object Part2 {
        fun getTrailheadRatingSum(input: List<String>): Int {
            val matrix = Matrix(input)
            var totalRating = 0
            for (y in matrix.rowRange) {
                for (x in matrix.colRange) {
                    val node = matrix.getAt(x, y)!!
                    if (node.symbol == "0") {
                        totalRating += findTrailheadRating(matrix, node)
                    }
                }
            }
            return totalRating
        }

        fun findTrailheadRating(matrix: Matrix, trailhead: Node): Int {
            val trails = mutableListOf(mutableListOf<Node>())
            findNextTrailNode(matrix, trailhead, 0, trails)
            return trails
                .filter { it.any { node -> node.symbol == "9" } }
                .size
        }

        fun findNextTrailNode(
            matrix: Matrix,
            current: Node,
            currentTrailIdx: Int,
            trails: MutableList<MutableList<Node>>,
        ) {
            trails[currentTrailIdx].add(current)
            val next = (current.symbol.toInt() + 1).toString()
            if (next == "10") {
                return
            }

            val north = matrix.lookAhead(current, Direction.NORTH)
            val east = matrix.lookAhead(current, Direction.EAST)
            val south = matrix.lookAhead(current, Direction.SOUTH)
            val west = matrix.lookAhead(current, Direction.WEST)
            val branches = listOfNotNull(north, east, south, west).filter { it.symbol == next }

            for (i in branches.indices) {
                val idx = if (i > 0) {
                    trails.add(mutableListOf())
                    trails.size - 1
                } else {
                    currentTrailIdx
                }
                findNextTrailNode(matrix, branches[i], idx, trails)
            }
        }
    }

    data class Matrix(private val input: List<String>) {
        val numCols = input[0].length
        val colRange = 0..<numCols
        val numRows = input.size
        val rowRange = 0..<numRows
        fun getAt(x: Int, y: Int): Node? {
            if (x in colRange && y in rowRange) {
                return Node(x, y, input[y][x].toString())
            }
            return null
        }

        fun lookAhead(curr: Node, direction: Direction): Node? {
            val (x, y) = when (direction) {
                Direction.NORTH -> Pair(curr.x, curr.y - 1)
                Direction.EAST -> Pair(curr.x + 1, curr.y)
                Direction.SOUTH -> Pair(curr.x, curr.y + 1)
                Direction.WEST -> Pair(curr.x - 1, curr.y)
            }
            return getAt(x, y)
        }
    }

    data class Node(val x: Int, val y: Int, val symbol: String = "X")

    enum class Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST,
    }
}