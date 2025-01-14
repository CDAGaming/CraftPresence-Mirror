import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

/**
 * Retrieve a Project Property
 */
operator fun String.invoke(): String? {
    return project.properties[this] as String?
}

val modName: String by extra
val modId: String by extra

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
    quilt {
        if (accessWidenerFile.exists()) {
            accessWidener(accessWidenerFile)
        }
        loader("quilt_loader_version"()!!)
        customIntermediaries = true
    }
    // Note: Required for ignoring Quilt hassles
    mods {
        modImplementation {
            catchAWNamespaceAssertion()
        }
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

dependencies {
    // Required for mod loading
    val quiltBase = "org.quiltmc.qsl.core:qsl_base:${"quilt_api_version"()!!}"
    "modImplementation"(quiltBase)
    "include"(quiltBase)

    // Required for loading translation data
    val resourceLoader = fabricApi.quiltFabricModule("fabric-resource-loader-v0", "quilt_fabric_api_version"()!!)
    "modImplementation"(resourceLoader)
    "include"(resourceLoader)

    // Mod Menu API Implementation
    "modImplementation"("${"modmenu_group"()}:modmenu:${"modmenu_version"()}") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "net.fabricmc")
    }

    // Quilt: Replace Fabric APIs with Quilt Equivalents
    "modImplementation"(fabricApi.quiltFabricModule("fabric-api-base", "quilt_fabric_api_version"()!!))
    "modImplementation"(fabricApi.quiltFabricModule("fabric-screen-api-v1", "quilt_fabric_api_version"()!!))
    "modImplementation"(fabricApi.quiltFabricModule("fabric-key-binding-api-v1", "quilt_fabric_api_version"()!!))
    "modImplementation"(fabricApi.quiltFabricModule("fabric-lifecycle-events-v1", "quilt_fabric_api_version"()!!))

    common(project(path = ":common")) { isTransitive = false }
    common(project(path = ":common", configuration = "shade"))
    common(project(path = ":common", configuration = "runtime"))
    shadowCommon(project(path = ":common", configuration = "shadeOnly"))
    shadowCommon(project(path = ":common")) { isTransitive = false }
}

val gameVersionRange = "quilt_game_version_range"()!!.split(',').toList()

val resourceTargets = listOf(
    "quilt.mod.json",
    "pack.mcmeta"
)
val replaceProperties = mapOf(
    "mod_id" to modId,
    "mod_name" to modName,
    "version" to baseVersionLabel,
    "mcversion" to mcVersionLabel,
    "game_version_range_start" to gameVersionRange[0],
    "game_version_range_end" to gameVersionRange[1],
    "loader_version_range" to "quilt_loader_version_range"()!!,
    "unilib_min_version" to "unilib_minimum_version"()!!
)

tasks.processResources {
    inputs.properties(replaceProperties)

    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

tasks.shadowJar {
    mustRunAfter(project(":common").tasks.shadowJar)
    dependsOn(project(":common").tasks.shadowJar)
    from(zipTree(project(":common").tasks.shadowJar.get().archiveFile))
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")
}

tasks.named<RemapJarTask>("remapJar") {
    dependsOn(tasks.shadowJar.get())
    asJar {
        inputFile.set(tasks.shadowJar.get().archiveFile)
        archiveClassifier.set(project.name)
    }
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