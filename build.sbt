lazy val root = project
  .in(file("."))
  .settings(
    name := "lwjgl-examples",
    version := "0.1.0",
    scalaVersion := "0.27.0-RC1",

    libraryDependencies ++= {
      val groupdID = "org.lwjgl"
      val artifactIDs = Seq(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl"
      )
      val revision = "3.2.3"
      val platforms = Seq(
        "linux",
        "macos",
        "windows"
      )

      for (artifactID <- artifactIDs; platform <- platforms) yield Seq(
        groupdID % artifactID % revision,
        groupdID % artifactID % revision classifier s"natives-$platform"
      )
    }.flatten
  )
