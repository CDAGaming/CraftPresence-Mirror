import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hypherionmc.modpublisher.plugin.ModPublisherGradleExtension
import xyz.wagyourtail.jvmdg.gradle.task.files.DowngradeFiles
import xyz.wagyourtail.replace_str.ProcessClasses
import xyz.wagyourtail.unimined.api.UniminedExtension
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.util.*

plugins {
    java
    id("xyz.wagyourtail.unimined") version "1.3.14" apply false
    id("xyz.wagyourtail.jvmdowngrader") version "1.2.2"
    id("com.diffplug.gradle.spotless") version "8.0.0" apply false
    id("com.gradleup.shadow") version "9.2.2" apply false
    id("com.hypherionmc.modutils.modpublisher") version "2.1.8" apply false
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
    apply(plugin = "com.gradleup.shadow")
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
    val uniLibMinVersion by extra("unilib_minimum_version"()!!)

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

    val canDowngrade = sourceVersionInt > buildVersionInt
    val shouldDowngrade = canDowngrade && isLoaderSource

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
        maven("https://maven.minecraftforge.net/") {
            name = "Forge"
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        maven("https://maven.neoforged.net/releases") {
            name = "NeoForged"
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
        maven("https://central.sonatype.com/repository/maven-snapshots/")
        // HypherionMC Mavens
        maven("https://maven.firstdark.dev/releases")
        maven("https://maven.firstdark.dev/snapshots")
        // Mod Integration Mavens
        if (isModern) {
            maven("https://maven.terraformersmc.com/releases/") {
                name = "TerraformersMC"
            }
        }
    }

    // debug, puts some things in build/unimined instead of ~/.gradle/caches/unimined
    extensions.getByType<UniminedExtension>().useGlobalCache = true

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
                devFallbackNamespace("official")
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
    val libFile = "$libName${if (canDowngrade) "-native" else ""}"

    dependencies {
        // Annotations
        "compileOnly"("com.google.code.findbugs:jsr305:3.0.2")
        "compileOnly"("com.github.spotbugs:spotbugs-annotations:4.9.6")

        // Attach UniLib dependency
        "modImplementation"(
            "com.gitlab.cdagaming.unilib:$libPrefix-${
                libName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }:$libVersion+$mcVersionLabel:$libFile"
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

    val relocatePath = "$modId.external"

    tasks.named<ShadowJar>("shadowJar").configure {
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
                    "UNILIB_MIN_VERSION" to uniLibMinVersion,
                    "UNILIB_LEGACY_RANGE" to "required-after:unilib@[$uniLibMinVersion,]"
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

            val resultJar = tasks.getByName<RemapJarTask>("remapJar").asJar
            val resultClassifier = resultJar.archiveClassifier.get()
            resultJar.archiveClassifier.set("$resultClassifier-native")

            tasks.getByName("assemble").dependsOn("shadeDowngradedApi")
            tasks.downgradeJar {
                dependsOn(resultJar)
                inputFile = resultJar.archiveFile.get().asFile
                destinationDirectory = temporaryDir
            }

            tasks.shadeDowngradedApi {
                archiveClassifier = resultClassifier
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

            curseDepends {
                required("unilib")
            }

            modrinthDepends {
                required("unilib")
            }

            nightbloomDepends {
                required("unilib")
            }
        }
    }
}

tasks.register<Delete>("cleanUniminedCache") {
    val uniminedPath = ".gradle/unimined"

    // Delete the .gradle/unimined directory in the root project if it exists
    delete(rootProject.projectDir.resolve(uniminedPath))

    // Delete the .gradle/unimined directory in each subproject if it exists
    delete(subprojects.map { it.projectDir.resolve(uniminedPath) })
}

// Ensure the clean task depends on cleanUniminedCache
tasks.named("clean") {
    dependsOn("cleanUniminedCache")
}

tasks {
    jar {
        enabled = false
    }
}
