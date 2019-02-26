import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
    `maven-publish`
}

group = "org.jetbrains.kotlin"
version = "0.1"

val archivePrefix = "kotlin-grammar-tools"
val jar: Jar by tasks
jar.archiveName = "$archivePrefix.jar"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
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
