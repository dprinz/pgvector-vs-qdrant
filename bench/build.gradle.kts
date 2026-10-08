plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.postgresql)
    implementation(libs.pgvector)
    implementation(libs.qdrant.client)
    // Reads the Parquet files of the dataset once (bench prepare).
    implementation(libs.duckdb.jdbc)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

application {
    mainClass = "demo.bench.Bench"
    // truth keeps all base vectors in memory: 999,000 x 1536 floats = 6.1 GB
    applicationDefaultJvmArgs = listOf("-Xmx12g", "--enable-native-access=ALL-UNNAMED")
}

tasks.named<JavaExec>("run") {
    workingDir = projectDir
    standardInput = System.`in`
}
