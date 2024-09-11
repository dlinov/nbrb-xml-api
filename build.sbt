enablePlugins(JavaAppPackaging)

val Http4sVersion = "0.23.28"
val Http4sBlazeVersion = "0.23.16"
val Http4sScalaXmlVersion = "0.23.14"
val LogbackVersion = "1.5.8"
val ScalaXmlVersion = "2.3.0"
val CirceVersion = "0.14.10"
val PureConfigVersion = "0.17.7"
val Redis4CatsVersion = "1.7.1"
val Log4CatsVersion = "2.7.0"
val MUnitCatsVersion = "2.0.0"
val JsoupVersion = "1.18.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nbrb-xml-api",
    version := "0.4.4",
    scalaVersion := "3.5.0", // upgrade target dir in .github/worklow after scala version bump
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig-core" % PureConfigVersion,
      "dev.profunktor" %% "redis4cats-effects" % Redis4CatsVersion,
      "dev.profunktor" %% "redis4cats-log4cats" % Redis4CatsVersion,
      "org.typelevel" %% "log4cats-slf4j" % Log4CatsVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sBlazeVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sBlazeVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-scala-xml" % Http4sScalaXmlVersion,
      "org.jsoup" % "jsoup" % JsoupVersion,
      "org.scala-lang.modules" %% "scala-xml" % ScalaXmlVersion,
      "org.typelevel" %% "munit-cats-effect" % MUnitCatsVersion % Test
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
  case PathList("module-info.class") =>
    MergeStrategy.first
  case path if path.endsWith("/module-info.class") =>
    MergeStrategy.first
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
