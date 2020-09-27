enablePlugins(JavaAppPackaging)

val Http4sVersion = "0.21.7"
val LogbackVersion = "1.2.3"
val ScalaXmlVersion = "1.3.0"
val CirceVersion = "0.13.0"
val PureConfigVersion = "0.13.0"
val RedisVersion = "3.30"
val Redis4CatsVersion = "0.10.3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nbrb-xml-api",
    version := "0.2.0",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-scala-xml" % Http4sVersion,
      "org.scala-lang.modules" %% "scala-xml" % ScalaXmlVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "dev.profunktor" %% "redis4cats-effects" % Redis4CatsVersion
    )
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  // "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)
