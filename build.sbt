ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "VerveDataEngineeringChallenge"
  )

val sparkVersion = "3.3.0"
val typeSafeVersion = "1.4.1"
val betterFiles = "3.9.1"
val playJson = "2.9.2"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "com.typesafe" % "config" % typeSafeVersion,
  "com.github.pathikrit" %% "better-files" % betterFiles,
  "com.typesafe.play" %% "play-json" % playJson
)