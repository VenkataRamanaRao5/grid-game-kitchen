package gridgamekitchen

import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport, JSExportAll}

trait GridBuilder[Data](config: GameConfig[Data]) extends Grid[Data]:
    nrows.set(config.nrows)
    ncols.set(config.ncols)
    val emptyData = config.emptyData
    val cellSize = config.cellSize
    val gridGap = config.gridGap
    val transitionTime = config.transitionTime

    override def placeAt(row: Int, col: Int, data: Data): Option[Block] = {
        grid(row)(col).block = Some(new GameBlock(grid(row)(col), data))
        grid(row)(col).block
    }

    type BlockType = GameBlock

    class GameBlock(sq: Square, symbol: Data = emptyData) extends Block(symbol, sq) {
        override def updationFunction(newData: Data, oldData: Data) = 
        config.updationFunction(newData, oldData)
    }

    override def className(data: Data): String = config.className(data)

    override def init(): Unit = config.init(this)

    val functions = config.functions
    val variables = config.variables
    val gridListeners = config.gridListeners
    val blockListeners = config.blockListeners
    val squareListeners = config.squareListeners
    
object QueenGridBuilder:
    def build[Data](config: GameConfig[Data]): Grid[Data] = 
        new QueenGrid[Data] with GridBuilder[Data](config):
            type SquareType = QueenSquare

            override def buildGrid(rows: Int, cols: Int): Unit = 
                nrows.set(rows)
                ncols.set(cols)
                grid = IndexedSeq.tabulate(rows, cols)((rowi, coli) => 
                    new QueenSquare(){
                        override val row = rowi
                        override val col = coli
                    }
                )
            buildGrid(config.nrows, config.ncols)

object RookGridBuilder:
    def build[Data](config: GameConfig[Data]): Grid[Data] = 
        new RookGrid[Data] with GridBuilder[Data](config):
            type SquareType = RookSquare
            override def buildGrid(rows: Int, cols: Int): Unit =
                nrows.set(rows)
                ncols.set(cols)
                grid = IndexedSeq.tabulate(rows, cols)((rowi, coli) => 
                    new RookSquare(){
                        override val row = rowi
                        override val col = coli
                    }
                )
            buildGrid(config.nrows, config.ncols)

object BishopGridBuilder:
    def build[Data](config: GameConfig[Data]): Grid[Data] = 
        new BishopGrid[Data] with GridBuilder[Data](config):
            type SquareType = BishopSquare
            override def buildGrid(rows: Int, cols: Int): Unit =
                nrows.set(rows)
                ncols.set(cols)
                grid = IndexedSeq.tabulate(rows, cols)((rowi, coli) => 
                    new BishopSquare(){
                        override val row = rowi
                        override val col = coli
                    }
                )
            buildGrid(config.nrows, config.ncols)
        

@JSExportAll
@JSExportTopLevel("DynamicGrid")
object DynamicGrid:

    def create(config: GameConfigJS): Grid[?] = 
        val configClass = GameConfig.fromJS(config)
        println(s"Creating grid of type: ${configClass.gridType} with data type: ${configClass}")
        val gridObj = config.gridType match
            case "QueenGrid" => QueenGridBuilder.build(configClass)
            case "RookGrid" => RookGridBuilder.build(configClass)
            case "BishopGrid" => BishopGridBuilder.build(configClass)
            case _ => 
                throw new IllegalArgumentException(s"Unknown grid type: ${config.gridType}")

        configClass.functions.foreach { case (name, func) => 
            gridObj.functions.update(name, func.bind(gridObj.asInstanceOf[js.Object]))
        }

        configClass.variables.foreach { case (name, value) => 
            gridObj.variables.update(name, value)
        }

        gridObj
