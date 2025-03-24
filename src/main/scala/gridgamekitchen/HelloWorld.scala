package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import com.raquo.laminar.api.L.{*, given}

def renderGrid(g: GameGrid, cellSize: Int, gridGap: Int) =
  def renderBlock(block: Signal[g.Block]) =
    div(
      child.text <-- block.flatMapSwitch(b => b.signal),
      cls("cell block"),
      cls <-- block.flatMapSwitch(
        _.signal.map(data => g.className(data))
      ),
      styleProp("top") <-- block.flatMapSwitch(
        _.square.map(sq => s"${sq.row * (cellSize + gridGap)}px")
      ),
      styleProp("left") <-- block.flatMapSwitch(
        _.square.map(sq => s"${sq.col * (cellSize + gridGap)}px")
      )
    )
  end renderBlock

  div(
    idAttr("grid"),
    div(
      cls("grid"),
      g.grid.map(row =>
        row.map(sq =>
          div(
            cls("cell"),
            styleProp("grid-row") := sq.row + 1,
            styleProp("grid-column") := sq.col + 1
          ),
        )
      ),
      children <-- g.blocksSignal.split(_.id) { (id, intial, block) =>
        renderBlock(block)
      }
    )
  )

def toggleGrid() = {
  val gridElement = document.getElementById("grid")
  if (gridElement.classList.contains("hidden")) {
    gridElement.classList.remove("hidden")
  } else {
    gridElement.classList.add("hidden")
  }
}

object GridApp {
  val g = GameGrid()
  g.init()
  val cellSize = 50
  val gridGap = 5
  def main(args: Array[String]): Unit = {
    document.documentElement.setAttribute(
      "style",
      s"""
        --grid-rows: ${g.nrows};
        --grid-cols: ${g.ncols};
        --cell-size: ${cellSize}px;
        --grid-gap: ${gridGap}px;
      """
    )
    document.addEventListener(
      "keydown",
      (e: dom.KeyboardEvent) => {
        println(e.key)
        e.key match {
          case "ArrowUp"    => g.moveGrid(g.Up)
          case "ArrowDown"  => g.moveGrid(g.Down)
          case "ArrowLeft"  => g.moveGrid(g.Left)
          case "ArrowRight" => g.moveGrid(g.Right)
        }
        println(g.dataGrid.flatMap(_.toString()).mkString(" "))
      }
    )
    val root = document.getElementById("root")
    val appElement = renderGrid(g, cellSize, gridGap)
    renderOnDomContentLoaded(
      root,
      appElement
    )
  }

}
