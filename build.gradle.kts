import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hypherionmc.modfusioner.plugin.FusionerExtension
import xyz.wagyourtail.jvmdg.gradle.task.files.DowngradeFiles
import xyz.wagyourtail.replace_str.ProcessClasses
import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.patch.fabric.FabricLikePatcher
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    java
    id("xyz.wagyourtail.unimined") version "1.2.14" apply false
    id("xyz.wagyourtail.jvmdowngrader") version "0.7.2"
    id("com.diffplug.gradle.spotless") version "6.25.0" apply false
    id("io.github.goooler.shadow") version "8.1.7" apply false
    id("com.hypherionmc.modutils.modfusioner") version "1.0.12"
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

val extProtocol = "mc_protocol"()!!.toInt()
val extIsLegacy = "isLegacy"()!!.toBoolean()
val extIsJarMod = "isJarMod"()!!.toBoolean()
val extIsNeoForge = "isNeoForge"()!!.toBoolean()
val extIsModern = !extIsLegacy && extProtocol >= 498
val extIsMCPJar = extIsJarMod && "mc_mappings_type"() == "mcp"

val extFmlName = if (extIsJarMod) "modloader" else "forge"

// Only apply ATs to forge on non-legacy builds, or on Legacy Protocols above 1.5
// due to the way Forge requires core-mods for lower version usage
val extAWFile = file("$rootDir/fabric/src/main/resources/craftpresence.accesswidener")
val extCanUseATs = extAWFile.exists() && (!extIsLegacy || extProtocol > 60)

subprojects {
    apply(plugin = "java")
    apply(plugin = "xyz.wagyourtail.unimined")
    apply(plugin = "xyz.wagyourtail.jvmdowngrader")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "io.github.goooler.shadow")

    val modName by extra(extModName)
    val modId by extra(extModId)
    val versionInfoLabel by extra(extVersionInfoLabel)
    val baseVersionLabel by extra(extBaseVersionLabel)
    val classPath by extra(extClassPath)
    val versionFormat by extra(extVersionFormat)
    val fileFormat by extra(extFileFormat)
    val protocol by extra(extProtocol)
    val isLegacy by extra(extIsLegacy)
    val isJarMod by extra(extIsJarMod)
    val isNeoForge by extra(extIsNeoForge)
    val isModern by extra(extIsModern)
    val isMCPJar by extra(extIsMCPJar)
    val fmlName by extra(extFmlName)
    val accessWidenerFile by extra(extAWFile)
    val canUseATs by extra(extCanUseATs)
    val mcVersionLabel by extra(extMcVersion)
    val versionLabel by extra(extVersionLabel)
    val mcVersion by extra("mc_version"()!!)
    val mcMappingsType by extra("mc_mappings_type"())

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

    val shouldDowngrade = sourceVersionInt > buildVersionInt && path != ":common"

    val sourceSets = extensions.getByType<SourceSetContainer>()

    extensions.getByType<JavaPluginExtension>().apply {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(sourceVersionInt))
        }
        withSourcesJar()
    }

    repositories {
        flatDir {
            dirs("$rootDir/libs")
        }
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
        version("empty-$mcVersion")

        defaultRemapJar = false
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
            merged {
                legacyFabric(fabricData)
                jarMod {}
            }
        }

        mappings {
            devNamespace("official")
            devFallbackNamespace("official")
        }

        minecraftRemapper.config {
            // most mcp mappings (except older format) dont include field desc
            ignoreFieldDesc(true)
            // this also fixes some issues with them, as it tells tiny remapper to try harder to resolve conflicts
            ignoreConflicts(true)
        }
    }

    dependencies {
        // Annotations
        "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
        "compileOnly"("com.github.spotbugs:spotbugs-annotations:4.8.6")
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
                    "IS_DEV" to "isDevState"()!!,
                    "IS_VERBOSE" to "isVerboseState"()!!
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

            val remapJar = tasks.getByName<ShadowJar>("shadowJar")

            tasks.getByName("assemble").dependsOn("shadeDowngradedApi")
            tasks.downgradeJar {
                dependsOn(remapJar)
                inputFile = remapJar.archiveFile.get().asFile
                destinationDirectory = temporaryDir
            }

            tasks.shadeDowngradedApi {
                archiveClassifier = remapJar.archiveClassifier
            }
        }
    }
}

fusioner {
    packageGroup = rootProject.group as String
    mergedJarName = extFileFormat
    outputDirectory = "build/libs"

    // Forge / ModLoader
    customConfigurations.add(FusionerExtension.CustomConfiguration().apply {
        projectName = extFmlName
        inputFile = "build/libs/$extFileFormat-$extFmlName.jar"
    })

    fabricConfiguration = FusionerExtension.FabricConfiguration().apply {
        inputFile = "build/libs/$extFileFormat-fabric.jar"
    }

    relocateDuplicate("com.gitlab.cdagaming.craftpresence.core")
}

tasks {
    jar {
        enabled = false
    }
}
