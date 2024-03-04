import com.diffplug.gradle.spotless.SpotlessExtension
import com.hypherionmc.modfusioner.plugin.FusionerExtension
import org.gradle.internal.jvm.Jvm
import xyz.wagyourtail.mini_jvmdg.MiniJVMDowngrade
import xyz.wagyourtail.replace_str.ProcessClasses
import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.internal.minecraft.task.RemapJarTaskImpl
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    id("xyz.wagyourtail.unimined") version "1.2.0-SNAPSHOT" apply false
    id("com.diffplug.gradle.spotless") version "6.25.0" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("com.hypherionmc.modutils.modfusioner") version "1.0.10"
}

/**
 * Retrieve a Project Property
 */
operator fun String.invoke(): String? {
    return project.properties[this] as String?
}

val extMcVersion = if ("display_version"()!!.isNotEmpty()) "display_version"() else "mc_version"()
val extVersionLabel = "${if ("versionLabel"().equals("release", ignoreCase = true)) "" else "versionLabel"()}"

val extVersionInfoLabel = if (extVersionLabel.isEmpty()) "" else "-$extVersionLabel"
val extBaseVersionLabel = ("versionId"() + extVersionInfoLabel.replace(Regex("\\s"), ".")).lowercase()
val extClassPath = "${rootProject.group}".replace(".", "/") + "/${"mod_name"()}".lowercase()

val extVersionFormat = "$extBaseVersionLabel+$extMcVersion"
val extFileFormat = "${"mod_name"()}-$extVersionFormat"

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
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.github.johnrengelman.shadow")

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
        archivesName = "mod_name"()
    }

    val currentJvm = Jvm.current()
    val sourceVersion = currentJvm.javaVersion!!
    val targetVersion = "java_version"()?.let { JavaVersion.toVersion(it) }!!

    val modernSourceSupport = sourceVersion.isJava9Compatible
    val modernTargetSupport = targetVersion.isJava9Compatible

    val sourceSets = extensions.getByType<SourceSetContainer>()

    extensions.getByType<JavaPluginExtension>().apply {
        if (!modernSourceSupport) {
            sourceCompatibility = sourceVersion
            targetCompatibility = targetVersion
        }
        withSourcesJar()
    }

    repositories {
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
        side(if (isJarMod || isNeoForge) "client" else "combined")
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
                if (extIsModern) {
                    intermediary()
                } else {
                    legacyIntermediary()
                }
            }

            // ability to add custom mappings
            val target = if (!extIsModern) "mcp" else "mojmap"
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
        }

        minecraftRemapper.config {
            // most mcp mappings (except older format) dont include field desc
            ignoreFieldDesc(true)
            // this also fixes some issues with them, as it tells tiny remapper to try harder to resolve conflicts
            ignoreConflicts(true)
        }
    }

    dependencies {
        if (!modernTargetSupport) {
            // If we are targeting a release below Java 9,
            // work around JDK-8206937 by providing a shim for inaccessible classes.
            "compileOnly"("me.eigenraven.java8unsupported:java-8-unsupported-shim:1.0.0")
        }

        // Annotations
        "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
        "compileOnly"("com.github.spotbugs:spotbugs-annotations:4.8.3")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"

        // The Minecraft launcher currently installs Java 8 for users, so your mod probably wants to target Java 8 too
        // JDK 9 introduced a new way of specifying this that will make sure no newer classes or methods are used.
        // We"ll use that if it"s available, but otherwise we"ll use the older option.
        if (modernSourceSupport) {
            options.release.set(Integer.parseInt(targetVersion.majorVersion))
        }
    }

    tasks.named<Jar>("jar").configure {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to "mod_name"(),
                    "Specification-Vendor" to "CDAGaming",
                    "Specification-Version" to "1", // We are version 1 of ourselves
                    "Implementation-Title" to "mod_name"(),
                    "Implementation-Version" to archiveVersion.get(),
                    "Implementation-Vendor" to "CDAGaming",
                    "Implementation-Timestamp" to LocalDateTime.now().atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"))
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
                    "MOD_NAME" to "mod_name"()!!,
                    "VERSION_ID" to baseVersionLabel,
                    "VERSION_TYPE" to "deploymentType"()!!,
                    "MC_VERSION" to mcVersionLabel,
                    "MC_PROTOCOL" to "mc_protocol"()!!,
                    "IS_LEGACY" to isLegacy.toString(),
                    "IS_DEV" to "isDevState"()!!,
                    "IS_VERBOSE" to "isVerboseState"()!!
                )
            )
        }
    }

    afterEvaluate {
        if ("isLegacyASM"()!!.toBoolean() && path != ":common") {
            // TODO: Replace with JvmDowngrader, when ready
            tasks.getByName<RemapJarTaskImpl>("remapJar") {
                doLast {
                    var pn = prodNamespace
                    if (pn == null) pn = provider.mcPatcher.prodNamespace
                    val cp = provider.sourceSet.runtimeClasspath.files
                        .map { it.toPath() }
                        .filter { !provider.isMinecraftJar(it) }
                        .filter { Files.exists(it) } + setOf(
                        // just one unimined internal, to get a remapped mc jar
                        provider.getMinecraft(
                            pn,
                            pn
                        )
                    )
                    MiniJVMDowngrade.downgradeZip(archiveFile.get().asFile.toPath(), cp.toSet())
                }
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
