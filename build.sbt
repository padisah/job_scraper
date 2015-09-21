name := "ScalaExtractors"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "commons-httpclient" % "commons-httpclient" % "3.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

mainClass in assembly := Some("com.padicrawler.extractors.ExtractJobsFromKite")
