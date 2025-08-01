plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "8.3.6"
}

group = "dev.keith.bots"
version = "1.2"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // JDA
    implementation("net.dv8tion:JDA:5.1.0") {
        // Optionally disable audio natives to reduce jar size by excluding `opus-java`
        // Kotlin DSL:
        // exclude(module="opus-java")
    }
    // logging
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    // driver
    implementation("com.mysql:mysql-connector-j:9.3.0")
    // hibernate
    implementation(platform("org.hibernate.orm:hibernate-platform:7.0.8.Final"))
    implementation("org.hibernate.orm:hibernate-core")
    implementation("com.zaxxer:HikariCP:7.0.0")

    // jakarta
    implementation("jakarta.transaction:jakarta.transaction-api")
    annotationProcessor("org.hibernate.orm:hibernate-processor:7.0.8.Final")
    // annotation
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("org.jetbrains:annotations:24.0.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "dev.keith.bots.Main"
}