import Day05.Part1
import Day05.Part2

// https://adventofcode.com/2024/day/5
fun main() {
    val input = readInput(2024, 5)
    println("Part 1: ${Part1.getCorrectUpdateSum(input)}")
    println("Part 2: ${Part2.getFixedUpdateSum(input)}")
}

private object Day05 {
    object Part1 {
        fun getCorrectUpdateSum(input: List<String>): Int {
            val (ruleList, updateList) = splitInputs(input)
            val orderRules = ruleList.map(::parseOrderRule)
            val updates = updateList.map(::parseUpdateList)

            return updates.sumOf { update ->
                if (isUpdateOrderCorrect(update, orderRules)) update.middle()
                else 0
            }
        }

        fun isUpdateOrderCorrect(update: List<Int>, rules: List<PageOrderRule>): Boolean {
            for (i in update.indices) {
                val pageNum = update[i]
                val pagesToCheck = update.subList(i + 1, update.size)
                // if there isn't a rule putting the current page before all the other pages, the order is incorrect
                if (!pagesToCheck.all { rules.contains(PageOrderRule(pageNum, it)) })
                    return false
            }
            return true
        }
    }

    object Part2 {
        fun getFixedUpdateSum(input: List<String>): Int {
            val (ruleList, updateList) = splitInputs(input)
            val orderRules = ruleList.map(::parseOrderRule)
            val updates = updateList.map(::parseUpdateList)

            val incorrectUpdates = updates.filterNot { Part1.isUpdateOrderCorrect(it, orderRules) }
            return incorrectUpdates
                .map { fixUpdateOrder(it, orderRules) }
                .sumOf { it.middle() }
        }

        fun fixUpdateOrder(update: List<Int>, rules: List<PageOrderRule>): List<Int> {
            val toBeSorted = update.toMutableList()
            val correctedUpdate = mutableListOf<Int>()
            var index = 0
            // search for the last page by finding rules putting it behind every other page, then
            // remove it from the list and start over
            while (toBeSorted.isNotEmpty()) {
                val current = toBeSorted[index]
                val others = toBeSorted.minus(current)
                if (others.all { rules.contains(PageOrderRule(it, current)) }) {
                    correctedUpdate.add(0, current)
                    toBeSorted.remove(current)
                    index = 0
                } else {
                    index++
                }
            }
            return correctedUpdate.toList()
        }
    }

    fun splitInputs(input: List<String>): Pair<List<String>, List<String>> {
        val splitIndex = input.indexOf("")
        val ruleList = input.subList(0, splitIndex)
        val updateList = input.subList(splitIndex + 1, input.size)
        return Pair(ruleList, updateList)
    }

    fun parseOrderRule(line: String): PageOrderRule {
        val pageNums = line.split("|")
        return PageOrderRule(pageNums.first().toInt(), pageNums.last().toInt())
    }

    fun parseUpdateList(line: String): List<Int> {
        val pageNums = line.split(",")
        return pageNums.map { it.toInt() }
    }

    fun List<Int>.middle() = this[this.size / 2]

    data class PageOrderRule(val before: Int, val after: Int)
}