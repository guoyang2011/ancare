import com.github.retronym.SbtOneJar._

oneJarSettings

name := "ancare"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies += "com.changhong.flashbird" % "flashbird_2.10" % "0.1.12" withJavadoc() withSources()

libraryDependencies += "org.yaml" % "snakeyaml" % "1.5" withJavadoc() withSources()
