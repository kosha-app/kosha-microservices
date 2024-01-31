import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("jacoco")
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

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
    implementation("com.azure:azure-cosmos:4.49.0")
    implementation("com.azure:azure-communication-email:1.0.2")
    compileOnly("org.projectlombok:lombok:1.18.30")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework.boot:spring-context-support:5.2.8.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-mail")




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

sourceSets {
    getByName("main").java.srcDirs("src/main/java")
    getByName("test").java.srcDirs("src/test/java")
}

tasks.withType<JacocoReport> {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map { file ->
            fileTree(file).apply {
//                exclude("com/example/SomeClass.class", "com/example/somepackage/**")
            }
        }))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.2.toBigDecimal()
                counter = "LINE"
            }
        }
    }
}

tasks {
    named("build") {
        dependsOn(jacocoTestCoverageVerification)
    }
}
