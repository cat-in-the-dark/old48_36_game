lazy val commonSettings = Seq(
  organization := "com.cat_in_the_dark",
  version := "0.0.1",
  scalaVersion := "2.11.8"
)

val libgdxVersion = "1.9.4"

lazy val core = project.in(file("./core"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "com.badlogicgames.gdx" % "gdx" % libgdxVersion,
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.2"
  ))

lazy val lib = project.in(file("./lib"))
    .dependsOn(core)
    .settings(commonSettings: _*)
    .settings(libraryDependencies ++= Seq(
      "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % libgdxVersion,
      "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion classifier "natives-desktop",
      "com.badlogicgames.gdx" % "gdx-freetype" % libgdxVersion,
      "com.badlogicgames.gdx" % "gdx-freetype-platform" % libgdxVersion classifier "natives-desktop",
      "io.socket" % "socket.io-client" % "0.7.0"
    ))

lazy val client = project.in(file("./client"))
  .dependsOn(lib)
  .settings(commonSettings: _*)
  .settings(unmanagedResourceDirectories in Compile += file("./client/assets"))
  .settings(assemblyJarName in assembly := "client.jar")
  .settings(fork in Compile := true)
  .settings(libraryDependencies ++= Seq(

  ))

lazy val server = project.in(file("./server"))
  .dependsOn(core)
  .settings(commonSettings: _*)
  .settings(assemblyJarName in assembly := "server.jar")
  .enablePlugins(JavaAppPackaging)
  .settings(assemblyMergeStrategy in assembly := {
    case _ => MergeStrategy.first
  })
  .settings(libraryDependencies ++= Seq(
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.2",
    "com.corundumstudio.socketio" % "netty-socketio" % "1.7.10",
    "org.slf4j" % "slf4j-simple" % "1.7.7",
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
    "org.sql2o" % "sql2o" % "1.5.4",
    "org.flywaydb" % "flyway-core" % "3.2.1"
  ))