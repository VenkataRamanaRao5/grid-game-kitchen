package gridgamekitchen

import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import js.JSConverters._
import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation

@js.native
trait GameConfigJS extends js.Object:   
    val typeStr: String = js.native
    val gridType: String = js.native
    type Data
    val nrows: Int = js.native
    val ncols: Int = js.native
    val emptyData: Data = js.native
    val updationFunction: js.Function2[Data, Data, Data] = js.native
    val className: js.Function1[js.Any, String] = js.native
    val init: js.Function0[Unit] = js.native
    val variables: js.Dictionary[js.Any] = js.native
    val functions: js.Dictionary[js.Dynamic] = js.native
    val blockListeners: js.Dictionary[js.Function1[dom.Event, ?]] = js.native
    val squareListeners: js.Dictionary[js.Function1[dom.Event, ?]] = js.native
    val gridListeners: js.Dictionary[js.Function1[dom.Event, ?]] = js.native

@EnableReflectiveInstantiation
class GameConfig[Data](config: GameConfigJS):
    val nrows: Int = config.nrows
    val ncols: Int = config.ncols
    type Type = Data
    val gridType: String = config.gridType
    val emptyData: Data = config.emptyData.asInstanceOf[Data]
    val updationFunction: (Data, Data) => Data = { (newData: Data, oldData: Data) => 
        config.updationFunction.asInstanceOf[(Data, Data) => Data].apply(newData, oldData)
    }
    val init: (Grid[?]) => Unit = (grid: Grid[?]) => config.init.asInstanceOf[js.Function1[Grid[?], Unit]].apply(grid)
    val className: Data => String = { (data: Data) => 
        config.className(data.asInstanceOf[js.Any])
    }
    val functions: js.Dictionary[js.Dynamic] = config.functions
    val variables: js.Dictionary[js.Any] = config.variables
    val blockListeners: js.Dictionary[js.Function1[dom.Event, ?]] = config.blockListeners
    val squareListeners: js.Dictionary[js.Function1[dom.Event, ?]] = config.squareListeners
    val gridListeners: js.Dictionary[js.Function1[dom.Event, ?]] = config.gridListeners

object GameConfig:
    def fromJS(config: GameConfigJS): GameConfig[?] = 
        config.typeStr match
            case "Int" => new GameConfig[Int](config)
            case "Char" => new GameConfig[Char](config)
            case "String" => new GameConfig[String](config)
            case _ => new GameConfig[AnyRef](config)