enablePlugins(JavaAppPackaging)

val Http4sVersion = "0.23.13"
val LogbackVersion = "1.4.5"
val ScalaXmlVersion = "2.1.0"
val CirceVersion = "0.14.3"
val PureConfigVersion = "0.17.2"
// val LightbendConfigVersion = "1.4.2"
val Redis4CatsVersion = "1.3.0"
val Log4CatsVersion = "2.5.0"
val MUnitCatsVersion = "1.0.7"

lazy val root = project
  .in(file("."))
  .settings(
    name := "nbrb-xml-api",
    version := "0.4.2",
    scalaVersion := "3.2.1", // upgrade target dir in .github/worklow after scala version bump
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig-core" % PureConfigVersion,
      // "com.typesafe" % "config" % LightbendConfigVersion,
      "dev.profunktor" %% "redis4cats-effects" % Redis4CatsVersion,
      "dev.profunktor" %% "redis4cats-log4cats" % Redis4CatsVersion,
      "org.typelevel" %% "log4cats-slf4j" % Log4CatsVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-scala-xml" % Http4sVersion,
      "org.scala-lang.modules" %% "scala-xml" % ScalaXmlVersion,
      "org.typelevel" %% "munit-cats-effect-3" % MUnitCatsVersion % Test,
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
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
