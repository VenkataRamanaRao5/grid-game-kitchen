package gridgamekitchen

import com.raquo.laminar.api.L.{Var, Signal}
import scala.collection.immutable.HashMap
import com.raquo.airstream.state.Val

trait Grid[Data]:
    
    sealed trait Directions
        
    protected trait Block(_data: Data, var square: Square):
        private var data0 = Var(_data)
        def data: Data = data0.now()
        def signal: Signal[Data] = data0.signal
        def updationFunction(newData: Data, oldData: Data): Data
        def updateData(newData: Data): Unit = data0.update(old => updationFunction(newData, old))
        def moveTo(destination: Square): Unit = 
            this.square.block.set(None)
            destination.block.set(Some(this))
            this.square = destination

        def removeFromGrid(): Unit = blockVar.update(blocks => blocks.filterNot(_ == this))

    protected sealed trait Square:

        val row: Int
        val col: Int

        val block = Var[Option[Block]](None)
        val blockSignal: Signal[Option[Block]] = block.signal

        lazy val neighbour: Map[Directions, Option[SquareType]]
        def getNeighbour: Map[Directions, Option[SquareType]] = neighbour
        lazy val nonEmpty: Map[Directions, Signal[Option[SquareType]]] = 
            directions.map(dir => 
                dir -> neighbour.getOrElse(dir, None)
                                .fold(Signal.fromValue(Option.empty))(sq => 
                                    sq.blockSignal.withCurrentValueOf(sq.nonEmpty(dir)).map((bl, nei) => bl.fold(nei)(_ => Some(sq))))
                        ).toMap
               
        def moveTo(sq: SquareType): Unit = 
            //block.now().foreach(block => block.moveTo(sq))
            block.update(b => {b.foreach(block => block.moveTo(sq)); b})

        override def toString(): String = s"($row, $col): ${block.now().map(_.data).getOrElse(emptyData)}"

    type SquareType <: Square

    val _directions: IndexedSeq[Directions]
    def directions = _directions
    val emptyData: Data
    val grid: IndexedSeq[IndexedSeq[SquareType]]
    lazy val blockVar: Var[List[Block]] = Var(List())
    lazy val blockSignal: Signal[List[Block]] = blockVar.signal

    def empties = grid.flatten.filter(_.block.now().isEmpty).map(sq => (sq.row, sq.col))

    def isOutside(row: Int, col: Int) = row < 0 || col < 0 || row >= grid.length || col >= grid(0).length

    def dataGrid = grid.map(row => row.map(_.blockSignal.foldOption(emptyData)(_.data)))
    
    def setGrid(givenGrid: IndexedSeq[((Int, Int), Data)]): Unit = 
        givenGrid.foreach{case ((x, y), data) => placeAtGrid(x, y, data)}
    
    def placeAtGrid(row: Int, col: Int, data: Data): Unit = 
        placeAt(row, col, data)
        blockVar.update(blocks => grid(row)(col).block.now().get +: blocks)

    def placeAt(row: Int, col: Int, data: Data): Unit
        

    def get(row: Int, col: Int): Option[SquareType] = 
        if isOutside(row, col) then None
        else Some(grid(row)(col))

trait RookGrid[Data] extends Grid[Data]:

    val nrows: Int
    val ncols: Int
    case object Left extends Directions
    case object Right extends Directions
    case object Up extends Directions
    case object Down extends Directions

    override val _directions = IndexedSeq(Left, Right, Up, Down)

    type SquareType <: RookSquare

    protected trait RookSquare extends Square:

        lazy val left: Option[SquareType] = get(row, col - 1)
        lazy val right: Option[SquareType] = get(row, col + 1)
        lazy val top: Option[SquareType] = get(row - 1, col)
        lazy val bottom: Option[SquareType] = get(row + 1, col)

        override lazy val neighbour = HashMap(Left -> left, Right -> right, Up -> top, Down -> bottom)

        lazy val lefts: IndexedSeq[SquareType] = left.map(l => l +: l.lefts).getOrElse(IndexedSeq())
        lazy val rights: IndexedSeq[SquareType] = right.map(r => r +: r.rights).getOrElse(IndexedSeq())
        lazy val ups: IndexedSeq[SquareType] = top.map(t => t +: t.ups).getOrElse(IndexedSeq())
        lazy val downs: IndexedSeq[SquareType] = bottom.map(b => b +: b.downs).getOrElse(IndexedSeq())

        lazy val thisRow = lefts.reverse ++ rights
        lazy val thisColumn = ups.reverse ++ downs
            
trait BishopGrid[Data] extends Grid[Data]:

    val nrows: Int
    val ncols: Int
    case object TopLeft extends Directions
    case object TopRight extends Directions
    case object BottomLeft extends Directions
    case object BottomRight extends Directions

    override val _directions = IndexedSeq(TopLeft, TopRight, BottomLeft, BottomRight)

    type SquareType <: BishopSquare

    protected trait BishopSquare extends Square:

        lazy val topLeft: Option[SquareType] = get(row - 1, col - 1)
        lazy val topRight: Option[SquareType] = get(row - 1, col + 1)
        lazy val bottomLeft: Option[SquareType] = get(row + 1, col - 1)
        lazy val bottomRight: Option[SquareType] = get(row + 1, col + 1)

        override lazy val neighbour: Map[Directions, Option[SquareType]] = 
            HashMap(TopLeft -> topLeft, TopRight -> topRight, BottomLeft -> bottomLeft, BottomRight -> bottomRight)

        lazy val toplefts: IndexedSeq[SquareType] = topLeft.map(l => l +: l.toplefts).getOrElse(IndexedSeq())
        lazy val toprights: IndexedSeq[SquareType] = topRight.map(r => r +: r.toprights).getOrElse(IndexedSeq())
        lazy val bottomlefts: IndexedSeq[SquareType] = bottomLeft.map(l => l +: l.bottomlefts).getOrElse(IndexedSeq())
        lazy val bottomrights: IndexedSeq[SquareType] = bottomRight.map(r => r +: r.bottomrights).getOrElse(IndexedSeq())

        lazy val backSlashDiagonal = toplefts.reverse ++ bottomrights
        lazy val forwardSlashDiagonal = toprights.reverse ++ bottomlefts
            
trait QueenGrid[Data] extends RookGrid[Data] with BishopGrid[Data]:
    val nrows: Int
    val ncols: Int
    type SquareType <: QueenSquare
    override val _directions = super[RookGrid].directions ++ super[BishopGrid].directions
    protected trait QueenSquare extends RookSquare with BishopSquare:
        override lazy val neighbour = directions.map(dir => dir -> super[RookSquare].getNeighbour.getOrElse(dir, super[BishopSquare].getNeighbour(dir))).toMap
