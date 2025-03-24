package gridgamekitchen

import com.raquo.laminar.api.L.{Var, Signal}
import com.raquo.airstream.ownership.Owner

trait Grid[Data]:
    
    sealed trait Directions
        
    trait Block(_data: Data, _square: Square):
        val id = System.nanoTime()
        private val data0 = Var(_data)
        private val square0 = Var(_square)
        val state = Var(0)
        val stateSignal = state.signal

        def data: Data = data0.now()
        val dataSignal: Signal[Data] = data0.signal
        def square = square0.signal
        def updationFunction(newData: Data, oldData: Data): Data
        def updateData(newData: Data): Unit = data0.update(old => updationFunction(newData, old))
        def moveTo(destination: Square): Unit = 
            this.square0.now().block = None
            destination.block = Some(this)
            this.square0.set(destination)

    protected sealed trait Square:

        val row: Int
        val col: Int
        var block: Option[Block] = None

        val neighbours: List[SquareType] = List()

        def neighbour(dir: Directions): Option[SquareType]
        
        def nonEmpty(dir: Directions): Option[SquareType] = 
            neighbour(dir) match
                case Some(sq) => sq.block match
                    case Some(_) => Some(sq)
                    case None => sq.nonEmpty(dir)
                case None => None

        def moveTo(sq: SquareType): Unit = 
            block.foreach(_.moveTo(sq))
        override def toString(): String = s"($row, $col): ${block.map(_.data).getOrElse(emptyData)}"

    type SquareType <: Square
    val emptyData: Data
    val grid: IndexedSeq[IndexedSeq[SquareType]]
    val blocksVar: Var[IndexedSeq[Block]] = Var(IndexedSeq())
    val blocksSignal: Signal[IndexedSeq[Block]] = blocksVar.signal

    val state = Var(0)
    val stateSignal = state.signal

    given owner: Owner = new Owner {}

    def removeBlock(block: Block): Unit = 
        blocksVar.update(blocks => blocks.filterNot(_ == block))

    def empties: IndexedSeq[(Int, Int)] = grid.zipWithIndex.flatMap((row, rowIndex) => row.zipWithIndex.flatMap{
        case (sq, colIndex) => 
            sq.block match 
                case Some(b) => None
                case None => Some((rowIndex, colIndex))
    })

    def isOutside(row: Int, col: Int) = row < 0 || col < 0 || row >= grid.length || col >= grid(0).length

    def dataGrid = grid.map(row => row.map(_.block.map(_.data).getOrElse(emptyData)))
    
    def setGrid(givenGrid: IndexedSeq[((Int, Int), Data)]): Unit = 
        givenGrid.foreach{case ((x, y), data) => placeAt(x, y, data)}
    
    def placeAt(row: Int, col: Int, data: Data): Unit

    def placeAtGrid(row: Int, col: Int, data: Data): Unit = 
        placeAt(row, col, data)
        blocksVar.update(blocks => blocks :+ grid(row)(col).block.get)

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

    type SquareType <: RookSquare

    protected trait RookSquare extends Square:

        lazy val left: Option[SquareType] = get(row, col - 1)
        lazy val right: Option[SquareType] = get(row, col + 1)
        lazy val top: Option[SquareType] = get(row - 1, col)
        lazy val bottom: Option[SquareType] = get(row + 1, col)

        def neighbour(dir: Directions) = dir match
            case Left => left
            case Right => right
            case Up => top
            case Down => bottom
            case _ => None

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

    type SquareType <: BishopSquare

    protected trait BishopSquare extends Square:

        lazy val topLeft: Option[SquareType] = get(row - 1, col - 1)
        lazy val topRight: Option[SquareType] = get(row - 1, col + 1)
        lazy val bottomLeft: Option[SquareType] = get(row + 1, col - 1)
        lazy val bottomRight: Option[SquareType] = get(row + 1, col + 1)

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

        lazy val backSlashDiagonal = toplefts.reverse ++ bottomrights
        lazy val forwardSlashDiagonal = toprights.reverse ++ bottomlefts
            
trait QueenGrid[Data] extends RookGrid[Data] with BishopGrid[Data]:
    val nrows: Int
    val ncols: Int
    type SquareType <: QueenSquare
    protected trait QueenSquare extends RookSquare with BishopSquare:
        override def neighbour(dir: Directions) = super[RookSquare].neighbour(dir).orElse(super[BishopSquare].neighbour(dir))

