import Dependencies._
import play.sbt.PlayImport
import sbt.Keys._

val codeguruURI =
  "https://repo1.maven.org/maven2/software/amazon/codeguruprofiler/codeguru-profiler-java-agent-standalone/1.1.0/codeguru-profiler-java-agent-standalone-1.1.0.jar"

// the application
lazy val hat = project
  .in(file("."))
  .settings(dockerSettings)
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb, SbtSassify, SbtGzip, SbtDigest)
  .enablePlugins(BasicSettings)
  .enablePlugins(SlickCodeGeneratorPlugin)
  .disablePlugins(PlayLogback)
  .settings(
    libraryDependencies ++= Seq(
          PlayImport.ehcache,
          PlayImport.filters,
          PlayImport.guice,
          PlayImport.ws,
          DsLib.Adjudicator,
          DsLib.DexClient,
          DsLib.PlayCommon,
          DsLib.RedisCache,
          DsLib.SilhouetteCryptoJca,
          DsLib.SilhouettePasswordBcrypt,
          DsLib.SilhouettePersistence,
          DsLib.SlickPostgresDriver,
          Lib.AwsV1Sdk,
          Lib.BouncyCastle,
          Lib.Ficus,
          Lib.Guard,
          Lib.Nbvcxz,
          Lib.PlayJson,
          Lib.PlayJsonJoda,
          Lib.PlaySlick,
          Lib.PlayTest,
          Lib.PrometheusFilter,
          Lib.ScalaGuice,
          LocalThirdParty.AlpakkaAwsLambda,
          LocalThirdParty.CirceConfig,
          LocalThirdParty.PrettyTime,
          DsLib.IntegrationTestCommon          % Test,
          DsLib.SilhouetteTestkit              % Test,
          Lib.ScalaTestScalaCheck              % Test,
          LocalThirdParty.ScalaTestplusMockito % Test
        ),
    Test / parallelExecution := false,
    Assets / pipelineStages := Seq(digest),
    Assets / sourceDirectory := baseDirectory.value / "app" / "org" / "hatdex" / "hat" / "phata" / "assets",
    update / aggregate := false,
    Global / cancelable := false, // Workaround sbt/bug#4822 Unable to Ctrl-C out of 'run' in a Play app
    TwirlKeys.templateImports := Seq(),
    play.sbt.routes.RoutesKeys.routesImport := Seq.empty,
    routesGenerator := InjectedRoutesGenerator,
    Global / concurrentRestrictions += Tags.limit(Tags.Test, 1),
    coverageExcludedPackages := """.*\.controllers\..*Reverse.*;router.Routes.*;org.hatdex.hat.dal.Tables.*;org.hatdex.hat.phata.views.*;controllers.javascript\..*""",
    // Do not publish docs and source in compiled package
    Compile / doc / sources := Seq.empty,
    Compile / packageDoc / publishArtifact := false,
    Compile / packageSrc / publishArtifact := false
  )
  .settings(
    gentables / codegenPackageName := "org.hatdex.hat.dal",
    gentables / codegenBaseDir := (baseDirectory.value / "app").getCanonicalPath,
    gentables / codegenClassName := "Tables",
    gentables / codegenExcludedTables := Seq("databasechangelog", "databasechangeloglock"),
    gentables / codegenDatabase := "devdb",
    gentables / codegenConfig := "dev.conf",
    gentables / codegenEvolutions := "devhatMigrations"
  )
  .settings(
    // Omit Tables.scala for scalafix, since it is autogenerated
    Compile / scalafix / unmanagedSources := (Compile / unmanagedSources).value
          .filterNot(
            _.getAbsolutePath.contains(
              "dal/Tables.scala"
            )
          ),
    // Omit Tables.scala for scalafmt, since it is autogenerated
    Compile / scalafmt / unmanagedSources := (Compile / unmanagedSources).value
          .filterNot(
            _.getAbsolutePath.contains(
              "dal/Tables.scala"
            )
          )
  )

// Enable the semantic DB for scalafix
inThisBuild(
  List(
    scalafixScalaBinaryVersion := "2.13",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
  )
)
