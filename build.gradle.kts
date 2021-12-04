
plugins {
    application
    java
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

    test {
        // Use the built-in JUnit support of Gradle.
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    wrapper {
        gradleVersion = "7.3"
    }
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
}

application {
    mainClass.set((project.properties["execMainClass"] ?: "Day01Kt").toString())
}