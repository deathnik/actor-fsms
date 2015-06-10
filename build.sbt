name := "AktorPatternMatch"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.9"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.3"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1"

libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.0"

libraryDependencies += "org.apache.opennlp" % "opennlp-tools" % "1.5.3"


javaOptions in run += "-Xmx1G"