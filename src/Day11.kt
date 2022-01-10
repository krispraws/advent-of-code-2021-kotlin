import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.sql.PseudoColumnUsage

class OctoGrid (input: List<String>) {
    init {
        require(input.isNotEmpty() && !input.any {it.isEmpty()}) { "Invalid input"}
    }
    val rows = input.size
    val columns = input.first().length
    val state = input.map { it.toCharArray().map { c -> Character.getNumericValue(c)} }.flatten().toIntArray()

    /**
     * Returns the current energy of the octopus at the given coordinates
     * or null if the coordinates are invalid
     */
    fun getEnergyOrNull(row: Int, column: Int) : Int? {
        if (row !in 0 until rows || column !in 0 until columns) {
            return null
        }
        return state[row*columns + column]
    }

    /**
     * Sets the energy of the octopus at the given coordinates
     * Returns false if the coordinates are invalid, else returns true
     */

    fun setEnergy(row: Int, column: Int, energy: Int) : Boolean {
        if (row !in 0 until rows || column !in 0 until columns) {
            return false
        }
        state[row*columns + column] = energy
        return true
    }

    fun increment(row: Int, column: Int) : Boolean {
        if (row !in 0 until rows || column !in 0 until columns) {
            return false
        }
        if (state[row*columns + column] < 0) {
            return false
        }
        state[row*columns + column]++
        return true
    }

    /**
     * Flash the octopus at the given coordinates
     */
    fun flash(row: Int, column: Int) : Boolean {
        if (row !in 0 until rows || column !in 0 until columns) {
            return false
        }
        if (state[row*columns + column] <= 9) {
            return false
        }
        // set energy of the flashed octopus to -1 to indicate it already flashed
        setEnergy(row, column, -1)

        // Increment all 8 neighbours including diagonal ones
        listOf(
            Pair(row - 1, column - 1), Pair(row - 1, column), Pair(row - 1, column + 1),
            Pair(row, column - 1), Pair(row, column + 1),
            Pair(row + 1, column - 1), Pair(row + 1, column), Pair(row + 1, column + 1),
        ).map { increment(it.first, it.second) }

        return true
    }

    fun fullyFlashed() = state.any { it > 0 }

    /**
     * Processes a step transition and updates the state
     * Returns number of flashes during the step
     */
    fun step() : Int {
        state.forEachIndexed { index, i -> state[index]++ }
        //prettyPrint()
        var flashed = false
        var numFlashes = 0
        do {
            flashed = false
            state.forEachIndexed { index, i ->
                if (i > 9) {
                    numFlashes++
                    flashed = flashed or flash(index / columns, index % columns)
                    //prettyPrint()
                }
            }
        } while (flashed)
        state.forEachIndexed { index, i ->
            if ( i < 0) {
                state[index] = 0
            }
        }
        //println("numFlashes = $numFlashes")
        return numFlashes
    }

    fun prettyPrint() : Unit {
        println()
        for (index in state.indices) {
            if (index % columns == 0) {
                print("\n")
            }
            print("%2d ".format(state[index]))
        }
    }
}

class Day11Impl {

    fun stepN(octoGrid: OctoGrid, steps: Int) : Int {
        var totalFlashes = 0
        for (i in 1 .. steps) {
            totalFlashes += octoGrid.step()
        }
        return totalFlashes
    }

    fun part1(input: List<String> ): Int {
        val octoGrid = OctoGrid(input)
        //octoGrid.prettyPrint()
        val totalFlashes = stepN(octoGrid, 100)
        //octoGrid.prettyPrint()
        return totalFlashes
    }

    fun part2(input: List<String>): Int {
        val octoGrid = OctoGrid(input)
        //octoGrid.prettyPrint()
        var step = 1
        while (true) {
            val numFlashes = octoGrid.step()
            if (numFlashes == octoGrid.rows * octoGrid.columns) {
                break
            }
            step++
        }
        //octoGrid.prettyPrint()
        return step
    }
}

class Day11Test {

    private val impl = Day11Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test 10 steps example`() {
        val input = listOf<String>(
            "5483143223",
            "2745854711",
            "5264556173",
            "6141336146",
            "6357385478",
            "4167524645",
            "2176841721",
            "6882881134",
            "4846848554",
            "5283751526"
        )
        val octoGrid = OctoGrid(input)
        octoGrid.prettyPrint()
        val totalFlashes = impl.stepN(octoGrid, 10)
        octoGrid.prettyPrint()
        Assertions.assertEquals(204, totalFlashes)
    }

    @Test
    fun `test part1 example`() {
        val input = listOf<String>(
            "5483143223",
            "2745854711",
            "5264556173",
            "6141336146",
            "6357385478",
            "4167524645",
            "2176841721",
            "6882881134",
            "4846848554",
            "5283751526"
        )
        Assertions.assertEquals(1656, impl.part1(input))
    }

    @Test
    fun `test part2 example`() {
        val input = listOf<String>(
            "5483143223",
            "2745854711",
            "5264556173",
            "6141336146",
            "6357385478",
            "4167524645",
            "2176841721",
            "6882881134",
            "4846848554",
            "5283751526"
        )
        Assertions.assertEquals(195, impl.part2(input))
    }

}

fun main() {
    with(Day11Impl()) {
        val input = File("src/Day11.txt").readLines()
        println("Part 1: ${part1(input)}")
        println("Part 2: ${part2(input)}")
    }
}
