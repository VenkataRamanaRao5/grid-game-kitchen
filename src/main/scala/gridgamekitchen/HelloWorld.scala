package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport, JSImport}
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.KeyboardEvent

def renderGrid(g: Grid[?], cellSize: Int, gridGap: Int, transitionTime: Int) =
  def renderBlock(block: Signal[g.Block]) =
    div(
      child.text <-- block.flatMapSwitch(_.dataSignal.composeChanges(_.delay(transitionTime)).map(_.toString)),
      cls("block"),
      cls <-- block.flatMapSwitch(
        _.dataSignal.composeChanges(_.delay(transitionTime)).map(data => g.className(data))
      ),
      styleProp("top") <-- block.flatMapSwitch(
        _.square.map(sq => style.px(sq.row * (cellSize + gridGap)))
      ),
      styleProp("left") <-- block.flatMapSwitch(
        _.square.map(sq => style.px(sq.col * (cellSize + gridGap)))
      ),
      onMountCallback(blk => {
        g.blockListeners.foreach{
          case (name, func) => 
            blk.thisNode.ref.addEventListener(name, func)
        }
        blk.thisNode.ref.asInstanceOf[js.Dynamic].block = block.asInstanceOf[js.Object]
        blk.thisNode.ref.asInstanceOf[js.Dynamic].grid = g.asInstanceOf[js.Object]
      }),
    )
  end renderBlock

  div(
    cls("grid"),
    g.grid.map(row =>
      row.map(sq => 
        div(
          cls("cell"),
          styleProp("grid-row") := sq.row + 1,
          styleProp("grid-column") := sq.col + 1,
          onMountCallback(squ => {
            g.squareListeners.foreach{
              case (name, func) => 
                squ.thisNode.ref.addEventListener(name, func)
            }
            squ.thisNode.ref.asInstanceOf[js.Dynamic].square = sq.asInstanceOf[js.Object]
            squ.thisNode.ref.asInstanceOf[js.Dynamic].grid = g.asInstanceOf[js.Object]
          }),
        ),
      )
    ),
    children <-- g.blocksSignal.composeChanges(_.delay(transitionTime)).split(_.id) { (id, intial, block) =>
      renderBlock(block)
    },
  )
end renderGrid

@JSExportTopLevel("GridApp")
object GridApp {
  @JSImport("/assets/js/GridXOJS.js", "GridXO")
  @js.native
  object gameRules extends GameConfigJS
  @JSExport
  val g = DynamicGrid.create(gameRules)
  g.init()

  js.Dynamic.global.window.grid = g.asInstanceOf[js.Object]

  val cellSize = 75
  val gridGap = 5
  val transitionTime = 500

  def main(args: Array[String]): Unit = {

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
      renderGrid(g, cellSize, gridGap, transitionTime),
      onMountCallback(cont => 
        g.gridListeners.foreach{
          case (name, func) => 
            cont.thisNode.ref.addEventListener(name, func)
        }
      ),
    )
    renderOnDomContentLoaded(
      root,
      appElement
    )

  }

}
