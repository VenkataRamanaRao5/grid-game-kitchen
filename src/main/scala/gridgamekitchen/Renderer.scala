package gridgamekitchen

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, JSExport, JSImport}
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.KeyboardEvent
import com.raquo.airstream.ownership.Subscription

given owner: Owner = new Owner {}

def renderGrid(g: Signal[Grid[?]]) =
  g.map(_renderGrid)

def _renderGrid(g: Grid[?]) =
  def renderBlock(block: Signal[g.Block]) =
    var subscription: Option[Subscription] = None
    div(
      child.text <-- block.flatMapSwitch(_.dataSignal.composeChanges(_.delay(g.transitionTime)).map(_.toString)),
      cls("block"),
      cls <-- block.flatMapSwitch(
        _.dataSignal.composeChanges(_.delay(g.transitionTime)).map(data => g.className(data))
      ),
      styleProp("top") <-- block.flatMapSwitch(
        _.square.map(sq => style.px(sq.row * (g.cellSize + g.gridGap)))
      ),
      styleProp("left") <-- block.flatMapSwitch(
        _.square.map(sq => style.px(sq.col * (g.cellSize + g.gridGap)))
      ),
      onMountCallback(blk => {
        g.blockListeners.foreach{
          case (name, func) => 
            blk.thisNode.ref.addEventListener(name, func)
        }
        subscription = Some(block.foreach(bloc =>
          blk.thisNode.ref.asInstanceOf[js.Dynamic].block = bloc.asInstanceOf[js.Object]
        ))
        blk.thisNode.ref.asInstanceOf[js.Dynamic].grid = g.asInstanceOf[js.Object]
      }),
      onUnmountCallback(_ => subscription.map(_.kill()))
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
    children <-- g.blocksSignal.composeChanges(_.delay(g.transitionTime)).split(_.id) { (id, intial, block) =>
      renderBlock(block)
    },
  )
end _renderGrid

object GridApp {
  @JSImport("./GridXOJS.js", JSImport.Default)
  @js.native
  object gameRulesXO extends GameConfigJS

  @JSImport("./Grid2048JS.js", JSImport.Default)
  @js.native
  object gameRules2048 extends GameConfigJS

  @JSImport("./GridArjunJS.js", JSImport.Default)
  @js.native
  object gameRulesArjun extends GameConfigJS
  
  val gXO = DynamicGrid.create(gameRulesXO)
  gXO.init()
  val g2048 = DynamicGrid.create(gameRules2048)
  g2048.init()
  val gArjun = DynamicGrid.create(gameRulesArjun)
  gArjun.init()

  val g = Var(g2048)
  val gSignal = g.signal

  js.Dynamic.global.window.grid = g.asInstanceOf[js.Object]

  val cellSize = gSignal.map(_.cellSize)
  val gridGap = gSignal.map(_.gridGap)
  val transitionTime = gSignal.map(_.transitionTime)

  def main(args: Array[String]): Unit = {

    val root = document.getElementById("root")
    
    val appElement = div(
      cls("container"),
      styleProp("--grid-rows") <-- gSignal.flatMapSwitch(_.nrows.var0.signal),
      styleProp("--grid-cols") <-- gSignal.flatMapSwitch(_.ncols.var0.signal),
      styleProp("--cell-size") <-- cellSize.map(style.px),
      styleProp("--grid-gap") <-- gridGap.map(style.px),
      styleProp("--transition-time") <-- transitionTime.map(t => style.ms(t.toInt)),
      button(
        cls("reset-button"),
        "Reset",
        onClick --> { _ =>
          gSignal.foreach(g => {
            g.clear()
            g.state.set(0)
            g.init()
        })
        }
      ),
      select(
        cls("select-grid"),
        option(
          value := "XO",
          "XO",
        ),
        option(
          value := "2048",
          "2048",
          defaultSelected := true,
        ),
        option(
          value := "Arjun",
          "Arjun",
        ),
        onChange.mapToValue --> { value =>
          value match {
            case "XO" => g.set(gXO)
            case "2048" => g.set(g2048)
            case "Arjun" => g.set(gArjun)
            case _ => ()
          }
          gSignal.foreach(_.init())
        }
      ),
      child <-- renderGrid(gSignal),
      onMountCallback(cont => 
        gSignal.foreach(g => 
          g.gridListeners.foreach {
            case (name, func) => 
              cont.thisNode.ref.addEventListener(name, func)
          }
          cont.thisNode.ref.asInstanceOf[js.Dynamic].grid = g.asInstanceOf[js.Object]
        )
      ),
    )
    renderOnDomContentLoaded(
      root,
      appElement
    )

  }

}
