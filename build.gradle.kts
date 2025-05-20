import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("jacoco")
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.8.22"
}

val exclusions = listOf(
    "com/sage/sage/microservices/device/repository/**",
    "com/sage/sage/microservices/user/repository/**",
    "com/sage/sage/microservices/music/repository/**",
    "com/sage/sage/microservices/music/model/**",
    "com/sage/sage/microservices/user/model/**",
    "com/sage/sage/microservices/exception/exceptionobjects/**",
    "com/sage/sage/microservices/config/azure/**",
    "com/sage/sage/microservices/user/email/**",
)
group = "com.sage"
version = "0.0.1-SNAPSHOT"

jacoco {
    toolVersion = "0.8.7" // Adapt version based on your Gradle and Jacoco versions
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test:3.4.10")
    implementation("com.azure:azure-cosmos:4.49.0")
    implementation("com.azure:azure-communication-email:1.0.2")
    compileOnly("org.projectlombok:lombok:1.18.30")


    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework.boot:spring-context-support:5.2.8.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    implementation("io.micrometer:micrometer-registry-datadog")

    implementation("com.google.firebase:firebase-admin:9.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")


}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
    finalizedBy("jacocoTestReport")
}

sourceSets {
    getByName("main").java.srcDirs("src/main/java")
    getByName("test").java.srcDirs("src/test/java")
}

tasks.withType<JacocoReport> {
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map { file ->
            fileTree(file).apply {
                exclude(exclusions)
            }
        }))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.50.toBigDecimal()
                counter = "LINE"
            }
        }
    }
}

tasks {
    named("build") {
        dependsOn(test)
        dependsOn(jacocoTestCoverageVerification)
    }
}
