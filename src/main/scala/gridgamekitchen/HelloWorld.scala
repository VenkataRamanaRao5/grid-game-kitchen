package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import com.raquo.laminar.api.L.{*, given}

def renderGrid(g: GameGrid) =
  def renderBlock(block: Signal[g.Block]) =
    div(
      child.text <-- block.flatMapSwitch(b => b.signal),
      cls("cell block"),
      styleProp("top") <-- block.flatMapSwitch(_.square.map(sq => s"${sq.row * 60}px")),
      styleProp("left") <-- block.flatMapSwitch(_.square.map(sq => s"${sq.col * 60}px")),
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
      children <-- g.blocksSignal.split(_.id) { (id, intial, block) => renderBlock(block) }
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
  def main(args: Array[String]): Unit = {
    val root = document.getElementById("root")
    document.documentElement.setAttribute(
      "style",
      s"--grid-rows: ${g.nrows}; --grid-cols: ${g.ncols};"
    )
    val appElement = div(
      button(
        "Down",
        onClick --> { _ =>
          g.moveGrid(g.Down)
          g.placeRandom()
          println(g.dataGrid.flatMap(_.toString()).mkString(" "))
        }
      ),
      button(
        "Up",
        onClick --> { _ =>
          g.moveGrid(g.Up)
          g.placeRandom()
          println(g.dataGrid.flatMap(_.toString()).mkString(" "))
        }
      ),
      button(
        "Left",
        onClick --> { _ =>
          g.moveGrid(g.Left)
          g.placeRandom()
          println(g.dataGrid.flatMap(_.toString()).mkString(" "))
        }
      ),
      button(
        "Right",
        onClick --> { _ =>
          g.moveGrid(g.Right)
          g.placeRandom()
          println(g.dataGrid.flatMap(_.toString()).mkString(" "))
        }
      ),
      renderGrid(g)
    )
    renderOnDomContentLoaded(
      root,
      appElement
    )
  }

}
