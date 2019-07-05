// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

val previousVersion = "0.1.0"

inThisBuild(Def.settings(
  crossScalaVersions := Seq("2.12.8", "2.10.7", "2.11.12", "2.13.0", "0.17.0-bin-SNAPSHOT"),
  scalaVersion := crossScalaVersions.value.head,
  version := "0.1.1-SNAPSHOT",
  organization := "org.portable-scala",

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

def modifyIf[T](p: => Boolean)(f: T => T): T => T = v => if (p) f(v) else v
def modifyIfInfer[T](p: => Boolean, f: T => T): T => T = v => if (p) f(v) else v

def dottyJSSettings = List(
)

def scala2JVMSettings = List(
)

lazy val `portable-scala-reflect` = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    scalacOptions in (Compile, doc) -= "-Xfatal-warnings",

    mimaPreviousArtifacts +=
      organization.value %%% moduleName.value % previousVersion,

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
    libraryDependencies := {
      val v = libraryDependencies.value
      val scalaOrgV = scalaOrganization.value
      val scalaVersionV = scalaVersion.value
      if (!isDotty.value) v :+ scalaOrgV % "scala-reflect" % scalaVersionV
      else v
    },

    unmanagedSourceDirectories in Compile := {
      val v = (unmanagedSourceDirectories in Compile).value
      if (isDotty.value) v :+ (baseDirectory.value / "src-dotty")
      else v :+ (baseDirectory.value / "src-scala2")
    },

    // Speed up compilation a bit. Our .java files do not need to see the .scala files.
    compileOrder := CompileOrder.JavaThenScala,

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )
  .jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin))
  .jsSettings(
    // Enable the Scala.js back-end of dotty.
    scalacOptions := {
      val opts = scalacOptions.value
      if (isDotty.value) opts :+ "-scalajs" else opts 
    },

    libraryDependencies := {
      val v = libraryDependencies.value
      val scalaVersionV = scalaVersion.value
      val scalaJSDottyVersion = "1.0.0-M7"
      if (isDotty.value) v
        // Remove the Scala.js compiler plugin for scalac
      . filterNot(_.name.startsWith("scalajs-compiler"))
      . map(_.withDottyCompat(scalaVersionV))

        // Replace the JVM JUnit dependency by the Scala.js one
      . filter(d => !Set("junit-interface", "scalajs-junit").exists(d.name.startsWith))
      .:+(("org.scala-js" %% "scalajs-junit-test-runtime" % scalaJSDottyVersion % "test").withDottyCompat(scalaVersionV))
      else v
    },

    // Typecheck the Scala.js IR found on the classpath
    scalaJSLinkerConfig := {
      val v = scalaJSLinkerConfig.value
      if (isDotty.value) v.withCheckIR(true) else v
    }
  )
