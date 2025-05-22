package gridgamekitchen

import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport, JSExportAll}

@JSExportAll
@JSExportTopLevel("DynamicGrid")
object DynamicGrid:
    def create(config: GameConfigJS): Grid[?] = 
        val configClass = GameConfig.fromJS(config)
        println(s"Creating grid of type: ${configClass.gridType} with data type: ${configClass}")
        val grid = config.gridType match
            case "QueenGrid" => 
                class GameGrid extends QueenGrid[configClass.Type]:
                    var nrows = configClass.nrows
                    var ncols = configClass.ncols

                    type SquareType = QueenSquare
                    type BlockType = GameBlock

                    val grid: IndexedSeq[IndexedSeq[SquareType]] = IndexedSeq.tabulate(nrows, ncols)((rowi, coli) => 
                        new QueenSquare(){
                            override val row = rowi
                            override val col = coli
                        }
                    )

                    val emptyData = configClass.emptyData
                    
                    class GameBlock(val sq: Square, val symbol: configClass.Type = emptyData) extends Block(symbol, sq):
                        override def updationFunction(newData: configClass.Type, oldData: configClass.Type) = 
                            configClass.updationFunction(newData, oldData)

                    override def placeAt(row: Int, col: Int, dat: configClass.Type): Unit =
                        grid(row)(col).block = Some(new GameBlock(grid(row)(col), dat))

                    override def init(): Unit = 
                        configClass.init(this)

                    override def className(data: configClass.Type): String =
                        configClass.className(data)

                    val functions = configClass.functions
                    val variables = configClass.variables
                    val gridListeners = configClass.gridListeners
                    val blockListeners = configClass.blockListeners
                    val squareListeners = configClass.squareListeners
                
                new GameGrid()

            case "RookGrid" => new RookGrid[configClass.Type]:
                var nrows = configClass.nrows
                var ncols = configClass.ncols

                type SquareType = RookSquare
                type BlockType = GameBlock

                val grid: IndexedSeq[IndexedSeq[SquareType]] = IndexedSeq.tabulate(nrows, ncols)((rowi, coli) => 
                    new RookSquare(){
                        override val row = rowi
                        override val col = coli
                    }
                )

                val emptyData = configClass.emptyData

                class GameBlock(val sq: Square, val symbol: configClass.Type = emptyData) extends Block(symbol, sq):
                    override def updationFunction(newData: configClass.Type, oldData: configClass.Type) = 
                        configClass.updationFunction(newData, oldData)

                override def placeAt(row: Int, col: Int, dat: configClass.Type): Unit =
                    grid(row)(col).block = Some(new GameBlock(grid(row)(col), dat))

                override def init(): Unit = 
                    configClass.init(this)

                override def className(data: configClass.Type): String =
                    configClass.className(data)
                val functions = configClass.functions
                val variables = configClass.variables
                val gridListeners = configClass.gridListeners
                val blockListeners = configClass.blockListeners
                val squareListeners = configClass.squareListeners

            case "BishopGrid" => new BishopGrid[configClass.Type]:
                var nrows = configClass.nrows
                var ncols = configClass.ncols

                type SquareType = BishopSquare
                type BlockType = GameBlock

                val grid: IndexedSeq[IndexedSeq[SquareType]] = IndexedSeq.tabulate(nrows, ncols)((rowi, coli) => 
                    new BishopSquare(){
                        override val row = rowi
                        override val col = coli
                    }
                )

                val emptyData = configClass.emptyData

                class GameBlock(val sq: Square, val symbol: configClass.Type = emptyData) extends Block(symbol, sq):
                    override def updationFunction(newData: configClass.Type, oldData: configClass.Type) = 
                        configClass.updationFunction(newData, oldData)

                override def placeAt(row: Int, col: Int, dat: configClass.Type): Unit =
                    grid(row)(col).block = Some(new GameBlock(grid(row)(col), dat))

                override def init(): Unit = 
                    configClass.init(this)

                override def className(data: configClass.Type): String =
                    configClass.className(data)

                val functions = configClass.functions
                val variables = configClass.variables
                val gridListeners = configClass.gridListeners
                val blockListeners = configClass.blockListeners
                val squareListeners = configClass.squareListeners
            
            case _ => 
                throw new IllegalArgumentException(s"Unknown grid type: ${config.gridType}")
        
        configClass.functions.foreach { case (name, func) => 
            grid.functions.update(name, func.bind(grid.asInstanceOf[js.Object]))
        }

        configClass.variables.foreach { case (name, value) => 
            grid.variables.update(name, value)
        }

        grid