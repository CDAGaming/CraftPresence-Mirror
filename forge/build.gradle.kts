import xyz.wagyourtail.unimined.api.task.ExportMappingsTask
import xyz.wagyourtail.unimined.api.task.RemapJarTask

/**
 * Retrieve a Project Property
 */
operator fun String.invoke(): String? {
    return project.properties[this] as String?
}

val isLegacy: Boolean by extra
val protocol: Int by extra
val isJarMod: Boolean by extra
val accessWidenerFile: File by extra
val isMCPJar: Boolean by extra
val isModern: Boolean by extra
val fmlName: String by extra
val versionFormat: String by extra
val versionLabel: String by extra
val mcVersionLabel: String by extra
val mcMappingsType: String by extra
val canUseATs: Boolean by extra
val baseVersionLabel: String by extra

unimined.minecraft {
    if (!isJarMod) {
        minecraftForge {
            if (canUseATs) {
                accessTransformer(aw2at(accessWidenerFile))
            }
            loader("forge_version"()!!)
            customSearge = (mcMappingsType != "mojmap")
        }
        // Note: Required for proper mixin remaps
        mods {
            modImplementation {
                mixinRemap("UNIMINED")
            }
        }
    } else {
        jarMod {}
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

dependencies {
    if (isJarMod) {
        "jarMod"("risugami:modloader:${"forge_version"()}")
    }

    "common"(project(path = ":common")) { isTransitive = false }
    "common"(project(path = ":common", configuration = "shade"))
    "common"(project(path = ":common", configuration = "runtime"))
    "shadowCommon"(project(path = ":common", configuration = "shadeOnly"))
    "shadowCommon"(project(path = ":common")) { isTransitive = false }
}

val resourceTargets = listOf(
    "mcmod.info", "META-INF/mods.toml", "mod_${"mod_name"()}.info"
)
val replaceProperties = mapOf(
    "version" to baseVersionLabel,
    "mcversion" to mcVersionLabel,
    "fml_version_range" to "fml_version_range"(),
    "game_version_range" to "forge_game_version_range"(),
    "loader_version_range" to "forge_loader_version_range"()
)

tasks.processResources {
    inputs.properties(replaceProperties)

    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }

    filesMatching("mappings-$fmlName.srg") {
        filter { line ->
            @Suppress("NULL_FOR_NONNULL_TYPE")
            if (line.startsWith("CL:")) line else null
        }
    }
}

tasks.named<ExportMappingsTask>("exportMappings") {
    export {
        setTargetNamespaces(listOf(if ("mc_mappings_type"() == "retroMCP") "mcp" else "searge"))
        setSourceNamespace("official")
        location = file("$projectDir/src/main/resources/mappings-$fmlName.srg")
        setType("SRG")
    }
}

tasks.processResources.get().dependsOn(tasks.named<ExportMappingsTask>("exportMappings"))

tasks.shadowJar {
    mustRunAfter(project(":common").tasks.shadowJar)
    dependsOn(project(":common").tasks.shadowJar)
    from(zipTree(project(":common").tasks.shadowJar.get().archiveFile))
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")

    // Meta Exclusions
    exclude("**/DEPENDENCIES*")
    exclude("**/LICENSE*")
    exclude("**/Log4J*")
    exclude("META-INF/NOTICE*")
    exclude("META-INF/versions/**")

    // JUnixSocket exclusions:
    // libs
    // discord doesn't support bsd or sun
    exclude("lib/*BSD*/**")
    exclude("lib/*Sun*/**")
    // we don"t use junixsocket on windows
    exclude("lib/*Window*/**")
    // include only arm on mac
    exclude("lib/aarch64-Linux*/**")
    // doesn't support these architectures
    exclude("lib/ppc*/**")
    exclude("lib/risc*/**")
    exclude("lib/s390x*/**")
    exclude("lib/arm*/**")
    // metadata
    // discord doesn"t support bsd or sun
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-*BSD*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-*Sun*/**")
    // we don"t use junixsocket on windows
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-*Window*/**")
    // include only arm on mac
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-aarch64-Linux*/**")
    // doesn't support these architectures
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-ppc*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-risc*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-s390x*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-arm*/**")

    // Package Relocations
    relocate("com.jagrosh", "external.com.jagrosh")
    relocate("org.meteordev", "external.org.meteordev")
    relocate("io.github.classgraph", "external.io.github.classgraph")
    relocate("nonapi.io.github.classgraph", "external.nonapi.io.github.classgraph")
    if (protocol < 755) {
        relocate("org.slf4j", "external.org.slf4j")
        relocate("org.apache.logging.slf4j", "external.org.apache.logging.slf4j")
    }
    // Integration Relocations
    if (!isLegacy) {
        relocate("me.hypherionmc", "external.me.hypherionmc")
    }
}

tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar.get())
    archiveClassifier.set(fmlName)
}

tasks.jar {
    if (canUseATs) {
        manifest {
            attributes(
                mapOf(
                    "FMLAT" to "accesstransformer.cfg"
                )
            )
        }
    }
    archiveClassifier.set("dev")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val commonSources = project(":common").tasks.sourcesJar
    dependsOn(commonSources.get())
    from(commonSources.get().archiveFile.map { zipTree(it) })
}