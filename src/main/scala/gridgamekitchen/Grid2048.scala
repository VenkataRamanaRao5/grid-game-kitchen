package gridgamekitchen

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import scala.util.Random
import scala.annotation.static

@JSExportAll
@JSExportTopLevel("Grid2048")
class GameGrid extends RookGrid[Int]:
    val nrows = 4
    val ncols = 4
    override val emptyData = 0
    type SquareType = RookSquare

    override val grid: IndexedSeq[IndexedSeq[SquareType]] = IndexedSeq.tabulate(nrows, ncols)((rowi, coli) => 
        new RookSquare(){
            override val row = rowi
            override val col = coli
        }
    )
    class NumberBlock(val sq: Square, val number: Int = emptyData) extends Block(number, sq):
        override def updationFunction(newData: Int, oldData: Int) = oldData * 2

    private def pullFrom(square: SquareType, dir: Directions): Unit = 
        square.nonEmpty(dir) match 
            case Some(nextSquare) => 
                val next = (square.block, nextSquare.block) match
                    case (Some(thisBlock), Some(nextBlock)) => 
                        println((dir, square, nextSquare))
                        if thisBlock.data == nextBlock.data then
                            removeBlock(thisBlock)
                            nextSquare.moveTo(square)
                            nextBlock.updateData(0)
                            square
                        else
                            val temp = square.neighbour(dir).get
                            nextSquare.moveTo(temp)
                            temp
                    case _ => 
                        nextSquare.moveTo(square)
                        square
                pullFrom(next, dir)
            case None => ()
        

    def moveGrid(move: Directions): Unit = 
        val (frontier, opposite) = move match
            case Left => (grid.map(_.head), Right)
            case Right => (grid.map(_.last), Left)
            case Up => (grid.head, Down)
            case Down => (grid.last, Up)
            case _ => ???

        frontier.foreach(sq => pullFrom(sq, opposite))

    def placeRandom(): Unit = 
        empties match
            case IndexedSeq() => ()
            case empties =>
                val index = Random.nextInt(empties.length)
                val (x, y) = empties(index)
                placeAtGrid(x, y, if Random.nextFloat() < 0.9 then 2 else 4)

    override def placeAt(x: Int, y: Int, data: Int): Unit = grid(x)(y).block = Some(new NumberBlock(grid(x)(y), data))
    
    def init(): Unit = 
        placeRandom()
        placeRandom()

    def clear(): Unit = 
        grid.foreach(row => row.foreach(sq => sq.block = None))

    
        
