import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

private fun File.toInput(): List<String> = readLines()

class Day08Impl {

    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

}

class Day08Test {

    private val impl = Day08Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 example`() {
    }

    @Test
    fun `test part2 example`() {
    }

}

fun main() {
    with(Day08Impl()) {
        val input = File("src/Day08.txt").toInput()
        println("Part 1: ${part1(input)}")
        println("Part 2: ${part2(input)}")
    }
}
