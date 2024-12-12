// https://adventofcode.com/2024/day/12
fun main() {
    val input = readInput(2024, 12)
    val testInput = listOf(
        "RRRRIICCFF",
        "RRRRIICCCF",
        "VVRRRCCFFF",
        "VVRCCCJFFF",
        "VVVVCJJCFE",
        "VVIVCCJJEE",
        "VVIIICJJEE",
        "MIIIIIJJEE",
        "MIIISIJEEE",
        "MMMISSJEEE",
    )
    println("Part 1: ${Day12.Part1.calculateTotalFencePrice(input)}")
}

private object Day12 {
    object Part1 {
        fun calculateTotalFencePrice(input: List<String>): Int {
            val matrix = Matrix(input)
            matrix.regions.map { it.getSubregions(matrix) }
                .flatten()
                .forEach {
                    kotlin.io.println(
                        "Region ${it.plant}: ${it.area} ${
                            it.perimeter(
                                matrix
                            )
                        } ${it.getSubregions(matrix).size}"
                    )
                }
            return matrix.regions.map { it.getSubregions(matrix) }.flatten()
                .sumOf { it.area * it.perimeter(matrix) }
        }
    }

    object Part2 {

    }

    data class Matrix(private val input: List<String>) {
        val numRows = input.size
        val numCols = input[0].length

        fun getAt(x: Int, y: Int): Plot? {
            if (x < 0 || x >= numCols || y < 0 || y >= numRows) return null
            val plant = input[y][x].toString()
            return Plot(x, y, plant)
        }

        fun lookAhead(plot: Plot, direction: Direction): Plot? {
            return when (direction) {
                Direction.NORTH -> getAt(plot.x, plot.y + 1)
                Direction.EAST -> getAt(plot.x + 1, plot.y)
                Direction.SOUTH -> getAt(plot.x, plot.y - 1)
                Direction.WEST -> getAt(plot.x - 1, plot.y)
            }
        }

        val regions: List<Region> by lazy {
            val plotMap = mutableMapOf<String, MutableList<Plot>>()
            for (y in 0..<numRows) {
                for (x in 0..<numCols) {
                    val plot = getAt(x, y)
                    val plotList = plotMap.computeIfAbsent(plot!!.plant) { mutableListOf() }
                    plotList.add(plot)
                }
            }
            plotMap.values.map { Region(it) }
        }
    }

    data class Plot(val x: Int, val y: Int, val plant: String) {
        fun isTouching(other: Plot): Boolean {
            return when {
                other.x == x && other.y in y - 1..y + 1 -> true
                other.y == y && other.x in x - 1..x + 1 -> true
                else -> false
            }
        }
    }

    data class Region(val plots: List<Plot>) {
        val plant = plots.first().plant
        val area = plots.size
        fun perimeter(matrix: Matrix) = plots.sumOf {
            val north = matrix.lookAhead(it, Direction.NORTH)
            val east = matrix.lookAhead(it, Direction.EAST)
            val south = matrix.lookAhead(it, Direction.SOUTH)
            val west = matrix.lookAhead(it, Direction.WEST)
            val borders = listOf(north, east, south, west)
            borders.map { border ->
                if (border == null || border.plant != it.plant) {
                    1
                } else {
                    0
                }
            }.sum()
        }

        fun getSubregions(matrix: Matrix): List<Region> {
            var totalSubregions = 1
            val subregionMap = mutableMapOf<Int, MutableSet<Plot>>()
            val remainingPlots = plots.toMutableList()
            while (remainingPlots.size > 0) {
                val curr = remainingPlots.first()
                val currentSubregion =
                    subregionMap.computeIfAbsent(totalSubregions) { mutableSetOf(curr) }
                if (subregionMap.values.flatten().none { it.isTouching(curr) }) {
                    totalSubregions++
                } else {
                    currentSubregion.add(curr)
                    remainingPlots.removeFirst()
                }
//                if (remainingPlots.any { it.isTouching(curr) }) {
                remainingPlots.sortByDescending { it.isTouching(curr) }
//                } else {
//                    remainingPlots.sortBy { it.y * 10 + it.x }
//                }
            }
            return subregionMap.values.map { Region(it.toList()) }
        }
    }

    enum class Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST,
    }
}