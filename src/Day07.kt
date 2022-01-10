import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

private fun File.toCrabPositions(): List<Int> {
    val line = readText()
    if (line.isNotEmpty() && line.isNotBlank()) {
        return line.trim().split(",").map { xy -> xy.trim().toInt() }
    }
    return emptyList()
}

// This is just brute-forcing :(
class Day07Impl {

    fun part1(crabPositions: List<Int>) = (crabPositions.minOf {it} .. crabPositions.maxOf { it }).map { p ->
        crabPositions.map { p1 -> kotlin.math.abs(p1 - p) }.sum()
    }.minOf { it }

    fun part2(crabPositions: List<Int>) = (crabPositions.minOf {it} .. crabPositions.maxOf { it }).map { p ->
        crabPositions.map { p1 ->
            val n = kotlin.math.abs(p1 - p)
            n * (n+1)/2
        }.also { /*println("$p: $it")*/ }.sum()
    }.also { /*println(it)*/ }.minOf { it }
}

class Day07Test {

    private val impl = Day07Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 example`() {
        val crabPositions = listOf(16,1,2,0,4,2,7,1,2,14)
        Assertions.assertEquals(37, impl.part1(crabPositions))
    }

    @Test
    fun `test part2 example`() {
        val crabPositions = listOf(16,1,2,0,4,2,7,1,2,14)
        Assertions.assertEquals(168, impl.part2(crabPositions))
    }

}

fun main() {
    with(Day07Impl()) {
        val crabPositions = File("src/Day07.txt").toCrabPositions()
        println("Part 1: ${part1(crabPositions)}")
        println("Part 2: ${part2(crabPositions)}")
    }
}
