import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL
import java.nio.charset.StandardCharsets

plugins {
    kotlin("jvm") version "1.3.11"
    antlr
    `maven-publish`
}

group = "org.jetbrains.kotlin"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    antlr("org.antlr:antlr4:4.7.1")
    compile(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlin.spec.grammar:kotlin-grammar-parser:0.1")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "kotlin-grammar-tools"
            version = version as String

            from(components["java"])
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<AntlrTask> {
    outputDirectory = File("src/main/java/org/jetbrains/kotlin/grammar/parser").also { it.mkdirs() }
    arguments.add("-package")
    arguments.add("org.jetbrains.kotlin.grammar.parser")
}

tasks.create("updateGrammar") {
    doFirst {
        val grammarUrlPrefix = "https://raw.githubusercontent.com/JetBrains/kotlin-spec/master/src/grammar/"
        val grammarLocalPathPrefix = "src/main/antlr/".also { File(it).mkdirs() }

        setOf("UnicodeClasses.g4", "KotlinLexer.g4", "KotlinParser.g4").forEach {
            File(grammarLocalPathPrefix + it).writeText(
                String(URL(grammarUrlPrefix + it).openStream().readBytes(), StandardCharsets.UTF_8)
            )
        }
    }
}
