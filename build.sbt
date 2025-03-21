lazy val root = project.in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    scalaVersion := "3.5.2",
    name := "Grid Game Kitchen",

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",

    // Depend on Laminar
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0",

    // Testing framework
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0" % Test,
  )