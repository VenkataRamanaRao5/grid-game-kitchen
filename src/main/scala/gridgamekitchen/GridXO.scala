package gridgamekitchen
/*
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import scala.scalajs.js

@JSExportAll
@JSExportTopLevel("GridXO")
class TicTacToe extends QueenGrid[Char]:
    val nrows = 3
    val ncols = 3
    type SquareType = QueenSquare
    type BlockType = XOBlock

    val grid: IndexedSeq[IndexedSeq[SquareType]] = IndexedSeq.tabulate(nrows, ncols)((rowi, coli) => 
        new QueenSquare(){
            override val row = rowi
            override val col = coli
        }
    )
    
    val emptyData = ' '

    class XOBlock(val sq: Square, val symbol: Char = emptyData) extends Block(symbol, sq):
        override def updationFunction(newch: Char, oldch: Char) = newch

    override def placeAt(row: Int, col: Int, ch: Char): Unit = 
        grid(row)(col).block = Some(new XOBlock(grid(row)(col), ch))

    def init(): Unit = 
        clear()

    def className(data: Char): String = data.toString()

    override val gridListeners = {}.asInstanceOf[js.Dictionary[js.Function]]
    override val squareListeners = {}.asInstanceOf[js.Dictionary[js.Function]]
    override val blockListeners = {}.asInstanceOf[js.Dictionary[js.Function]]
    override val functions = {}.asInstanceOf[js.Dictionary[js.Dynamic]]
    override val variables = {}.asInstanceOf[js.Dictionary[js.Any]]


    def checkWinAt(square: QueenSquare): Boolean = 
        val ch = square.block.get.data
        square.thisRow.forall(_.block.forall(_.data == ch)) || 
        square.thisColumn.forall(_.block.forall(_.data == ch)) ||
        square.backSlashDiagonal.forall(_.block.forall(_.data == ch)) ||
        square.forwardSlashDiagonal.forall(_.block.forall(_.data == ch))

    def ply(square: QueenSquare, ch: Char): Boolean =
        placeBySquare(square, ch)
        checkWinAt(square)
*/