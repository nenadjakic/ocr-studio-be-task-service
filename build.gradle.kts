import org.gradle.api.JavaVersion.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)

    id("application")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.openapi.gradle)
}

group = "com.github.nenadjakic.ocrstudio.task"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = VERSION_21
}

repositories {
    mavenCentral()
}
dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.modelmapper)
    implementation(libs.tika.core)
    implementation(libs.tika.parsers)

    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
    }
}

tasks.bootJar {
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}