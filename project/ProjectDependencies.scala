/*
 * Copyright 2015 - 2016 Forever High Tech <http://www.foreverht.com> - all rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sbt._

object ProjectDependencyVersions {
  val AkkaVersion = "2.4.14"
  val EventuateVersion = "v0.8-SNAPSHOT"
  val RxTalkLibraryVersion = "1.0.0-SNAPSHOT"

  val SlickVersion = "3.1.1"
  val SqliteVersion = "3.15.1"
  val SpringVersion = "4.3.2.RELEASE"

  val ZooKeeperVersion = "3.4.9"
  val CuratorVersion = "3.2.1"
  val CassandraVersion = "3.4"
  val Log4jVersion = "2.5"
  val ProtobufVersion = "2.5.0"

  val OkHttpVersion = "3.5.0"
  val JacksonVersion = "2.8.5"
  val Json4sVersion = "3.5.0"

  val SprayJsonVersion = "1.3.2"
}

object ProjectDependencies {

  import ProjectDependencyVersions._

  val CassandraDriver =      "com.datastax.cassandra"     % "cassandra-driver-core"     % "3.0.2"
  val CassandraConnector =   "com.datastax.spark"        %% "spark-cassandra-connector" % "1.6.0-M2"
  val Javaslang =            "io.javaslang"               % "javaslang"                 % "2.0.5"
  val JunitInterface =       "com.novocode"               % "junit-interface"           % "0.11"

  val AkkaActor =            "com.typesafe.akka"         %% "akka-actor"                % AkkaVersion
  val AkkaRemote =           "com.typesafe.akka"         %% "akka-remote"               % AkkaVersion
  val AkkaCluster =          "com.typesafe.akka"         %% "akka-cluster"              % AkkaVersion
  val AkkaClusterSharding =  "com.typesafe.akka"         %% "akka-cluster-sharding"     % AkkaVersion
  val AkkaStream =           "com.typesafe.akka"         %% "akka-stream"               % AkkaVersion
  val AkkaStreamTestkit =    "com.typesafe.akka"         %% "akka-stream-testkit"       % AkkaVersion
  val AkkaTestkit =          "com.typesafe.akka"         %% "akka-testkit"              % AkkaVersion
  val AkkaTestkitMultiNode = "com.typesafe.akka"         %% "akka-multi-node-testkit"   % AkkaVersion

  val Eventuate =            "com.rbmhtechnology"        %% "eventuate"                 % EventuateVersion
  val EventuateCore =        "com.rbmhtechnology"        %% "eventuate-core"            % EventuateVersion
  val EventuateCrdt =        "com.rbmhtechnology"        %% "eventuate-crdt"            % EventuateVersion
  val EventuateLeveldb =     "com.rbmhtechnology"        %% "eventuate-log-leveldb"     % EventuateVersion
  val EventuateCassandra =   "com.rbmhtechnology"        %% "eventuate-log-cassandra"   % EventuateVersion
  val EventuateStream =      "com.rbmhtechnology"        %% "eventuate-adapter-stream"  % EventuateVersion
  val EventuateSpark =       "com.rbmhtechnology"        %% "eventuate-adapter-spark"   % EventuateVersion


  val Slick =                "com.typesafe.slick"        %% "slick"                     % SlickVersion
  val Sqlite =               "org.xerial"                 % "sqlite-jdbc"               % SqliteVersion
  val SpringContext =        "org.springframework"        % "spring-context"            % SpringVersion

  val ZooKeeper =            "org.apache.zookeeper"       % "zookeeper"                 % ZooKeeperVersion
  val CuratorFramework =     "org.apache.curator"         % "curator-framework"         % CuratorVersion exclude("org.apache.zookeeper", "zookeeper")
  val CuratorRecipes =       "org.apache.curator"         % "curator-recipes"           % CuratorVersion exclude("org.apache.zookeeper", "zookeeper")

  val CommonsIo =            "commons-io"                 % "commons-io"                % "2.4"
  val CassandraClientUtil =  "org.apache.cassandra"       % "cassandra-clientutil"      % CassandraVersion
  val Log4jApi =             "org.apache.logging.log4j"   % "log4j-api"                 % Log4jVersion
  val Log4jCore =            "org.apache.logging.log4j"   % "log4j-core"                % Log4jVersion
  val Log4jSlf4j =           "org.apache.logging.log4j"   % "log4j-slf4j-impl"          % Log4jVersion
  val CassandraUnit =        "org.cassandraunit"          % "cassandra-unit"            % "3.0.0.1"
  val Leveldb =              "org.fusesource.leveldbjni"  % "leveldbjni-all"            % "1.8"
  val Sigar =                "org.fusesource"             % "sigar"                     % "1.6.4"
  val Java8Compat =          "org.scala-lang.modules"    %% "scala-java8-compat"        % "0.8.0"
  val Scalatest =            "org.scalatest"             %% "scalatest"                 % "3.0.0"
  val Scalaz =               "org.scalaz"                %% "scalaz-core"               % "7.2.7"
  val OkHttp =               "com.squareup.okhttp3"       % "okhttp"                    % OkHttpVersion
  val JacksonDatabind =      "com.fasterxml.jackson.core" % "jackson-databind"          % JacksonVersion

  val Json4sNative =         "org.json4s"                %% "json4s-native"             % Json4sVersion
  val Json4sJackson =        "org.json4s"                 % "json4s-jackson_2.11"       % Json4sVersion

  val RxTalkLibrary =        "im.rxtalk"                 %% "rxtalk-library"            % RxTalkLibraryVersion
  val SprayJson =            "io.spray"                   % "spray-json_2.11"           % SprayJsonVersion
}

