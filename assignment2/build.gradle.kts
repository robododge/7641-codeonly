plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    implementation( files("${rootDir}/flatjars/ABAGAIL.jar"))
    implementation(project(":abagail-module"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("de.siegmar:fastcsv:2.0.0")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

task<JavaExec>("runKColor") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "testruns.MaxKColoring"
}

task<JavaExec>("runFlipFlop") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "testruns.FlipflopFirst"
}