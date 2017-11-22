import ProjectDependencies._
import ProjectSettings._
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

name := "logback4s"

version in ThisBuild := "1.0.0-SNAPSHOT"

organization in ThisBuild := "logback4s"

scalaVersion in ThisBuild := "2.11.11"

javacOptions in ThisBuild ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

externalResolvers in ThisBuild := Resolver.withDefaultResolvers(Seq.empty, mavenCentral = true)

isSnapshot in ThisBuild := true

//fork in ThisBuild := true

lazy val aggregateProjects: Seq[ProjectReference] = Seq(
  core,
  flume,
  fluentd,
  logstash
)

lazy val core = (project in file("logback4s-core"))
  .settings(name := "logback4s-core")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(Disruptor))
  .settings(libraryDependencies ++= Seq(LogbackCore))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val flume = (project in file("logback4s-flume"))
  .settings(name := "logback4s-flume")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Flume))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val fluentd = (project in file("logback4s-fluentd"))
  .settings(name := "logback4s-fluentd")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val logstash = (project in file("logback4s-logstash"))
  .settings(name := "logback4s-logstash")
  .settings(commonSettings: _*)
  .settings(integrationTestSettings: _*)
  .settings(libraryDependencies ++= Seq(LogbackClassic))
  .settings(libraryDependencies ++= Seq(Json4sJackson, Json4sExt))
  .settings(libraryDependencies ++= Seq(Javaslang % "test", JunitInterface % "test", Scalatest % "test,it"))
  .settings(integrationTestPublishSettings: _*)
  .dependsOn(core)
  .configs(IntegrationTest, MultiJvm)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)

lazy val root = (project in file("."))
  .aggregate(aggregateProjects: _*)
  .settings(name := "logback4s")
  .settings(commonSettings: _*)
  .settings(documentationSettings: _*)
  .enablePlugins(HeaderPlugin, AutomateHeaderPlugin)
