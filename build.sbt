// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

inThisBuild(Def.settings(
  crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.6"),
  scalaVersion := crossScalaVersions.value.last,
  version := "0.1.0-SNAPSHOT",

  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-encoding",
    "utf-8",
    "-Xfatal-warnings",
  ),

  homepage := Some(url("https://github.com/portable-scala/portable-scala-reflect")),
  licenses += ("BSD New",
    url("https://github.com/portable-scala/portable-scala-reflect/blob/master/LICENSE")),
  scmInfo := Some(ScmInfo(
    url("https://github.com/portable-scala/portable-scala-reflect"),
    "scm:git:git@github.com:portable-scala/portable-scala-reflect.git",
    Some("scm:git:git@github.com:portable-scala/portable-scala-reflect.git"))),
))

lazy val `portable-scala-reflect` = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    scalacOptions in (Compile, doc) -= "-Xfatal-warnings",

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <developers>
        <developer>
          <id>sjrd</id>
          <name>Sébastien Doeraene</name>
          <url>https://github.com/sjrd/</url>
        </developer>
        <developer>
          <id>gzm0</id>
          <name>Tobias Schlatter</name>
          <url>https://github.com/gzm0/</url>
        </developer>
        <developer>
          <id>densh</id>
          <name>Denys Shabalin</name>
          <url>https://github.com/densh/</url>
        </developer>
      </developers>
    ),
    pomIncludeRepository := { _ => false },
  )
  .jvmSettings(
    // Macros
    libraryDependencies += scalaOrganization.value % "scala-reflect" % scalaVersion.value,

    // Speed up compilation a bit. Our .java files do not need to see the .scala files.
    compileOrder := CompileOrder.JavaThenScala,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
