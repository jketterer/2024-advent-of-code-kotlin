import Day09.Part1
import Day09.Part2

// https://adventofcode.com/2024/day/9
fun main() {
    val input = readInput(2024, 9)
    println("Part 1: ${Part1.getFilesystemChecksum(input)}")
    println("Part 2: ${Part2.getFilesystemChecksum(input)}")
}

private object Day09 {
    object Part1 {
        fun getFilesystemChecksum(input: List<String>): Long {
            val diskMap = input[0]
            val files = parseFileRefs(diskMap)
            var nextFreeSpaceIdx = files.indexOfFirst { it.id == null }
            var lastFileBlockIdx = files.indexOfLast { it.id != null }
            while (nextFreeSpaceIdx < lastFileBlockIdx) {
                val nextFreeSpace = files[nextFreeSpaceIdx]
                val lastFile = files[lastFileBlockIdx]
                val diff = nextFreeSpace.length - lastFile.length
                if (diff == 0) {
                    files[nextFreeSpaceIdx] = lastFile
                    files.removeAt(lastFileBlockIdx)
                } else if (diff >= 0) {
                    val (remainder, _) = splitFile(nextFreeSpace, diff)
                    files.removeAt(nextFreeSpaceIdx)
                    files.add(nextFreeSpaceIdx, remainder)
                    files.add(nextFreeSpaceIdx, lastFile)
                    files.removeAt(lastFileBlockIdx + 1)
                } else {
                    val (remainder, toBeRemoved) = splitFile(lastFile, diff * -1)
                    files[lastFileBlockIdx] = remainder
                    files.removeAt(nextFreeSpaceIdx)
                    files.add(nextFreeSpaceIdx, toBeRemoved)
                }

                nextFreeSpaceIdx = files.indexOfFirst { it.id == null }
                lastFileBlockIdx = files.indexOfLast { it.id != null }
            }

            return calculateChecksum(files)
        }

        fun calculateChecksum(files: List<FileRef>): Long {
            val toBeCalculated = files.filter { it.id != null && it.length > 0 }.toMutableList()
            var position = 0
            var filePosition = 0
            var total = 0L
            while (toBeCalculated.isNotEmpty()) {
                val currentFile = toBeCalculated[0]
                total += (position * currentFile.id!!)
                position++
                filePosition++
                if (filePosition == currentFile.length) {
                    toBeCalculated.removeAt(0)
                    filePosition = 0
                }
            }
            return total
        }
    }

    object Part2 {
        fun getFilesystemChecksum(input: List<String>): Long {
            val diskMap = input[0]
            val files = parseFileRefs(diskMap)
            val occupiedFiles = files.filterNot { it.id == null }
            for (currentFile in occupiedFiles.reversed()) {
                val currentIndex = files.indexOf(currentFile)
                val nextFreeSpaceIdx = files.findNextFreeSpace(currentFile.length)
                if (nextFreeSpaceIdx != null && nextFreeSpaceIdx < currentIndex) {
                    val nextFreeSpace = files[nextFreeSpaceIdx]
                    val diff = nextFreeSpace.length - currentFile.length
                    val (remainder, toBeRemoved) = splitFile(nextFreeSpace, diff)
                    files.removeAt(nextFreeSpaceIdx)
                    files.add(nextFreeSpaceIdx, remainder)
                    files.add(nextFreeSpaceIdx, currentFile)
                    files[currentIndex + 1] = toBeRemoved
                }
            }

            return calculateChecksum(files)
        }

        fun calculateChecksum(files: List<FileRef>): Long {
            val blocks = files.filterNot { it.length == 0 }.flatMap { file ->
                buildList {
                    for (i in 0..<file.length) {
                        add(file.id)
                    }
                }
            }

            var total = 0L
            for (i in blocks.indices) {
                val id = blocks[i] ?: 0
                total += id * i
            }
            return total
        }
    }

    /**
     * Return the index of the next available free space with a minimum size of [minimumSize]. Return
     * null if none are available.
     */
    fun List<FileRef>.findNextFreeSpace(minimumSize: Int): Int? {
        val nextSpace = filter { it.id == null }.firstOrNull { it.length >= minimumSize }
        return nextSpace?.let { indexOf(it) }
    }

    fun parseFileRefs(diskMap: String): MutableList<FileRef> {
        var fileId = -1
        return diskMap.mapIndexed { i, c ->
            val id = if (i % 2 == 0) {
                fileId++
                fileId
            } else null
            FileRef(id, c.toString().toInt())
        }
            .filterNot { it.length == 0 }
            .toMutableList()
    }

    fun splitFile(file: FileRef, newSize: Int): Pair<FileRef, FileRef> {
        val first = file.copy(length = newSize)
        val last = file.copy(length = file.length - newSize)
        return Pair(first, last)
    }

    // null id is free space
    data class FileRef(val id: Int?, val length: Int)
}