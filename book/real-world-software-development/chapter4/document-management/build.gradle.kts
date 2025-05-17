plugins {
    id("java")
}

group = "project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // JUnit 4
    testImplementation("junit:junit:4.13.2")

    // Hamcrest
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.test {
    useJUnit()
}
