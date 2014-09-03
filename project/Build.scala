import sbt._
import Keys._
import play.Play._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build with UniversalKeys {

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  override def rootProject = Some(sitejvm)

  val sharedSrcDir = "shared"

  lazy val sitejvm = Project(
    id = "sitejvm",
    base = file("sitejvm")
  ) enablePlugins (play.PlayScala) settings (sitejvmSettings: _*) aggregate (scalajs)

  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("sitejs")
  ) settings (scalajsSettings: _*)

  lazy val sharedScala = Project(
    id = "sharedScala",
    base = file(sharedSrcDir)
  ) settings (sharedScalaSettings: _*)

  lazy val sitejvmSettings =
    Seq(
      name := "scalagis",
      version := Versions.app,
      scalaVersion := Versions.scala,
      scalajsOutputDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
      compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (scalajs, Compile)),
      dist <<= dist dependsOn (fullOptJS in (scalajs, Compile)),
      libraryDependencies ++= Dependencies.sitejvm,
      commands += preStartCommand,
      EclipseKeys.skipParents in ThisBuild := false
    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, packageLauncher, fastOptJS, fullOptJS) map { packageJSKey =>
        crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
    ) ++ sharedDirectorySettings

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "scalajs-example",
      version := Versions.app,
      scalaVersion := Versions.scala,
      persistLauncher := true,
      persistLauncher in Test := false,
      libraryDependencies ++= ("org.scala-lang.modules.scalajs" %%% "scalajs-dom" % Versions.scalajsDom) +: Dependencies.scalajs
    ) ++ sharedDirectorySettings

  lazy val sharedScalaSettings =
    Seq(
      name := "shared-scala-example",
      EclipseKeys.skipProject := true,
      libraryDependencies ++= Dependencies.shared
    )

  lazy val sharedDirectorySettings = Seq(
    unmanagedSourceDirectories in Compile += new File((file(".") / sharedSrcDir / "src" / "main" / "scala").getCanonicalPath),
    unmanagedSourceDirectories in Test += new File((file(".") / sharedSrcDir / "src" / "test" / "scala").getCanonicalPath),
    unmanagedResourceDirectories in Compile += file(".") / sharedSrcDir / "src" / "main" / "resources",
    unmanagedResourceDirectories in Test += file(".") / sharedSrcDir / "src" / "test" / "resources"
  )

  // Use reflection to rename the 'start' command to 'play-start'
  Option(play.Play.playStartCommand.getClass.getDeclaredField("name")) map { field =>
    field.setAccessible(true)
    field.set(playStartCommand, "play-start")
  }

  // The new 'start' command optimises the JS before calling the Play 'start' renamed 'play-start'
  val preStartCommand = Command.args("start", "<port>") { (state: State, args: Seq[String]) =>
    Project.runTask(fullOptJS in (scalajs, Compile), state)
    state.copy(remainingCommands = ("play-start " + args.mkString(" ")) +: state.remainingCommands)
  }
}

object Dependencies {
  val shared = Seq()

  val sitejvm = Seq(
    "org.webjars" %% "webjars-play" % "2.3.0",
    "org.webjars" % "jquery" % "2.1.1",
    "org.webjars" % "modernizr" % "2.7.1",
    "org.webjars" % "foundation" % "5.3.0",
    "org.webjars" % "font-awesome" % "4.2.0",
    "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
    "com.typesafe.play" %% "play-slick" % "0.8.0",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.github.tminglei" %% "slick-pg" % "0.6.3",
    "com.github.tminglei" %% "slick-pg_joda-time" % "0.6.3",
    "com.github.tminglei" %% "slick-pg_jts" % "0.6.3",
    "com.scalatags" %% "scalatags" % Versions.scalatags,
    "com.lihaoyi" %% "upickle" % Versions.upickle,
    "com.lihaoyi" %% "autowire" % Versions.autowire
  ) ++ shared

  val scalajs = Seq(
    "com.scalatags" %%%! "scalatags" % Versions.scalatags,
    "com.lihaoyi" %%%! "upickle" % Versions.upickle,
    "com.lihaoyi" %%%! "autowire" % Versions.autowire
  ) ++ shared
}

object Versions {
  val app = "0.1.0-SNAPSHOT"
  val scala = "2.11.1"
  val scalajsDom = "0.6"
  val scalatags="0.4.0"
  val upickle="0.2.2"
  val autowire="0.2.2"
}
