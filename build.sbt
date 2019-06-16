name := "MET-CS-579"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.4",
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.apache.opennlp" % "opennlp-tools" % "1.9.1",
  "log4j" % "log4j" % "1.2.17"
)