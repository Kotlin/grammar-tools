import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    `maven-publish`
}

group = "org.jetbrains.kotlin.spec.grammar.tools"
version = "0.1"

val archivePrefix = "kotlin-grammar-tools"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin.spec.grammar:kotlin-grammar-parser:0.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = archivePrefix
            version = version as String

            from(components["java"])
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Jar> {
    archiveFileName.set("$archivePrefix-$archiveVersion.jar")

    manifest {
        attributes(
            mapOf(
                "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }
            )
        )
    }

    from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
