enablePlugins(JavaAppPackaging)

val Http4sVersion = "0.21.4"
val LogbackVersion = "1.2.3"
val ScalaXmlVersion = "1.3.0"
val CirceVersion = "0.13.0"
val PureConfigVersion = "0.12.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nbrb-xml-api",
    version := "0.1.0",

    scalaVersion := "2.13.2",

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-generic-extras" % CirceVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.scala-lang.modules" %% "scala-xml" % ScalaXmlVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
    )
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  // "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
