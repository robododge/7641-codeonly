
  
plugins {
    java
}

group = "org.omscs.sdodge6"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("de.siegmar:fastcsv:2.1.0")
    implementation("edu.brown.cs.burlap:burlap:3.0.1")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

task<JavaExec>("helloGridWorld") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "a4.HelloGridWorld"
}

task<JavaExec>("plotTest") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "a4.PlotTest"
}

