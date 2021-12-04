import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class Day03 {

    // one counter for each bit position
    // each counter tracks how many readings had 1 in that position
    private fun createSetBitCounters(input: List<String>): List<Int> {
        if (input.isEmpty()) {
            return emptyList()
        }
        val readingLength = input.first().length
        val counters = MutableList(readingLength) { 0 }

        input.forEach { bstr ->
            bstr.forEachIndexed { index, c -> if (c == '1') counters[index]++ }
        }
        return counters
    }

    private fun part1UsingCounters(input: List<String>): Int {
        if (input.isEmpty()) {
            return 0
        }
        val numReadings = input.size
        val counters = createSetBitCounters(input)

        val gammaSb = StringBuilder()
        val epsilonSb = StringBuilder()
        counters.forEach {
            if (it > numReadings/2) {
                gammaSb.append('1')
                epsilonSb.append('0')
            } else {
                gammaSb.append('0')
                epsilonSb.append('1')
            }
        }

        val gamma = Integer.parseUnsignedInt(gammaSb.toString(), 2)
        val epsilon = Integer.parseUnsignedInt(epsilonSb.toString(), 2)
        println("gamma: $gammaSb $gamma")
        println("epsilon: $epsilonSb $epsilon")
        return gamma * epsilon
    }

    fun part1(input: List<String>): Int {
        return part1UsingCounters(input)
    }

    private fun findMCB(countOfOnes: Int, countOfZeroes: Int, tieBreakWinner: Char): Char {
        return when {
            countOfOnes > countOfZeroes -> '1'
            (countOfOnes == countOfZeroes) -> tieBreakWinner
            else -> '0'
        }
    }

    private fun oxygenCriteria(countOfOnes: Int, sampleCount: Int): Char
        = findMCB(countOfOnes, sampleCount - countOfOnes, '1')

    private fun co2Criteria(countOfOnes: Int, sampleCount: Int): Char
        = findMCB(sampleCount-countOfOnes, countOfOnes, '0')

    private fun findRating(
        input: List<String>,
        criteria: (countOfOnes: Int, sampleCount: Int) -> Char)
    : Int {
        var validReadings = input
        var bitIndex = 0
        while ((validReadings.size > 1)) {
            val countOfOnes = validReadings.count { r -> r[bitIndex] == '1' }
            validReadings = validReadings.filter { r ->
                r[bitIndex] == criteria(countOfOnes, validReadings.size)
            }
            bitIndex += 1
        }
        return when (validReadings.size) {
            1 -> Integer.parseInt(validReadings.first(), 2)
            else -> 0
        }
    }

    fun part2(input: List<String>): Int {
        if (input.isEmpty()) {
            return 0
        }
        val oxygen = findRating(input, ::oxygenCriteria)
        val co2 = findRating(input, ::co2Criteria)
        println("oxygen: $oxygen")
        println("co2: $co2")
        return oxygen * co2
    }
}

class Day03Test {

    private val impl = Day03()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 empty input`() {
        Assertions.assertEquals(impl.part1(emptyList()), 0)
    }

    @Test
    fun `test part1 basic`() {
        val input = listOf("0100", "0001", "0011")
        Assertions.assertEquals(impl.part1(input), 14)
    }

    @Test
    fun `test part1 example`() {
        val input = listOf("00100", "11110", "10110", "10111", "10101", "01111", "00111", "11100", "10000", "11001", "00010", "01010")
        Assertions.assertEquals(impl.part1(input), 198)
    }

    @Test
    fun `test part2 empty input`() {
        Assertions.assertEquals(impl.part2(emptyList()), 0)
    }

    @Test
    fun `test part2 basic`() {
        val input = listOf("0100", "0001", "0011")
        //oxygen == 3
        //co2 == 0 : everything gets filtered out in first bit
        Assertions.assertEquals(impl.part2(input), 0)
    }

    @Test
    fun `test part2 example`() {
        val input = listOf("00100", "11110", "10110", "10111", "10101", "01111", "00111", "11100", "10000", "11001", "00010", "01010")
        Assertions.assertEquals(impl.part2(input), 230)
    }

}

fun main() {
    with(Day03()) {
        File("src/Day03.txt").readLines().run {
            println("Part 1: ${part1(this)}")
            println("Part 2: ${part2(this)}")
        }
    }
}
