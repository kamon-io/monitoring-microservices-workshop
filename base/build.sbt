scalaVersion := "2.12.2"

resolvers += Resolver.bintrayRepo("kamon-io", "releases")
resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")

libraryDependencies += "io.kamon" %% "kamon-core" % "1.0.0-RC4"
libraryDependencies += "io.kamon" %% "kamon-jaeger" % "1.0.0-RC4"
libraryDependencies += "io.kamon" %% "kamon-scala" % "1.0.0-RC4"
libraryDependencies += "io.kamon" %% "kamon-akka-2.5" % "1.0.0-RC4"
