import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.HexFormat

plugins {
    java
    alias(libs.plugins.quarkus)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.bom))
    implementation(enforcedPlatform(libs.quarkus.langchain4j.bom))

    implementation(libs.quarkus.rest.jackson)
    implementation(libs.quarkus.agroal)
    implementation(libs.quarkus.jdbc.postgresql)
    implementation(libs.quarkus.narayana.jta)
    implementation(libs.quarkus.flyway)
    implementation(libs.quarkus.flyway.postgresql)
    implementation(libs.langchain4j.embeddings)
    // PGvector: binds a float[] as a typed parameter of type vector (see KnowledgeRepository).
    implementation(libs.pgvector)

    testImplementation(libs.quarkus.junit5)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

// Embedding model: intfloat/multilingual-e5-small as quantized ONNX export from Xenova, pinned to one revision and
// checked by SHA-256. Downloaded once into the Gradle user home and copied to build/embedding-model.
abstract class DownloadEmbeddingModel : DefaultTask() {
    @get:Input abstract val repository: Property<String>
    @get:Input abstract val revision: Property<String>
    @get:Input abstract val files: MapProperty<String, String>
    @get:Internal abstract val cacheDirectory: DirectoryProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun download() {
        val target = outputDirectory.get().asFile
        files.get().forEach { (path, expected) ->
            val name = path.substringAfterLast('/')
            val cached = cacheDirectory.get().asFile.resolve("${revision.get()}/$name")
            if (!cached.isFile || sha256(cached) != expected) {
                cached.parentFile.mkdirs()
                val url = URI("https://huggingface.co/${repository.get()}/resolve/${revision.get()}/$path").toURL()
                logger.lifecycle("Downloading $url")
                val partial = File(cached.parentFile, "$name.part")
                url.openStream().use { input -> partial.outputStream().use { input.copyTo(it) } }
                check(sha256(partial) == expected) { "SHA-256 mismatch for $name" }
                Files.move(partial.toPath(), cached.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            cached.copyTo(target.resolve(name), overwrite = true)
        }
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(1 shl 16)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return HexFormat.of().formatHex(digest.digest())
    }
}

val downloadEmbeddingModel by tasks.registering(DownloadEmbeddingModel::class) {
    description = "Downloads multilingual-e5-small (quantized ONNX) and checks its SHA-256."
    repository = "Xenova/multilingual-e5-small"
    revision = "761b726dd34fb83930e26aab4e9ac3899aa1fa78"
    files = mapOf(
        "onnx/model_quantized.onnx" to "f80102d3f2a1229f387d3c81909990d8945513e347b0eab049f7de3c6f98c193",
        "tokenizer.json" to "0b44a9d7b51c3c62626640cda0e2c2f70fdacdc25bbbd68038369d14ebdf4c39",
    )
    cacheDirectory = layout.dir(provider { gradle.gradleUserHomeDir.resolve("caches/pgvector-vs-qdrant/models") })
    outputDirectory = layout.buildDirectory.dir("embedding-model")
}

// Corpus and questions live at the repository root and are shipped as resources.
tasks.named<ProcessResources>("processResources") {
    from(rootProject.layout.projectDirectory.dir("knowledge")) { into("knowledge") }
    from(rootProject.layout.projectDirectory.dir("evaluation")) { into("evaluation") }
    dependsOn(downloadEmbeddingModel)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    // The ONNX runtime loads a native library.
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.named<Test>("test") {
    useJUnitPlatform { excludeTags("evaluation") }
}

// ./gradlew :rag-app:evaluate writes build/reports/evaluation.md
val evaluate by tasks.registering(Test::class) {
    description = "Runs the retrieval evaluation against a Dev Services PostgreSQL."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform { includeTags("evaluation") }
    outputs.upToDateWhen { false }
    testLogging { showStandardStreams = true }
}

tasks.named("quarkusDev") { dependsOn(downloadEmbeddingModel) }
