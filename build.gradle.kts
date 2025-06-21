import org.jreleaser.model.Active

plugins {
    java
    `java-library`
    checkstyle
    `maven-publish`
    signing
    id("org.jreleaser") version "1.17.0"
}

group = "org.wmn4j"
description = "A Java library for handling western music notation."

java {
    sourceCompatibility = JavaVersion.VERSION_21

    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.apache.commons:commons-math3:3.6.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.slf4j:slf4j-jdk14:1.7.36")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

checkstyle {
    configDirectory = file("build-resources/checkstyle/")
}

tasks.named<Checkstyle>("checkstyleTest") {
    enabled = false
}

tasks.named<Javadoc>("javadoc") {
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        addBooleanOption("notimestamp", true)
    }
    exclude("doc/examples/**")
}

val buildDir = getLayout().buildDirectory.get().toString()

val createProjectPropertiesFile by tasks.registering {
    dependsOn(tasks.processResources)
    doLast {
        val versionNumber = project.version.toString()
        println("Creating properties file for ${project.name} version $versionNumber")
        val outputFile = file("$buildDir/resources/main/wmn4j.properties")
        outputFile.parentFile.mkdirs()
        outputFile.writeText("version=$versionNumber\n")
    }
}

tasks.classes {
    dependsOn(createProjectPropertiesFile)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            artifactId = project.name
            from(components["java"])

            pom {
                name = project.name
                description = project.description
                url = "http://www.wmn4j.org"
                licenses {
                    license {
                        name = "MIT license"
                    }
                }
                developers {
                    developer {
                        name = "Otso Björklund"
                    }
                    developer {
                        name = "Matias Wargelin"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/otsob/wmn4j.git"
                    developerConnection = "scm:git:ssh://git@github.com:otsob/wmn4j.git"
                    url = "https://github.com/otsob/wmn4j"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/staging-deploy")
        }
    }
}

jreleaser {
    project {
        versionPattern = "SEMVER"

        snapshot {
            pattern = ".*-SNAPSHOT"
        }

    }

    release {
        github {
            enabled = true
            tagName = project.name.get() + "-" + version
            repoUrl = "https://github.com/otsob/wmn4j"
            branch = "main"
        }
    }
    signing {
        active = Active.ALWAYS
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("$buildDir/staging-deploy")
                }
            }
        }
    }
}

