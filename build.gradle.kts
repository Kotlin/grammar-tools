import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
    `maven-publish`
}

group = "org.jetbrains.kotlin.spec.grammar.tools"
version = "0.1"

val archivePrefix = "kotlin-grammar-tools"
val jar: Jar by tasks

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlin.spec.grammar:kotlin-grammar-parser:0.1")
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
    kotlinOptions.jvmTarget = "1.8"
}

jar.archiveName = "$archivePrefix-$version.jar"

jar.manifest {
    attributes(
        mapOf(
            "Class-Path" to configurations.runtime.files.joinToString(" ") { it.name }
        )
    )
}

jar.from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
