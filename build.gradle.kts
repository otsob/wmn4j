plugins {
    java
    `java-library`
    checkstyle
}

group = "org.wmn4j"
description = "A Java library for handling western music notation."

java {
    sourceCompatibility = JavaVersion.VERSION_17

    sourceSets["main"].java {
        srcDir("doc/examples")
    }

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

val createProjectPropertiesFile by tasks.registering {
    dependsOn(tasks.processResources)
    doLast {
        val versionNumber = project.version.toString()
        println("Creating properties file for ${project.name} version $versionNumber")
        val buildDir = getLayout().buildDirectory.get().toString()
        val outputFile = file("$buildDir/resources/main/wmn4j.properties")
        outputFile.parentFile.mkdirs()
        outputFile.writeText("version=$versionNumber\n")
    }
}

tasks.classes {
    dependsOn(createProjectPropertiesFile)
}