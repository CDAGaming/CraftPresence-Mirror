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
val mcMappingsType: String by extra
val canUseATs: Boolean by extra
val baseVersionLabel: String by extra

val forgeVersion = "forge_version"()!!

unimined.minecraft {
    // N/A
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

val foxVersion = "2.0-alpha38"

dependencies {
    "jarMod"("local:nsss:$forgeVersion")
    "jarMod"("local:foxloader:$foxVersion")

    common(project(path = ":common")) { isTransitive = false }
    common(project(path = ":common", configuration = "shade"))
    common(project(path = ":common", configuration = "runtime"))
    shadowCommon(project(path = ":common", configuration = "shadeOnly"))
    shadowCommon(project(path = ":common")) { isTransitive = false }
}

val resourceTargets = listOf(
    "mcmod.info",
    "META-INF/mods.toml",
    "META-INF/neoforge.mods.toml",
    "mod_$modName.info",
    "pack.mcmeta"
)
val replaceProperties = mapOf(
    "mod_id" to modId,
    "mod_name" to modName,
    "version" to baseVersionLabel,
    "mcversion" to mcVersionLabel,
    "forge_id" to name,
    "fml_version_range" to "fml_version_range"(),
    "game_version_range" to "forge_game_version_range"(),
    "loader_version_range" to "forge_loader_version_range"(),
    "unilib_min_version" to uniLibMinVersion
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
    archiveClassifier.set(project.name)
}
tasks.build.get().dependsOn(tasks.shadowJar.get())

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "For-FoxLoader-Version" to foxVersion,
                "For-ReIndev-Version" to forgeVersion,
                "ModDesc" to "Completely Customize the way others see you play in Discord!",
                "ModMain" to "mod_CraftPresence",
                "ModName" to modName,
                "ModVersion" to archiveVersion.get(),
                "ModId" to modId,
                "ModAuthors" to "CDAGaming",
                "ModIcon" to "assets/$modId/logo.png",
                "ModWebsite" to "https://www.curseforge.com/minecraft/mc-mods/craftpresence"
            )
        )
    }
    archiveClassifier.set("dev")
}

tasks.sourcesJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val commonSources = project(":common").tasks.sourcesJar
    dependsOn(commonSources)
    from(commonSources.get().archiveFile.map { zipTree(it) })
}