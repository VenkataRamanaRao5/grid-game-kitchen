package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel
import com.raquo.laminar.api.L.{*, given}

def renderGrid(g: GameGrid) =
  div(
    idAttr("grid"),
    children <-- g.blockSignal.map { blocks =>
      blocks.map { block =>
        div(
          cls := "block",
          block.data.toString()
        )
      }
    }
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
