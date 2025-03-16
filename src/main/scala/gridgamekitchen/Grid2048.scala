package gridgamekitchen

class GameGrid extends RookGrid[Int]:
    val nrows = 4
    val ncols = 4
    override val emptyData = 0
    type SquareType = RookSquare

    val grid: IndexedSeq[IndexedSeq[SquareType]] = IndexedSeq.tabulate(nrows, ncols)((rowi, coli) => 
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
        empties.headOption match
            case Some((x, y)) => 
                val sq = grid(x)(y)
                sq.block = Some(new NumberBlock(sq, 2))
            case None => ()

    override def placeAt(x: Int, y: Int, data: Int): Unit = grid(x)(y).block = Some(new NumberBlock(grid(x)(y), data))
    
    def init(): Unit = 
        var sq = grid(1)(1)
        sq.block = Some(new NumberBlock(sq, 2))
        sq = grid(2)(1)
        sq.block = Some(new NumberBlock(sq, 2))
        sq = grid(3)(1)
        sq.block = Some(new NumberBlock(sq, 2))
        sq = grid(0)(1)
        sq.block = Some(new NumberBlock(sq, 2))
        placeRandom()
        placeRandom()

    def clear(): Unit = 
        grid.foreach(row => row.foreach(sq => sq.block = None))

    
        
