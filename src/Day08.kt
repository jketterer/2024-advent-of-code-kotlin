import Day08.Part1
import Day08.Part2

// https://adventofcode.com/2024/day/8
fun main() {
    val input = readInput(2024, 8)
    println("Part 1: ${Part1.findUniqueAntinodeLocations(input)}")
    println("Part 2: ${Part2.findUniqueAntinodeLocations(input)}")
}

private object Day08 {
    object Part1 {
        fun findUniqueAntinodeLocations(input: List<String>): Int {
            val matrix = Matrix(input)
            val nodeMap = getNodes(matrix)
            val antinodes = mutableSetOf<Coord>()
            for (entry in nodeMap.entries.filter { it.value.size > 1 }) {
                for (node in entry.value) {
                    val possibleAntinodes = mutableSetOf<Diff>()
                    for (otherNode in entry.value.minus(node)) {
                        val dx = node.col - otherNode.col
                        val dy = node.row - otherNode.row
                        possibleAntinodes.add(Diff(dx, dy))
                    }
                    possibleAntinodes.map { it.toCoord(node) }
                        .filter { it.isWithin(matrix) }
                        .forEach { antinodes.add(it) }
                }
            }
            return antinodes.size
        }
    }

    object Part2 {
        fun findUniqueAntinodeLocations(input: List<String>): Int {
            val matrix = Matrix(input)
            val nodeMap = getNodes(matrix)
            val antinodes = mutableSetOf<Coord>()
            for (entry in nodeMap.entries.filter { it.value.size > 1 }) {
                for (node in entry.value) {
                    for (otherNode in entry.value.minus(node)) {
                        val dx = node.col - otherNode.col
                        val dy = node.row - otherNode.row
                        val diff = Diff(dx, dy)
                        var possibleAntinode = node
                        while (possibleAntinode.isWithin(matrix)) {
                            antinodes.add(possibleAntinode)
                            possibleAntinode = diff.toCoord(possibleAntinode)
                        }
                    }
                }
            }
            return antinodes.size
        }
    }

    fun getNodes(matrix: Matrix): Map<Char, Set<Coord>> {
        val nodeMap: MutableMap<Char, MutableSet<Coord>> = mutableMapOf()
        for (row in 0..<matrix.numRows) {
            for (col in 0..<matrix.numCols) {
                val node = matrix.getAt(row, col)
                if (node == '.') continue
                val set = nodeMap.computeIfAbsent(node) { mutableSetOf() }
                set.add(Coord(row, col))
            }
        }
        return nodeMap
    }

    data class Matrix(private val input: List<String>) {
        val numRows = input.size
        val numCols = input[0].length
        fun getAt(row: Int, col: Int): Char = input[row][col]
    }

    data class Coord(val row: Int, val col: Int) {
        fun isWithin(matrix: Matrix) =
            row >= 0 && row < matrix.numRows && col >= 0 && col < matrix.numCols
    }

    data class Diff(val dx: Int, val dy: Int) {
        fun toCoord(initialCoord: Coord) = Coord(initialCoord.row + dy, initialCoord.col + dx)
    }
}