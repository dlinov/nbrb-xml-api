enablePlugins(JavaAppPackaging)

val Http4sVersion = "0.21.15"
val LogbackVersion = "1.2.3"
val ScalaXmlVersion = "1.3.0"
val CirceVersion = "0.13.0"
val PureConfigVersion = "0.14.0"
val Redis4CatsVersion = "0.11.1"
val Log4CatsVersion = "1.1.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nbrb-xml-api",
    version := "0.3.2",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "dev.profunktor" %% "redis4cats-effects" % Redis4CatsVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % Log4CatsVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-scala-xml" % Http4sVersion,
      "org.scala-lang.modules" %% "scala-xml" % ScalaXmlVersion
    )
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-feature",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)
