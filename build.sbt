lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion = "2.5.11"
lazy val scalikejdbcVersion = "3.2.3"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      scalaVersion    := "2.12.6"
    )),
    name := "mtproto",
    libraryDependencies ++= Seq(
      "ch.qos.logback"    %  "logback-classic"           % "1.2.3",


      "com.typesafe.akka" %% "akka-stream" % "2.5.9",
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.9",
      "com.typesafe"      %  "config"                    % "1.3.2",

      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
      "org.scodec" %% "scodec-core" % "1.10.3",
      "org.scodec" %% "scodec-bits" % "1.1.6"
    )
  )
