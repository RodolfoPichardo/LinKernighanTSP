lazy val commonSettings = Seq(
  name                           := "LinKernighanTSP",
  version                        := "0.1.1",
  organization                   := "de.sciss",
  homepage                       := Some(url(s"https://git.iem.at/sciss/${name.value}")),
  licenses                       := Seq("MIT" -> url("https://raw.githubusercontent.com/Sciss/LinKernighanTSP/master/LICENSE")),
  fork                           := true,
  javaOptions  in run            += "-Xmx8G",
  connectInput in run            := true,
  outputStrategy                 := Some(StdoutOutput),
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(
    scalaVersion                := "2.13.1",
    crossScalaVersions          := Seq("2.13.1", "2.12.10"),
    mainClass in (Compile, run) := Some("de.sciss.tsp.Main")
  )
  .settings(publishSettings)

lazy val javaProject = project.in(file("java"))
  .settings(commonSettings)
  .settings(
    javacOptions in Compile        ++= Seq("-target", "1.8", "-source", "1.8"),
    javacOptions in (Compile, doc)  := Nil,
    crossScalaVersions              := Seq(scalaVersion.value),
    autoScalaLibrary                := false,
    mainClass in (Compile, run)     := Some("j.Main")
  )

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
    <scm>
      <url>git@git.iem.at:sciss/{n}.git</url>
      <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
    </scm>
      <developers>
        <developer>
          <id>Jilocasin</id>
          <name>Daniel Obermeier</name>
          <url>https://jilocasin.de/</url>
        </developer>
        <developer>
          <id>sciss</id>
          <name>Hanns Holger Rutz</name>
          <url>http://www.sciss.de</url>
        </developer>
      </developers>
  }
)
