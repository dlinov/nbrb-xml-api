enablePlugins(JavaAppPackaging)

val Http4sVersion = "0.23.6"
val LogbackVersion = "1.2.6"
val ScalaXmlVersion = "2.0.1"
val CirceVersion = "0.14.1"
//val PureConfigVersion = "0.17.0"
val LightbendConfigVersion = "1.4.1"
val Redis4CatsVersion = "1.0.0"
val Log4CatsVersion = "2.1.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nbrb-xml-api",
    version := "0.4.0",
    scalaVersion := "3.0.2",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
//      "com.github.pureconfig" %% "pureconfig-core" % PureConfigVersion,
      "com.typesafe" % "config" % LightbendConfigVersion,
      "dev.profunktor" %% "redis4cats-effects" % Redis4CatsVersion,
      "org.typelevel" %% "log4cats-slf4j" % Log4CatsVersion,
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
  "-Xfatal-warnings"
)

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", "io.netty.versions.properties") =>
    MergeStrategy.filterDistinctLines
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
