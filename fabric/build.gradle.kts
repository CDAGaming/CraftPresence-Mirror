import xyz.wagyourtail.unimined.api.minecraft.patch.fabric.FabricLikePatcher
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

/**
 * Retrieve a Project Property
 */
operator fun String.invoke(): String? {
    return project.properties[this] as String?
}

val modName: String by extra
val modId: String by extra

val uniLibMinVersion: String by extra

val isLegacy: Boolean by extra
val protocol: Int by extra
val isJarMod: Boolean by extra
val accessWidenerFile: File by extra
val isMCPJar: Boolean by extra
val isModern: Boolean by extra
val versionFormat: String by extra
val versionLabel: String by extra
val mcVersionLabel: String by extra
val baseVersionLabel: String by extra

unimined.minecraft {
    val fabricData: FabricLikePatcher.() -> Unit = {
        if (accessWidenerFile.exists()) {
            accessWidener(accessWidenerFile)
        }
        loader("fabric_loader_version"()!!)
        if (isJarMod) {
            prodNamespace("official")
            devMappings = null
        }
        customIntermediaries = true
    }
    if (isModern) {
        fabric(fabricData)
    } else {
        legacyFabric(fabricData)
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

dependencies {
    // Fabric Integrations (1.14+)
    if (isModern) {
        // Required for loading translation data
        modImplementation(fabricApi.fabricModule("fabric-resource-loader-v0", "fabric_api_version"()!!))
        "include"(fabricApi.fabricModule("fabric-resource-loader-v0", "fabric_api_version"()!!))

        // Mod Menu API Implementation
        "modImplementation"("${"modmenu_group"()}:modmenu:${"modmenu_version"()}")
    }

    common(project(path = ":common")) { isTransitive = false }
    common(project(path = ":common", configuration = "shade"))
    common(project(path = ":common", configuration = "runtime"))
    shadowCommon(project(path = ":common", configuration = "shadeOnly"))
    shadowCommon(project(path = ":common")) { isTransitive = false }
}

val resourceTargets = listOf(
    "fabric.mod.json",
    "pack.mcmeta"
)
val replaceProperties = mapOf(
    "mod_id" to modId,
    "mod_name" to modName,
    "version" to baseVersionLabel,
    "mcversion" to mcVersionLabel,
    "game_version_range" to "fabric_game_version_range"()!!,
    "loader_version_range" to "fabric_loader_version_range"()!!,
    "unilib_min_version" to uniLibMinVersion
)

tasks.processResources {
    inputs.properties(replaceProperties)

    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

val relocatePath = "$modId.external"

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
    // we don't use junixsocket on windows
    exclude("lib/*Window*/**")
    // include only arm on mac
    exclude("lib/aarch64-Linux*/**")
    // doesn't support these architectures
    exclude("lib/ppc*/**")
    exclude("lib/risc*/**")
    exclude("lib/s390x*/**")
    exclude("lib/arm*/**")
    // metadata
    // discord doesn't support bsd or sun
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-*BSD*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-*Sun*/**")
    // we don't use junixsocket on windows
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-*Window*/**")
    // include only arm on mac
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-aarch64-Linux*/**")
    // doesn't support these architectures
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-ppc*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-risc*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-s390x*/**")
    exclude("META-INF/native-image/com.kohlschutter.junixsocket/junixsocket-native-arm*/**")

    // Package Relocations
    relocate("net.lenni0451", "$relocatePath.net.lenni0451")
    relocate("com.jagrosh", "$relocatePath.com.jagrosh")
    relocate("org.meteordev", "$relocatePath.org.meteordev")
    relocate("io.github.classgraph", "$relocatePath.io.github.classgraph")
    relocate("nonapi.io.github.classgraph", "$relocatePath.nonapi.io.github.classgraph")
    if (protocol < 755) {
        relocate("org.slf4j", "$relocatePath.org.slf4j")
        relocate("org.apache.logging.slf4j", "$relocatePath.org.apache.logging.slf4j")
    }
    // Integration Relocations
    if (!isLegacy) {
        relocate("me.hypherionmc", "$relocatePath.me.hypherionmc")
    }
}

tasks.named<RemapJarTask>("remapJar") {
    if (isJarMod) {
        prodNamespace("official")
    }
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar.get())
    archiveClassifier.set(project.name)
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val commonSources = project(":common").tasks.sourcesJar
    dependsOn(commonSources)
    from(commonSources.get().archiveFile.map { zipTree(it) })
}