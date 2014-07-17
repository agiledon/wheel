import _root_.sbt.Keys._
import _root_.sbt.TestFrameworks
import _root_.sbt.Tests
import _root_.scala._

name := """wheel"""

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "ch.qos.logback"      % "logback-classic"  % "1.0.13",
  "org.scalatest"      %% "scalatest"        % "2.1.0"        % "test",
  "com.novocode"        % "junit-interface"  % "0.7"          % "test->default"
)

unmanagedJars in Compile ++=
  (file("project/lib/") * "*.jar").classpath

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")