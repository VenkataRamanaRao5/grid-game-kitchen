enablePlugins(ScalaJSPlugin)

name := "Grid Game Kitchen"
scalaVersion := "3.5.2" // or a newer version such as "3.4.2", if you like

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0"