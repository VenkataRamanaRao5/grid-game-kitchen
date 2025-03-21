package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js.annotation.JSExportTopLevel

def appendPar(targetNode: dom.Node, text: String): Unit = {
  val parNode = document.createElement("p")
  parNode.textContent = text
  targetNode.appendChild(parNode)
}

@JSExportTopLevel("clickHandler")
def addClickedMessage(): Unit = {
  toggleGrid()
  appendPar(document.body, "You clicked the button!")
}

def appendGrid(targetNode: dom.Element, grid: GameGrid): Unit = {
  val gridElement = document.createElement("div")
  gridElement.id = "grid"
  grid.dataGrid.foreach { row =>
    val rowNode = document.createElement("div")
    rowNode.classList.add("row")
    row.foreach { cell =>
      val cellNode = document.createElement("div")
      cellNode.classList.add("cell")
      cellNode.textContent = cell.toString
      rowNode.appendChild(cellNode)
    }
    gridElement.appendChild(rowNode)
  }
  targetNode.appendChild(gridElement)
}

def toggleGrid() = {
  val gridElement = document.getElementById("grid")
  if(gridElement.classList.contains("hidden")) {
    gridElement.classList.remove("hidden")
  } else {
    gridElement.classList.add("hidden")
  }
}

object HelloWorld {
  def main(args: Array[String]): Unit = {
    document.addEventListener(
      "DOMContentLoaded",
      { (e: dom.Event) =>
        setupUI()
      }
    )
  }
}

def setupUI(): Unit = {
  val button = document.createElement("button")
  button.textContent = "Toggle Grid"
  button.addEventListener(
    "click",
    { (e: dom.MouseEvent) =>
      addClickedMessage()
    }
  )
  document.body.appendChild(button)

  val grid = GameGrid()
  grid.init()
  appendGrid(document.body, grid)

  appendPar(document.body, "Hello World")
}
