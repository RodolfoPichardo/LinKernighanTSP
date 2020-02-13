lazy val root = project.in(file("."))
  .settings(
    name                           := "LinKernighanTSP",
    version                        := "0.1.0-SNAPSHOT",
    organization                   := "de.sciss",
    homepage                       := Some(url(s"https://github.com/Sciss/${name.value}")),
    licenses                       := Seq("MIT" -> url("https://raw.githubusercontent.com/Sciss/LinKernighanTSP/master/LICENSE")),
    scalaVersion                   := "2.13.1",
//    crossPaths                     := false,
    javacOptions in Compile       ++= Seq("-target", "1.8", "-source", "1.8"),
    javacOptions in (Compile, doc) := Nil,
//    autoScalaLibrary               := false,
//    testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a"))
    mainClass in (Compile, run) := Some("Main")
  )
  .settings(publishSettings)

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
      <url>git@github.com:Sciss/{n}.git</url>
      <connection>scm:git:git@github.com:Sciss/{n}.git</connection>
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
