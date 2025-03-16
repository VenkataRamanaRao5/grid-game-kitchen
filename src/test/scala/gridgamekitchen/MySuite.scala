// For more information on writing tests, see
// https://scalameta.org/munit/docs/getting-started.html
package gridgamekitchen

class MySuite extends munit.FunSuite {
  test("example test that succeeds") {
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
  }

  test("Moving grid down") {
    val g = GameGrid()
    val indices = Vector(((0, 0), 2), ((0, 1), 2), ((0, 2), 2), ((1, 1), 2), ((2, 1), 2), ((3, 1), 2))
    g.setGrid(indices)
    g.moveGrid(g.Down)
    println(g.dataGrid)
    assertEquals(g.dataGrid, Vector(Vector(0, 0, 0, 0), Vector(0, 0, 0, 0), Vector(0, 4, 0, 0), Vector(2, 4, 2, 0)))
  }

  test("Moving grid right") {
    val g = GameGrid()
    val indices = Vector(((0, 0), 2), ((0, 1), 2), ((0, 2), 2), ((1, 1), 2), ((2, 1), 2), ((3, 1), 2))
    g.setGrid(indices)
    g.moveGrid(g.Right)
    println(g.dataGrid)
    assertEquals(g.dataGrid, Vector(Vector(0, 0, 2, 4), Vector(0, 0, 0, 2), Vector(0, 0, 0, 2), Vector(0, 0, 0, 2)))
  }

  test("Tictactoe test") {
    val g = TicTacToe()
    val indices = Vector(((0, 0), 'X'), ((0, 1), 'O'), ((0, 2), 'X'), ((1, 1), 'O'))
    g.setGrid(indices)
    println(g.dataGrid)
    val f = g.ply(g.grid(2)(1), 'O')
    assertEquals(g.dataGrid, Vector(Vector('X', 'O', 'X'), Vector(' ', 'O', ' '), Vector(' ', 'O', ' ')))
    assertEquals(f, true)
  }

}
