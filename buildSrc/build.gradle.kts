plugins {
    java
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.9.1")
    implementation("org.ow2.asm:asm-commons:9.9.1")
    implementation("org.ow2.asm:asm-tree:9.9.1")
    implementation("org.ow2.asm:asm-util:9.9.1")
    implementation("org.ow2.asm:asm-analysis:9.9.1")

    implementation("org.apache.commons:commons-compress:1.27.1")
}