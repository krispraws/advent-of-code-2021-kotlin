import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

private fun chunkByEmptyLines(lines: Sequence<String>): Sequence<List<String>> = sequence {
    val currentChunk = mutableListOf<String>()
    for (line in lines) {
        if (line.isEmpty() || line.isBlank()) {
            yield(currentChunk.toList())
            currentChunk.clear()
        } else {
            currentChunk += line
        }
    }
    yield(currentChunk)
}

private fun readBingoInput(fileName: String) : Pair<List<Int>, List<BingoBoard>> {
    return File(fileName).useLines { lines ->
        var firstLine = false
        var calledNumbers = mutableListOf<Int>()
        var boards = mutableListOf<BingoBoard>()
        for (chunk in chunkByEmptyLines(lines)) {
            // println("chunk: $chunk")
            if (!firstLine) {
                check(chunk.size == 1) { "First line must be list of called numbers" }
                firstLine = true
                calledNumbers.addAll(chunk.first().trim().split(",").map { s -> s.toInt() })
            } else {
                val rx = Regex("\\s+")
                val boardInput = chunk.map {
                    it.trim().split(rx).map { numStr -> numStr.toInt() }
                }
                val boardSize = chunk.size
                boardInput.forEach {
                    check(it.size == boardSize) { "All rows for a board must have $boardSize elements"}
                }
                boards.add(BingoBoard(boardSize, boardInput.flatten()))
            }
        }
        calledNumbers to boards
    }
}

class BingoBoard (val size: Int, nums: List<Int> ) {
    private data class BoardSlot (val num: Int, var marked: Boolean)
    private var slots = nums.map { BoardSlot(it, false) }.toMutableList()

    data class BoardStatus (
        val winner: Boolean,
        val winningScore: Int,
        )
    var status = BoardStatus(false, 0)
        private set

    fun mark(num: Int): BoardStatus {
        var marked = false
        var winner = false
        for (index in 0 until slots.size) {
            if (slots[index].num == num) {
                marked = true
                slots[index].marked = true
                if (status.winner) {
                    continue
                }
                // (index / size) is the row index, (index % size) is the column index
                val rowStart = size * (index/size)
                val unmarkedInRow = slots.slice(rowStart until (rowStart + size)).count { !it.marked }
                val unmarkedInCol = slots.slice((index % size) until slots.size step size).count { !it.marked }
                if (unmarkedInRow == 0 || unmarkedInCol == 0) {
                    winner = true
                    break
                }
            }
        }
        var winningScore = 0
        if (winner) {
            // Freeze winning score at the point the board won
            winningScore = num * slots.filter { !it.marked }.sumOf {it.num}
        }
        if (winner && winner != status.winner) {
            status = BoardStatus(winner, winningScore)
        }
        return status
    }

    override fun toString(): String {
        return "BingoBoard(size=$size, slots=$slots, status=$status)"
    }

    fun toPrettyPrintString(): String {
        val sb = StringBuilder()
        sb.append("\n".padEnd(30, '-'))
        sb.append("\nBoard Size %d Winner %s Score %d".format(size, status.winner, status.winningScore))
        for (i in 0 until slots.size) {
            if (i % size == 0) {
                sb.append("\n")
            }
            if (slots[i].marked) {
                sb.append("%3d* ".format(slots[i].num))
            } else {
                sb.append("%3d  ".format(slots[i].num))
            }
        }
        sb.append("\n".padEnd(30, '-'))
        return sb.toString()
    }

}
class Day04 {

    fun part1(calledNumbers: List<Int>, boards: List<BingoBoard>): Int {
        for (num in calledNumbers) {
            val maxWinner = boards.mapIndexed { bIndex, b -> bIndex to b.mark(num) }.filter { it.second.winner }.maxByOrNull { it.second.winningScore }
            // println("called: $num")
            boards.forEachIndexed { index, bingoBoard -> println("board $index: ${bingoBoard.toPrettyPrintString()}") }
            // println("winner: $maxWinner")
            if (maxWinner != null) {
                return maxWinner.second.winningScore
            }
        }
        return 0
    }

    /*
        The implementation currently continues to mark boards that have already won with new numbers called.
        However the winning score for boards is only computed when they first win.
        This can be optimized further by not considering already won boards for future numbers,
        and/or making `mark` a no-op for already won boards.
     */
    fun part2(calledNumbers: List<Int>, boards: List<BingoBoard>): Int {
        for (num in calledNumbers) {
            val newWinners = boards.mapIndexed { bIndex, b ->
                val oldStatus = b.status.winner
                Triple(bIndex, oldStatus, b.mark(num))
            }.filter { it.third.winner && (it.second != it.third.winner)}
            // println("called: $num")
            // boards.forEachIndexed { index, bingoBoard -> println("board $index: ${bingoBoard.toPrettyPrintString()}") }
            // println("new winners: $newWinners")
            // any boards left to win?
            if (newWinners.isNotEmpty() && !boards.any { !it.status.winner} ) {
                return newWinners.last().third.winningScore
            }
        }
        return 0
    }
}

class Day04Test {

    private val impl = Day04()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }


    @Test
    fun `test part1 example`() {
        val calledNumbers = listOf(7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1)
        val bingoBoards = mutableListOf<BingoBoard>()
        val board1 = BingoBoard(5, listOf(22, 13, 17, 11, 0, 8, 2, 23, 4, 24, 21, 9, 14, 16, 7, 6, 10, 3, 18, 5, 1, 12, 20, 15, 19))
        bingoBoards.add(board1)
        val board2 = BingoBoard(5, listOf(3, 15, 0, 2, 22, 9, 18, 13, 17, 5, 19, 8, 7, 25, 23, 20, 11, 10, 24, 4, 14, 21, 16, 12, 6))
        bingoBoards.add(board2)
        val board3 = BingoBoard(5, listOf(14, 21, 17, 24, 4, 10, 16, 15, 9, 19, 18, 8, 23, 26, 20, 22, 11, 13, 6, 5, 2, 0, 12, 3, 7))
        bingoBoards.add(board3)

        Assertions.assertEquals(4512, impl.part1(calledNumbers, bingoBoards))
    }

    @Test
    fun `test part2 example`() {
        val calledNumbers = listOf(7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1)
        val bingoBoards = mutableListOf<BingoBoard>()
        val board1 = BingoBoard(5, listOf(22, 13, 17, 11, 0, 8, 2, 23, 4, 24, 21, 9, 14, 16, 7, 6, 10, 3, 18, 5, 1, 12, 20, 15, 19))
        bingoBoards.add(board1)
        val board2 = BingoBoard(5, listOf(3, 15, 0, 2, 22, 9, 18, 13, 17, 5, 19, 8, 7, 25, 23, 20, 11, 10, 24, 4, 14, 21, 16, 12, 6))
        bingoBoards.add(board2)
        val board3 = BingoBoard(5, listOf(14, 21, 17, 24, 4, 10, 16, 15, 9, 19, 18, 8, 23, 26, 20, 22, 11, 13, 6, 5, 2, 0, 12, 3, 7))
        bingoBoards.add(board3)

        Assertions.assertEquals(1924, impl.part2(calledNumbers, bingoBoards))
    }

}

fun main() {
    with(Day04()) {
        val (calledNumbers, bingoBoards) = readBingoInput("src/Day04.txt")
        println("Part 1: ${part1(calledNumbers, bingoBoards)}")
        println("Part 2: ${part2(calledNumbers, bingoBoards)}")
    }
}
