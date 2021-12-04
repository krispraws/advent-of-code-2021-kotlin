
plugins {
    application
    kotlin("jvm") version "1.5.10"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

application {
    mainClass.set((project.properties["execMainClass"] ?: "Day01Kt").toString())
}