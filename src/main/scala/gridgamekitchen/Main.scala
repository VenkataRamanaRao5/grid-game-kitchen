package gridgamekitchen

@main def hello(): Unit =
  println("Hello world!")
  println(msg)
  val g = GameGrid()
  println(g.empties.toIndexedSeq.sorted)
  println(g.dataGrid)
  g.init()
  println(g.empties.toIndexedSeq.sorted)
  println(g.dataGrid)
  println(g.grid.map(_.map(_.neighbour(g.Down))))
  print(g.grid.map(_.map(_.nonEmpty(g.Down))))
  g.moveGrid(g.Down)
  println(g.empties.toIndexedSeq.sorted)
  println(g.dataGrid)
  g.clear()
  println(g.empties.toIndexedSeq.sorted)
  println(g.dataGrid)

def msg = "I was compiled by Scala 3. :)"


