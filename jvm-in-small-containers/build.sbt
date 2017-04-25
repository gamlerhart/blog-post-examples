
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "info.gamlor.blog",
      scalaVersion := "2.12.1",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "useless-demo-service",
    libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "9.4.3.v20170317",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    assemblyJarName in assembly := "useless-demo-service.jar"
  )
