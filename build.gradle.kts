plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "8.3.6"
}

group = "dev.keith.bots"
version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.dv8tion:JDA:5.1.0") {
        // Optionally disable audio natives to reduce jar size by excluding `opus-java`
        // Kotlin DSL:
        // exclude(module="opus-java")
    }
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.13")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "dev.keith.bots.Main"
}