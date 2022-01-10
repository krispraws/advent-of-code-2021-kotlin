import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

private fun File.toFishes(): List<Int> {
    val line = readText()
    if (line.isNotEmpty() && line.isNotBlank()) {
        return line.trim().split(",").map { xy -> xy.trim().toInt() }
    }
    return emptyList()
}

private fun LongArray.print(): String {
    val sb = StringBuilder()
    for (item in this) {
        sb.append("$item, ")
    }
    return sb.toString()
}

class Day06Impl {

    // needs more heap space, very slow
    private fun spawnFishNaive(initialFishes: List<Int>, numDays: Int): Int {
        val currentFishes = initialFishes.toMutableList()
        //println("day 0: ${currentFishes.size} fishes: $currentFishes")
        for (day in 1 .. numDays) {
            val currSize = currentFishes.size
            for (i in 0 until currSize) {
                if (currentFishes[i] == 0) {
                    currentFishes[i] = 6
                    currentFishes.add(8)
                } else {
                    currentFishes[i] -= 1
                }
            }
            //println("day $day: ${currentFishes.size} fishes: $currentFishes")
        }
        return currentFishes.size
    }

    private fun spawnFish(initialFishes: List<Int>, numDays: Int): Long {
        //println("Starting state: $initialFishes")
        // There are only 9 valid timer values 0 to 8
        // Just store count of fishes for each timer
        val currentFishes = LongArray(9) { 0L }
        for (f in initialFishes) {
            currentFishes[f] += 1L
        }
        //println("day 0: fishes: ${currentFishes.print()}")
        for (day in 1 .. numDays) {
            val zeroTimers = currentFishes[0]
            for (timer in 0 until currentFishes.size - 1) {
                currentFishes[timer] = currentFishes[timer + 1]
            }
            currentFishes[currentFishes.size - 1] = zeroTimers
            currentFishes[6] += zeroTimers
            //println("day $day: fishes: ${currentFishes.print()}")
        }
        return currentFishes.sum()
    }

    fun part1(initialFishes: List<Int>) = spawnFish(initialFishes, 80)

    fun part2(initialFishes: List<Int>) = spawnFish(initialFishes, 256)
}

class Day06Test {

    private val impl = Day06Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 example`() {
        val fishes = listOf(3,4,3,1,2)
        Assertions.assertEquals(5934L, impl.part1(fishes))
    }

    @Test
    fun `test part2 example`() {
        val fishes = listOf(3,4,3,1,2)
        Assertions.assertEquals(26984457539L, impl.part2(fishes))
    }

}

fun main() {
    with(Day06Impl()) {
        val fishes = File("src/Day06.txt").toFishes()
        println("Part 1: ${part1(fishes)}")
        println("Part 2: ${part2(fishes)}")
    }
}
