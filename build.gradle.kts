plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "me.daoge.chatbubble"
description = "Chat bubble plugin for AllayMC"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allay {
    api = "0.19.0"

    plugin {
        entrance = ".ChatBubble"
        name = "ChatBubble"
        authors += "daoge_cmd"
        website = "https://github.com/smartcmd/ChatBubble"
    }
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}