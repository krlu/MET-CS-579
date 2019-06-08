name := "MET-CS-579"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "3.3.4",
  "org.scalikejdbc" %% "scalikejdbc-config" % "3.3.4",
  "com.h2database" % "h2" % "1.4.199",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3",
  "org.postgresql" % "postgresql" % "42.2.5"
)