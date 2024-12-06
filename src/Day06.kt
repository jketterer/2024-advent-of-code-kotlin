import Day06.Part1
import Day06.Part2

// https://adventofcode.com/2024/day/6
fun main() {
    val input = readInput(2024, 6)
    println("Part 1: ${Part1.countPositions(input)}")
    println("Part 2: ${Part2.getPossibleObstructions(input)}")
}

private object Day06 {
    object Part1 {
        fun countPositions(input: List<String>): Int {
            var position = getGuardPosition(input)
            val positionSet: MutableSet<Pair<Int, Int>> = mutableSetOf(Pair(position.x, position.y))
            while (!isAtEdge(position, input)) {
                var nextPos = position.moveForward()
                while (input[nextPos.y][nextPos.x] == '#') {
                    nextPos = position.turn().moveForward()
                }
                position = nextPos
                positionSet.add(Pair(position.x, position.y))
            }
            return positionSet.size
        }

        fun isAtEdge(position: GuardPosition, map: List<String>): Boolean {
            return when (position.direction) {
                Direction.UP -> position.y == 0
                Direction.RIGHT -> position.x == (map[0].length - 1)
                Direction.DOWN -> position.y == (map.size - 1)
                Direction.LEFT -> position.x == 0
            }
        }

        fun getGuardPosition(input: List<String>): GuardPosition {
            for (y in input.indices) {
                val row = input[y]
                for (x in row.indices) {
                    if (Direction.entries.any { it.symbol == row[x] }) {
                        return GuardPosition(x, y, Direction.entries.first { it.symbol == row[x] })
                    }
                }
            }
            println("something went wrong")
            return GuardPosition(-1, -1, Direction.DOWN)
        }
    }

    object Part2 {
        fun getPossibleObstructions(input: List<String>): Int {
            // forgive my sins, honestly couldn't puzzle this one out without the nastiest brute force of my life
            val position = Part1.getGuardPosition(input)
            var count = 0
            for (y in input.indices) {
                val row = input[y]
                for (x in row.indices) {
                    if (row[x] != '#' && Direction.entries.none { it.symbol == row[x] }) {
                        val modifiedRow = row.replaceRange(x, x + 1, "#")
                        val modifiedList = input.toMutableList()
                        modifiedList[y] = modifiedRow
                        if (checkForLoop(modifiedList.toList())) {
                            count++
                        }
                    }
                }
            }
            return count
        }

        fun checkForLoop(input: List<String>): Boolean {
            var position = Part1.getGuardPosition(input)
            val positionSet = mutableSetOf<GuardPosition>()
            while (!Part1.isAtEdge(position, input) && !positionSet.contains(position)) {
                positionSet.add(position)
                var nextPos = position.moveForward()
                while (input[nextPos.y][nextPos.x] == '#') {
                    position = position.turn()
                    nextPos = position.moveForward()
                    if (Part1.isAtEdge(nextPos, input)) break
                }
                position = nextPos
            }
            return !Part1.isAtEdge(position, input)
        }
    }

    data class GuardPosition(val x: Int, val y: Int, val direction: Direction) {
        fun moveForward() = when (direction) {
            Direction.UP -> copy(x = x, y = y - 1, direction = direction)
            Direction.RIGHT -> copy(x = x + 1, y = y, direction = direction)
            Direction.DOWN -> copy(x = x, y = y + 1, direction = direction)
            Direction.LEFT -> copy(x = x - 1, y = y, direction = direction)
        }

        fun turn() = when (direction) {
            Direction.UP -> copy(direction = Direction.RIGHT)
            Direction.RIGHT -> copy(direction = Direction.DOWN)
            Direction.DOWN -> copy(direction = Direction.LEFT)
            Direction.LEFT -> copy(direction = Direction.UP)
        }
    }

    enum class Direction(val symbol: Char) {
        UP('^'),
        RIGHT('>'),
        DOWN('v'),
        LEFT('<');
    }
}