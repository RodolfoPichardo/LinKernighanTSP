lazy val projectVersion = "0.1.3"
lazy val mimaVersion    = "0.1.0"

lazy val baseName       = "LinKernighanTSP"
lazy val baseNameL      = baseName.toLowerCase

// sonatype plugin requires that these are in global
ThisBuild / version      := projectVersion
ThisBuild / organization := "de.sciss"

lazy val commonSettings = Seq(
  name                           := baseName,
//  version                        := projectVersion,
//  organization                   := "de.sciss",
  homepage                       := Some(url(s"https://git.iem.at/sciss/${name.value}")),
  licenses                       := Seq("MIT" -> url("https://raw.githubusercontent.com/Sciss/LinKernighanTSP/main/LICENSE")),
 // fork                           := true,
  javaOptions  in run            += "-Xmx8G",
  connectInput in run            := true,
  outputStrategy                 := Some(StdoutOutput),
)

lazy val root = crossProject(JVMPlatform, JSPlatform).in(file("."))
  .settings(commonSettings)
  .settings(
    scalaVersion                := "2.13.4",
    mainClass in (Compile, run) := Some("de.sciss.tsp.Main"),
    mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion),
  )
  .jvmSettings(
    crossScalaVersions          := Seq("3.0.0-M2", "2.13.4", "2.12.12"),
  )
  .settings(publishSettings)

lazy val javaProject = project.in(file("java"))
  .settings(commonSettings)
  .settings(
    fork := true,
    javacOptions in Compile        ++= Seq("-target", "1.8", "-source", "1.8"),
    javacOptions in (Compile, doc)  := Nil,
    crossScalaVersions              := Seq(scalaVersion.value),
    autoScalaLibrary                := false,
    mainClass in (Compile, run)     := Some("j.Main")
  )

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    Developer(
      id    = "Jilocasin",
      name  = "Daniel Obermeier",
      email = "mail@jilocasin.de",
      url   = url("https://jilocasin.de/"),
    ),
    Developer(
      id    = "sciss",
      name  = "Hanns Holger Rutz",
      email = "contact@sciss.de",
      url   = url("https://www.sciss.de"),
    ),
  ),
  scmInfo := {
    val h = "git.iem.at"
    val a = s"sciss/${name.value}"
    Some(ScmInfo(url(s"https://$h/$a"), s"scm:git@$h:$a.git"))
  },
)

