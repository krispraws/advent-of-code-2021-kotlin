import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Math.max

/**
 * A HeightMap is a 2D grid where the values represent the height of a location
 * and the (row,column) indices represent the position relative to other locations.
 * Each location has 4 neighbouring locations (up, down, left, right)
 * unless it is on the edge.
 */
data class HeightMapPoint(val row: Int, val column: Int)

interface HeightMap {
    fun exists(point: HeightMapPoint) : Boolean
    fun getHeight(point: HeightMapPoint) : Int
    fun getNeighbours(point: HeightMapPoint) : List<HeightMapPoint>

    /**
     * indices of all points
     */
    fun indices() : List<HeightMapPoint>

    /**
     * A low point has a height lower than all its neighbours
     */
    fun isLowPoint(point: HeightMapPoint) : Boolean

    /**
     * A basin consists of all locations that flow down to a low point,
     * excluding locations with height = 9 and locations not part of another basin
     * Similar to floodfill
     */
    fun getBasin(point: HeightMapPoint, otherBasinPoints: Set<HeightMapPoint>) : List<HeightMapPoint>
}

abstract class AbstractHeightMap : HeightMap {

    override fun exists(point: HeightMapPoint) : Boolean {
        return runCatching {
            val h = getHeight(point)
        }.isSuccess
    }

    override fun getNeighbours(point: HeightMapPoint) : List<HeightMapPoint> {
        require(exists(point)) { "Point $point out of bounds"}
        // up, down, left, right
        return listOf(
            HeightMapPoint(point.row-1, point.column),
            HeightMapPoint(point.row+1, point.column),
            HeightMapPoint(point.row, point.column-1),
            HeightMapPoint(point.row, point.column+1)
        ).filter { exists(it) }
    }

    override fun isLowPoint(point: HeightMapPoint) : Boolean {
        require(exists(point)) { "Point $point out of bounds"}
        val height = getHeight(point)
        return !getNeighbours(point).map { getHeight(it) }.any { it <= height }
    }

    override fun getBasin(point: HeightMapPoint, otherBasinPoints: Set<HeightMapPoint>): List<HeightMapPoint> {
        if (!isLowPoint(point)) {
            return emptyList()
        }
        val basin = mutableListOf<HeightMapPoint>(point)

        val pointQueue = mutableListOf<HeightMapPoint>(point)
        while(pointQueue.isNotEmpty()) {
            val currPoint = pointQueue.removeAt(0)
            val height = getHeight(currPoint)
            val nextPoints = getNeighbours(currPoint).filter {
                val h = getHeight(it)
                h != 9 && h > height && it !in basin && it !in otherBasinPoints
            }
            basin.addAll(nextPoints)
            pointQueue.addAll(nextPoints)
            //println("pointQueue = $pointQueue")
            //println("basin = $basin")
        }
        return basin
    }

}

/**
 * M x N grid
 * All rows have same number of columns.
 * Number of rows can be different from number of columns.
 */
class SymmetricHeightMap (val columns: Int, private val _data: List<Int>) : AbstractHeightMap() {
    val rows = _data.size/columns

    override fun indices() : List<HeightMapPoint> {
        return (0 until rows).map { r -> (0 until columns).map { c -> HeightMapPoint(r,c) } }.flatten()
    }

    override fun getHeight(point: HeightMapPoint) : Int {
        require(point.row in 0 until rows && point.column in 0 until columns) { "Point $point out of bounds"}
        return _data[point.row*columns + point.column]
    }
}

/**
 * M rows
 * Each row can have a different number of columns.
 */
class AsymmetricHeightMap (val _data: List<List<Int>>) : AbstractHeightMap() {

    override fun indices() : List<HeightMapPoint> {
        return _data.mapIndexed{ rowIndex, row -> row.mapIndexed { colIndex, col -> HeightMapPoint(rowIndex, colIndex)} }.flatten()
    }

    override fun getHeight(point: HeightMapPoint) : Int {
        require(point.row in _data.indices && point.column in _data[point.row].indices) { "Point $point out of bounds"}
        return _data[point.row][point.column]
    }
}

private fun File.toSymmetricHeightMap(): HeightMap {
    val lines = readLines()
    return SymmetricHeightMap(
        lines.firstOrNull()?.length ?: 0,
                lines.flatMap { it.toCharArray().map { c -> Character.getNumericValue(c) } }.toList()
    )
}

private fun File.toAsymmetricHeightMap(): HeightMap {
    useLines { lines ->
        return AsymmetricHeightMap(
            lines.map { it.toCharArray().map { c -> Character.getNumericValue(c) } }.toList()
        )
    }
}

class Day09Impl {

    fun part1(heightMap: HeightMap): Int {
        return with(heightMap){
            indices()
                .filter { isLowPoint(it) }
                .sumOf { getHeight(it) + 1 }
        }
    }

    fun part2(heightMap: HeightMap): Int {
        val basinPoints = mutableSetOf<HeightMapPoint>()
        return with(heightMap){
            indices()
                .filter { p -> isLowPoint(p) }
                .map { lp ->
                    getBasin(lp, basinPoints).also {
                        //println(it)
                        basinPoints.addAll(it)
                    }.size
                }
                .also { println("basin sizes = $it") }
                .sortedDescending()
                .take(3)
                .fold(1) { acc, basinSize -> acc*basinSize}
        }
    }
}

class Day09Test {

    private val impl = Day09Impl()

    @BeforeEach
    internal fun setUp() {
    }

    @AfterEach
    internal fun tearDown() {
    }

    @Test
    fun `test part1 example symmetric`() {
        val heights = listOf(
            listOf(2,1,9,9,9,4,3,2,1,0),
            listOf(3,9,8,7,8,9,4,9,2,1),
            listOf(9,8,5,6,7,8,9,8,9,2),
            listOf(8,7,6,7,8,9,6,7,8,9),
            listOf(9,8,9,9,9,6,5,6,7,8)
            )
        val heightMap = SymmetricHeightMap(heights.first().size, heights.flatten())
        Assertions.assertEquals(15, impl.part1(heightMap))
    }

    @Test
    fun `test part1 example asymmetric`() {
        val heights = listOf(
            listOf(2,1,9,9,9,4,3,2,1,0),
            listOf(3,9,8,7,8,9,4,9,2,1),
            listOf(9,8,5,6,7,8,9,8,9,2),
            listOf(8,7,6,7,8,9,6,7,8,9),
            listOf(9,8,9,9,9,6,5,6,7,8)
        )
        val heightMap = AsymmetricHeightMap(heights)
        Assertions.assertEquals(15, impl.part1(heightMap))
    }

    @Test
    fun `test part2 example symmetric`() {
        val heights = listOf(
            listOf(2,1,9,9,9,4,3,2,1,0),
            listOf(3,9,8,7,8,9,4,9,2,1),
            listOf(9,8,5,6,7,8,9,8,9,2),
            listOf(8,7,6,7,8,9,6,7,8,9),
            listOf(9,8,9,9,9,6,5,6,7,8)
        )
        val heightMap = SymmetricHeightMap(heights.first().size, heights.flatten())
        Assertions.assertEquals(1134, impl.part2(heightMap))
    }

    @Test
    fun `test part2 example asymmetric`() {
        val heights = listOf(
            listOf(2,1,9,9,9,4,3,2,1,0),
            listOf(3,9,8,7,8,9,4,9,2,1),
            listOf(9,8,5,6,7,8,9,8,9,2),
            listOf(8,7,6,7,8,9,6,7,8,9),
            listOf(9,8,9,9,9,6,5,6,7,8)
        )
        val heightMap = AsymmetricHeightMap(heights)
        Assertions.assertEquals(1134, impl.part2(heightMap))
    }

}

fun main() {
    with(Day09Impl()) {
        val heights = File("src/Day09.txt").toSymmetricHeightMap()
        println("Part 1: ${part1(heights)}")
        println("Part 2: ${part2(heights)}")
    }
}
