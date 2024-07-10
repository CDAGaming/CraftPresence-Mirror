import com.diffplug.gradle.spotless.SpotlessExtension
import com.hypherionmc.modpublisher.plugin.ModPublisherGradleExtension
import xyz.wagyourtail.jvmdg.gradle.task.files.DowngradeFiles
import xyz.wagyourtail.replace_str.ProcessClasses
import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.util.*

plugins {
    java
    id("xyz.wagyourtail.unimined") version "1.3.0" apply false
    id("xyz.wagyourtail.jvmdowngrader") version "0.8.2"
    id("com.diffplug.gradle.spotless") version "6.25.0" apply false
    id("io.github.goooler.shadow") version "8.1.7" apply false
    id("com.hypherionmc.modutils.modpublisher") version "2.1.5" apply false
}

/**
 * Retrieve a Project Property
 */
operator fun String.invoke(): String? {
    return project.properties[this] as String?
}

val extMcVersion = if ("display_version"()!!.isNotEmpty()) "display_version"() else "mc_version"()
val extVersionLabel = "${if ("versionLabel"().equals("release", ignoreCase = true)) "" else "versionLabel"()}"

val extModName = "mod_name"()!!
val extModId = "mod_id"()!!

val extVersionInfoLabel = if (extVersionLabel.isEmpty()) "" else "-$extVersionLabel"
val extBaseVersionLabel = ("versionId"() + extVersionInfoLabel.replace(Regex("\\s"), ".")).lowercase()
val extClassPath = "${rootProject.group}".replace(".", "/") + "/$extModId"

val extVersionFormat = "$extBaseVersionLabel+$extMcVersion"
val extFileFormat = "$extModName-$extVersionFormat"
val extDisplayFormat = extVersionFormat.replace(Regex("\\s"), "").lowercase()

val extProtocol = "mc_protocol"()!!.toInt()
val extIsLegacy = "isLegacy"()!!.toBoolean()
val extIsJarMod = "isJarMod"()!!.toBoolean()
val extIsModern = !extIsLegacy && extProtocol >= 498
val extIsMCPJar = extIsJarMod && "mc_mappings_type"() == "mcp"

// Only apply ATs to forge on non-legacy builds, or on Legacy Protocols above 1.5
// due to the way Forge requires core-mods for lower version usage
val extAWFile = file("$rootDir/fabric/src/main/resources/$extModId.accesswidener")
val extCanUseATs = extAWFile.exists() && (!extIsLegacy || extProtocol > 60)

// Setup Game Versions to upload for
val uploadVersions = mutableListOf("mc_version"()!!)
for (v in "additional_mc_versions"()!!.split(",")) {
    if (v.isNotEmpty()) {
        uploadVersions.add(v)
    }
}

subprojects {
    val isLoaderSource = path != ":common"

    apply(plugin = "java")
    apply(plugin = "xyz.wagyourtail.unimined")
    apply(plugin = "xyz.wagyourtail.jvmdowngrader")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "io.github.goooler.shadow")
    if (isLoaderSource) {
        apply(plugin = "com.hypherionmc.modutils.modpublisher")
    }

    val modName by extra(extModName)
    val modId by extra(extModId)
    val versionInfoLabel by extra(extVersionInfoLabel)
    val baseVersionLabel by extra(extBaseVersionLabel)
    val classPath by extra(extClassPath)
    val versionFormat by extra(extVersionFormat)
    val fileFormat by extra(extFileFormat)
    val displayFormat by extra(extDisplayFormat)
    val protocol by extra(extProtocol)
    val isLegacy by extra(extIsLegacy)
    val isJarMod by extra(extIsJarMod)
    val isModern by extra(extIsModern)
    val isMCPJar by extra(extIsMCPJar)
    val accessWidenerFile by extra(extAWFile)
    val canUseATs by extra(extCanUseATs)
    val mcVersionLabel by extra(extMcVersion)
    val versionLabel by extra(extVersionLabel)
    val mcVersion by extra("mc_version"()!!)
    val mcMappingsType by extra("mc_mappings_type"())

    val displayLoaderName =
        name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    extensions.getByType<SpotlessExtension>().apply {
        java {
            licenseHeaderFile(rootProject.file("HEADER"))
        }
    }

    version = versionFormat
    group = rootProject.group

    extensions.getByType<BasePluginExtension>().apply {
        archivesName = modName
    }

    val sourceVersion = "source_java_version"()?.let { JavaVersion.toVersion(it) }!!
    val sourceVersionInt = Integer.parseInt(sourceVersion.majorVersion)

    val buildVersion = "build_java_version"()?.let { JavaVersion.toVersion(it) }!!
    val buildVersionInt = Integer.parseInt(buildVersion.majorVersion)

    val shouldDowngrade = sourceVersionInt > buildVersionInt && isLoaderSource

    val sourceSets = extensions.getByType<SourceSetContainer>()

    extensions.getByType<JavaPluginExtension>().apply {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(sourceVersionInt))
        }
        withSourcesJar()
    }

    repositories {
        mavenLocal()
        mavenCentral()
        // ModLoader Mavens
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForged"
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        maven("https://maven.minecraftforge.net/") {
            name = "Forge"
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.legacyfabric.net/") {
            name = "Legacy Fabric"
        }
        // WagYourTail Mavens
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
        // LenniReflect Mavens
        maven("https://maven.lenni0451.net/releases/")
        maven("https://maven.lenni0451.net/snapshots/")
        // OSS Mavens
        maven("https://s01.oss.sonatype.org/content/repositories/releases")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
        // HypherionMC Mavens
        maven("https://maven.firstdarkdev.xyz/releases")
        maven("https://maven.firstdarkdev.xyz/snapshots")
        // Mod Integration Mavens
        if (isModern) {
            maven("https://maven.terraformersmc.com/releases/") {
                name = "TerraformersMC"
            }
        }
    }

    // debug, puts some things in build/unimined instead of ~/.gradle/caches/unimined
    extensions.getByType<UniminedExtension>().useGlobalCache = false

    extensions.getByType<UniminedExtension>().minecraft(sourceSets.getByName("main"), true) {
        side(if (isJarMod) "client" else "combined")
        version(mcVersion)

        mappings {
            val mcMappings = "mc_mappings"()!!
            when (mcMappingsType) {
                "mcp" -> {
                    if (!isJarMod) {
                        searge()
                    }
                    mcp(if (isJarMod) "legacy" else "stable", mcMappings) {
                        if (!isJarMod) {
                            clearOutputs()
                            outputs("mcp", true) { listOf("intermediary") }
                        }
                    }
                }

                "forgeMCP" -> {
                    forgeBuiltinMCP("forge_version"()!!) {
                        clearContains()
                        clearOutputs()
                        contains({ _, t ->
                            !t.contains("MCP")
                        }) {
                            onlyExistingSrc()
                            outputs("searge", false) { listOf("official") }
                        }
                        contains({ _, t ->
                            t.contains("MCP")
                        }) {
                            outputs("mcp", true) { listOf("intermediary") }
                            sourceNamespace("searge")
                        }
                    }
                    officialMappingsFromJar {
                        clearContains()
                        clearOutputs()
                        outputs("official", false) { listOf() }
                    }
                }

                "retroMCP" -> {
                    retroMCP(mcMappings)
                }

                "yarn" -> {
                    yarn(mcMappings)
                }

                "mojmap" -> {
                    mojmap {
                        skipIfNotIn("intermediary")
                    }
                }

                "parchment" -> {
                    mojmap {
                        skipIfNotIn("intermediary")
                    }
                    parchment(mcVersion, mcMappings)
                }

                else -> throw GradleException("Unknown or Unsupported Mappings version")
            }

            // Only use Intermediaries on Versions that support it
            val usingIntermediary = (isLegacy && protocol >= 39) || !isLegacy
            if (usingIntermediary) {
                if (isModern) {
                    intermediary()
                } else {
                    legacyIntermediary()
                }
            }

            // ability to add custom mappings
            val target = if (!isModern) "mcp" else "mojmap"
            stub.withMappings("searge", target) {
                c("ModLoader", "net/minecraft/src/ModLoader", "net/minecraft/src/ModLoader")
                c("BaseMod", "net/minecraft/src/BaseMod", "net/minecraft/src/BaseMod")
                // Fix: Fixed an inconsistent mapping in 1.16 and 1.16.1 between MCP and Mojmap
                if (!isLegacy && (protocol == 735 || protocol == 736)) {
                    c(
                        "dng",
                        listOf(
                            "net/minecraft/client/gui/widget/Widget",
                            "net/minecraft/client/gui/components/AbstractWidget"
                        )
                    ) {
                        m("e", "()I", "func_238483_d_", "getHeightRealms")
                    }
                }
            }

            if (isMCPJar) {
                if (protocol <= 2) { // MC a1.1.2_01 and below
                    devNamespace("searge")
                } else {
                    devFallbackNamespace("searge")
                }
            } else if (usingIntermediary) {
                devFallbackNamespace("intermediary")
            }

            if (shouldDowngrade) {
                val apiVersion = if (buildVersion.isJava7) JavaVersion.VERSION_1_8 else buildVersion
                val downgradeClient = tasks.register("downgradeClient", DowngradeFiles::class.java) {
                    inputCollection = sourceSet.output.classesDirs + sourceSet.runtimeClasspath
                    classpath = project.files()
                    outputCollection.files
                }

                runs.config("client") {
                    classpath = downgradeClient.get().outputCollection + files(jvmdg.getDowngradedApi(apiVersion))
                }
            }
        }

        minecraftRemapper.config {
            // most mcp mappings (except older format) dont include field desc
            ignoreFieldDesc(true)
            // this also fixes some issues with them, as it tells tiny remapper to try harder to resolve conflicts
            ignoreConflicts(true)
        }
    }

    // Setup UniLib attachment data
    val libPrefix = "unilib_name"()!!
    val libName = if (!isLoaderSource) "fabric" else name
    val libVersion = "unilib_build_version"()!!

    dependencies {
        // Annotations
        "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
        "compileOnly"("com.github.spotbugs:spotbugs-annotations:4.8.6")

        // Attach UniLib dependency
        "modImplementation"(
            "com.gitlab.cdagaming.unilib:$libPrefix-${
                libName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }:$libVersion+$mcVersionLabel:$libName"
        )
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"

        // JDK 9 introduced a new way of specifying this that will make sure no newer classes or methods are used.
        // We"ll use that if it"s available, but otherwise we"ll use the older option.
        if (sourceVersion.isJava9Compatible) {
            options.release.set(sourceVersionInt)
        }
    }

    tasks.named<Jar>("jar").configure {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to modName,
                    "Specification-Vendor" to "CDAGaming",
                    "Specification-Version" to "1", // We are version 1 of ourselves
                    "Implementation-Title" to modName,
                    "Implementation-Version" to archiveVersion.get(),
                    "Implementation-Vendor" to "CDAGaming"
                )
            )
        }
    }

    gradle.projectsEvaluated {
        tasks.withType<Javadoc>().configureEach {
            options.encoding = "UTF-8"

            exclude("**/*Gui.java")
            exclude("$classPath/**/config/**")
            exclude("$classPath/**/integrations/**")
        }
    }

    tasks.getByName<JavaCompile>("compileJava").apply {
        doLast {
            ProcessClasses.process(
                destinationDirectory.asFile.getOrNull()!!.toPath(), mapOf(
                    "MOD_ID" to modId,
                    "MOD_NAME" to modName,
                    "VERSION_ID" to baseVersionLabel,
                    "VERSION_TYPE" to "deploymentType"()!!,
                    "MC_VERSION" to mcVersionLabel,
                    "MC_PROTOCOL" to protocol.toString(),
                    "IS_LEGACY" to isLegacy.toString(),
                    "UNILIB_NAME" to libPrefix,
                    "UNILIB_MIN_VERSION" to "unilib_minimum_version"()!!
                )
            )
        }
    }

    afterEvaluate {
        if (shouldDowngrade) {
            // Setup JVMDG Globals
            jvmdg.downgradeTo = buildVersion
            jvmdg.shadePath = {
                "$modId/jvmdg/api"
            }
            if (buildVersion.isJava7) {
                jvmdg.debugSkipStubs.add(JavaVersion.VERSION_1_8)
            }

            val remapJar = tasks.getByName<RemapJarTask>("remapJar") {
                destinationDirectory = temporaryDir
            }

            tasks.getByName("assemble").dependsOn("shadeDowngradedApi")
            tasks.downgradeJar {
                dependsOn(remapJar)
                inputFile = remapJar.archiveFile.get().asFile
                destinationDirectory = temporaryDir
            }

            tasks.shadeDowngradedApi {
                archiveClassifier = remapJar.archiveClassifier
            }

            tasks.named("preRunClient") {
                dependsOn("downgradeClient")
            }
        }
    }

    if (isLoaderSource) {
        val targetFile = "build/libs/$fileFormat-$name.jar"
        val uploadLoaders = mutableListOf(name)
        val additionalLoaders = "additional_${name}_loaders"()
        if (!additionalLoaders.isNullOrEmpty()) {
            for (v in additionalLoaders.split(",")) {
                if (v.isNotEmpty()) {
                    uploadLoaders.add(v)
                }
            }
        }

        extensions.getByName<ModPublisherGradleExtension>("publisher").apply {
            apiKeys {
                curseforge(System.getenv("CF_APIKEY"))
                modrinth(System.getenv("MODRINTH_TOKEN"))
                nightbloom(System.getenv("NIGHTBLOOM_TOKEN"))
            }

            debug = false
            curseID = "297038"
            modrinthID = "DFqQfIBR"
            nightbloomID = modId
            versionType = "deploymentType"()!!.lowercase()
            changelog = file("$rootDir/Changes.md").readText()
            projectVersion = "$displayFormat-$name" // Modrinth Only
            displayName =
                "[$displayLoaderName $mcVersionLabel] $modName v${"versionId"()}${if (versionLabel.isEmpty()) "" else " $versionLabel"}"
            gameVersions = uploadVersions
            loaders = uploadLoaders
            curseEnvironment = "client"
            artifact = targetFile
        }
    }
}

tasks {
    jar {
        enabled = false
    }
}
