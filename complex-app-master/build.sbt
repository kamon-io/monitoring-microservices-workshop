
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.12.2"
    )),
    resolvers += Resolver.bintrayRepo("kamon-io", "snapshots"),
    resolvers += Resolver.mavenLocal,
    name := "My Akka HTTP Project",

    libraryDependencies ++= Seq(
      "com.typesafe.akka"       %% "akka-http"              % "10.0.7"
      , "com.typesafe.akka"       %% "akka-http-spray-json"   % "10.0.9",

      "io.kamon" %% "kamon-logback"     % "1.0.0-RC1-1e03edb69f1a7e52c584e70664f86f2a7bc1a004"  exclude("io.kamon", "kamon-core_2.12"),

      "io.kamon" %% "kamon-scala"             % "1.0.0-RC4",
      "io.kamon" %% "kamon-akka-http"         % "1.0.0-RC4-37344ef58d5ec7bdec47f2c09919ad3dc1a37dc3",
      "io.kamon" %% "kamino-reporter"         % "1.0.0-RC3-22f24f7b41a12df29ec85b3248b8eceac001d610",
      "io.kamon" %% "kamon-system-metrics"    % "1.0.0-RC4",
      "io.kamon" %% "kamon-akka-remote-2.4"   % "1.0.0-RC4",
      "io.kamon" %% "kamon-jdbc"              % "1.0.0-RC4",
      "io.kamon" %% "kamon-jaeger"            % "1.0.0-RC4",
      "io.kamon" %% "kamon-prometheus"        % "1.0.0-RC4",


      "com.h2database" % "h2" % "1.3.148",

      "com.typesafe.akka"       %% "akka-slf4j"             % "2.4.19",
      "ch.qos.logback"           % "logback-classic"        % "1.1.3"
    )
  )
