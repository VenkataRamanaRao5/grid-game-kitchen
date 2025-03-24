package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.KeyboardEvent

def renderGrid(g: GameGrid, cellSize: Int, gridGap: Int, transitionTime: Int) =
  def renderBlock(block: Signal[g.Block]) =
    div(
      child.text <-- block.flatMapSwitch(_.dataSignal.composeChanges(_.delay(transitionTime))),
      cls("cell block"),
      cls <-- block.flatMapSwitch(
        _.dataSignal.composeChanges(_.delay(transitionTime)).map(data => g.className(data))
      ),
      styleProp("top") <-- block.flatMapSwitch(
        _.square.map(sq => style.px(sq.row * (cellSize + gridGap)))
      ),
      styleProp("left") <-- block.flatMapSwitch(
        _.square.map(sq => style.px(sq.col * (cellSize + gridGap)))
      )
    )
  end renderBlock

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
    children <-- g.blocksSignal.composeChanges(_.delay(transitionTime)).split(_.id) { (id, intial, block) =>
      renderBlock(block)
    },
  )

object GridApp {
  val g = GameGrid()
  g.init()

  val cellSize = 75
  val gridGap = 5
  val transitionTime = 500

  def main(args: Array[String]): Unit = {
    document.addEventListener("keydown", (e: KeyboardEvent) => {
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
    
    val appElement = div(
      cls("container"),
      styleAttr := s"""
        --grid-rows: ${g.nrows};
        --grid-cols: ${g.ncols};
        --cell-size: ${cellSize}px;
        --grid-gap: ${gridGap}px;
        --transition-time: ${transitionTime}ms;
      """,
      renderGrid(g, cellSize, gridGap, transitionTime)
    )
    renderOnDomContentLoaded(
      root,
      appElement
    )
  }

}
