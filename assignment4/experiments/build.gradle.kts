plugins {
    java
}

group = "org.omscs.ml.assignment4"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("de.siegmar:fastcsv:2.1.0")
    implementation("edu.brown.cs.burlap:burlap:3.0.1")
    implementation("org.omscs.ml.a4burlap:a4burlap:1.0-SNAPSHOT")
}


task<JavaExec>("QonlyDBAlpha") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.experiments.QonlyDBAlpha")
}
task<JavaExec>("QonlyGWlpha") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.experiments.QonlyGWAlpha")
}
task<JavaExec>("MySmGWGammaVIPI") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.experiments.MySmGWGammaVIPI")
}

task<JavaExec>("MySmBDGammaVIPI") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.experiments.MySmBDGammaVIPI")
}
task<JavaExec>("MyLrgBDIterVIPI") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.experiments.MyLrgBDIterVIPI")
}
task<JavaExec>("MyLrgGWGammaVIPI") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.experiments.MyLrgGWGammaVIPI")
}