package gridgamekitchen

import scala.scalajs.js
import org.scalajs.dom
import com.raquo.laminar.api.L.{Var, Signal}
import com.raquo.airstream.ownership.Owner
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel, JSExport}
import js.JSConverters._
import com.raquo.airstream.ownership.Subscription

@JSExportAll
class JSVar[T](var0: Var[T]):
    def now(): T = var0.now()
    def set(value: T): Unit = var0.set(value)
    def update(f: js.Function1[T, T]): Unit = var0.update(f)
    def signal: JSSignal[T] = JSSignal(var0.signal)

@JSExportAll
class JSSignal[T](signal: Signal[T]):
    def forEach(f: js.Function1[T, Unit]): js.Function0[Unit] = {
        val subs = signal.foreach(f)
        () => subs.kill()
    }
    def map[U](f: js.Function1[T, U]): JSSignal[U] = 
        JSSignal(signal.map(f))

trait Grid[Data]:
    
    sealed trait Directions
        
    trait Block(_data: Data, _square: Square):
        @JSExport val id = System.nanoTime()
        private val data0 = Var(_data)
        private val square0 = Var(_square)
        @JSExport val state = JSVar(Var(0))
        @JSExport val stateSignal = state.signal

        @JSExport def data: Data = data0.now()
        val dataSignal: Signal[Data] = data0.signal
        @JSExport def square = square0.signal
        def updationFunction(newData: Data, oldData: Data): Data
        @JSExport def updateData(newData: Data): Unit = data0.update(old => updationFunction(newData, old))
        @JSExport def moveTo(destination: Square): Unit = 
            this.square0.now().block = None
            destination.block = Some(this)
            this.square0.set(destination)

    protected sealed trait Square:

        @JSExport val row: Int
        @JSExport val col: Int
        var block: Option[Block] = None
        @JSExport("block") 
        def jsBlock: js.UndefOr[Block] = block.orUndefined

        val neighbours: List[SquareType] = List()
        
        @JSExport("neighbours") 
        def jsNeighbours: js.Array[SquareType] = neighbours.toJSArray

        def neighbour(dir: Directions): Option[SquareType]
        
        @JSExport("neighbour") 
        def jsNeighbour(dir: Directions): js.UndefOr[SquareType] = 
            neighbour(dir).orUndefined
        
        def nonEmpty(dir: Directions): Option[SquareType] = 
            neighbour(dir) match
                case Some(sq) => sq.block match
                    case Some(_) => Some(sq)
                    case None => sq.nonEmpty(dir)
                case None => None
        
        @JSExport("nonEmpty") 
        def jsNonEmpty(dir: Directions): js.UndefOr[SquareType] = 
            nonEmpty(dir).orUndefined

        @JSExport 
        def moveTo(sq: SquareType): Unit = 
            block.foreach(_.moveTo(sq))
        
        @JSExport 
        override def toString(): String = s"($row, $col): ${block.map(_.data).getOrElse(emptyData)}"

    type SquareType <: Square

    @JSExport var nrows: Int
    @JSExport var ncols: Int

    @JSExport val emptyData: Data
    @JSExport val cellSize: Int
    @JSExport val gridGap: Int
    @JSExport val transitionTime: Int

    var grid: IndexedSeq[IndexedSeq[SquareType]]
    val blocksVar: Var[IndexedSeq[Block]] = Var(IndexedSeq())
    val blocksSignal: Signal[IndexedSeq[Block]] = blocksVar.signal

    @JSExport("blocksVar") val jsBlocksVar = JSVar(blocksVar.bimap(_.toJSArray)(_.toIndexedSeq))
    @JSExport("blocksSignal") val jsBlocksSignal = jsBlocksVar.signal


    @JSExport("grid")
    def jsGrid: js.Array[js.Array[SquareType]] = 
        grid.map(row => row.toJSArray).toJSArray

    @JSExport val state = JSVar(Var(0))
    @JSExport val stateSignal = state.signal

    @JSExport val functions: js.Dictionary[js.Dynamic]
    @JSExport val variables: js.Dictionary[js.Any]
    @JSExport val blockListeners: js.Dictionary[js.Function1[dom.Event, ?]]
    @JSExport val squareListeners: js.Dictionary[js.Function1[dom.Event, ?]]
    @JSExport val gridListeners: js.Dictionary[js.Function1[dom.Event, ?]]

    @JSExport 
    def removeBlock(block: Block): Unit = 
        blocksVar.update(blocks => blocks.filterNot(_ == block))
    
    @JSExport
    def empties: js.Array[js.Array[Int]] = grid.zipWithIndex.flatMap((row, rowIndex) => row.zipWithIndex.flatMap{
        case (sq, colIndex) => 
            sq.block match 
                case Some(b) => None
                case None => Some(js.Array(rowIndex, colIndex))
    }).toJSArray
    
    @JSExport
    def isOutside(row: Int, col: Int) = row < 0 || col < 0 || row >= grid.length || col >= grid(0).length
    
    @JSExport
    def dataGrid = grid.map(row => row.map(_.block.map(_.data).getOrElse(emptyData)).toJSArray).toJSArray
    
    @JSExport
    def setGrid(givenGrid: js.Array[((Int, Int), Data)]): Unit = 
        givenGrid.foreach{case ((x, y), data) => placeAt(x, y, data)}
    
    @JSExport
    def placeAt(row: Int, col: Int, data: Data): Unit
    
    @JSExport
    def placeBySquare(square: SquareType, data: Data): Unit = 
        placeByXY(square.row, square.col, data)
    
    @JSExport
    def placeByXY(row: Int, col: Int, data: Data): Unit = 
        placeAt(row, col, data)
        blocksVar.update(blocks => blocks :+ grid(row)(col).block.get)
    
    @JSExport
    def className(data: Data): String
    
    @JSExport
    def init(): Unit
    
    @JSExport
    def clear(): Unit = 
        grid.foreach(row => row.foreach(sq => sq.block = None))
        blocksVar.update(_ => IndexedSeq())
        state.set(0)

    def get(row: Int, col: Int): Option[SquareType] = 
        if isOutside(row, col) then None
        else Some(grid(row)(col))

    @JSExport("get")
    def jsGet(row: Int, col: Int) = get(row, col).orUndefined

trait RookGrid[Data] extends Grid[Data]:

    @JSExport case object Left extends Directions
    @JSExport case object Right extends Directions
    @JSExport case object Up extends Directions
    @JSExport case object Down extends Directions

    type SquareType <: RookSquare

    protected trait RookSquare extends Square:

        lazy val left: Option[SquareType] = get(row, col - 1)
        lazy val right: Option[SquareType] = get(row, col + 1)
        lazy val top: Option[SquareType] = get(row - 1, col)
        lazy val bottom: Option[SquareType] = get(row + 1, col)

        @JSExport("left") lazy val jsLeft = left.orUndefined
        @JSExport("right") lazy val jsRight = right.orUndefined
        @JSExport("top") lazy val jsTop = top.orUndefined
        @JSExport("bottom") lazy val jsBottom = bottom.orUndefined

        override def neighbour(dir: Directions) = dir match
            case Left => left
            case Right => right
            case Up => top
            case Down => bottom
            case _ => None

        lazy val lefts: IndexedSeq[SquareType] = left.map(l => l +: l.lefts).getOrElse(IndexedSeq())
        lazy val rights: IndexedSeq[SquareType] = right.map(r => r +: r.rights).getOrElse(IndexedSeq())
        lazy val ups: IndexedSeq[SquareType] = top.map(t => t +: t.ups).getOrElse(IndexedSeq())
        lazy val downs: IndexedSeq[SquareType] = bottom.map(b => b +: b.downs).getOrElse(IndexedSeq())

        @JSExport("lefts") lazy val jsLefts = lefts.toJSArray
        @JSExport("rights") lazy val jsRights = rights.toJSArray
        @JSExport("ups") lazy val jsUps = ups.toJSArray
        @JSExport("downs") lazy val jsDowns = downs.toJSArray

        lazy val thisRow = lefts.reverse ++ rights
        lazy val thisColumn = ups.reverse ++ downs

        @JSExport("thisRow") lazy val jsThisRow = thisRow.toJSArray
        @JSExport("thisColumn") lazy val jsThisColumn = thisColumn.toJSArray
            
trait BishopGrid[Data] extends Grid[Data]:

    @JSExport case object TopLeft extends Directions
    @JSExport case object TopRight extends Directions
    @JSExport case object BottomLeft extends Directions
    @JSExport case object BottomRight extends Directions

    type SquareType <: BishopSquare

    protected trait BishopSquare extends Square:

        lazy val topLeft: Option[SquareType] = get(row - 1, col - 1)
        lazy val topRight: Option[SquareType] = get(row - 1, col + 1)
        lazy val bottomLeft: Option[SquareType] = get(row + 1, col - 1)
        lazy val bottomRight: Option[SquareType] = get(row + 1, col + 1)

        @JSExport("topLeft") lazy val jsTopLeft = topLeft.orUndefined
        @JSExport("topRight") lazy val jsTopRight = topRight.orUndefined
        @JSExport("bottomLeft") lazy val jsBottomLeft = bottomLeft.orUndefined
        @JSExport("bottomRight") lazy val jsBottomRight = bottomRight.orUndefined

        override def neighbour(dir: Directions) = dir match
            case TopLeft => topLeft
            case TopRight => topRight
            case BottomLeft => bottomLeft
            case BottomRight => bottomRight
            case _ => None

        lazy val toplefts: IndexedSeq[SquareType] = topLeft.map(l => l +: l.toplefts).getOrElse(IndexedSeq())
        lazy val toprights: IndexedSeq[SquareType] = topRight.map(r => r +: r.toprights).getOrElse(IndexedSeq())
        lazy val bottomlefts: IndexedSeq[SquareType] = bottomLeft.map(l => l +: l.bottomlefts).getOrElse(IndexedSeq())
        lazy val bottomrights: IndexedSeq[SquareType] = bottomRight.map(r => r +: r.bottomrights).getOrElse(IndexedSeq())

        @JSExport("toplefts") lazy val jsToplefts = toplefts.toJSArray
        @JSExport("toprights") lazy val jsToprights = toprights.toJSArray
        @JSExport("bottomlefts") lazy val jsBottomlefts = bottomlefts.toJSArray
        @JSExport("bottomrights") lazy val jsBottomrights = bottomrights.toJSArray

        lazy val backSlashDiagonal = toplefts.reverse ++ bottomrights
        lazy val forwardSlashDiagonal = toprights.reverse ++ bottomlefts

        @JSExport("backSlashDiagonal") lazy val jsBackSlashDiagonal = backSlashDiagonal.toJSArray
        @JSExport("forwardSlashDiagonal") lazy val jsForwardSlashDiagonal = forwardSlashDiagonal.toJSArray
            
trait QueenGrid[Data] extends RookGrid[Data] with BishopGrid[Data]:

    type SquareType <: QueenSquare
    protected trait QueenSquare extends RookSquare with BishopSquare:
        override def neighbour(dir: Directions) = super[RookSquare].neighbour(dir).orElse(super[BishopSquare].neighbour(dir))

