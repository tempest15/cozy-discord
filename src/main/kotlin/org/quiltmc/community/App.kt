@file:OptIn(PrivilegedIntent::class)

/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.quiltmc.community

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.modules.extra.phishing.DetectionAction
import com.kotlindiscord.kord.extensions.modules.extra.phishing.extPhishing
import com.kotlindiscord.kord.extensions.utils.envOrNull
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import org.quiltmc.community.modes.quilt.extensions.SubteamsExtension
import org.quiltmc.community.modes.quilt.extensions.SyncExtension
import org.quiltmc.community.modes.quilt.extensions.UtilityExtension
import org.quiltmc.community.modes.quilt.extensions.filtering.FilterExtension
import org.quiltmc.community.modes.quilt.extensions.github.GithubExtension
import org.quiltmc.community.modes.quilt.extensions.messagelog.MessageLogExtension
import org.quiltmc.community.modes.quilt.extensions.minecraft.MinecraftExtension
import org.quiltmc.community.modes.quilt.extensions.suggestions.SuggestionsExtension

val MODE = envOrNull("MODE")?.lowercase() ?: "quilt"

suspend fun setupCollab() = ExtensibleBot(DISCORD_TOKEN) {
    common()
    database()

    extensions {
        sentry {
            distribution = "collab"
        }
    }
}

suspend fun setupQuilt() = ExtensibleBot(DISCORD_TOKEN) {
    common()
    database(true)

    chatCommands {
        enabled = true
    }

    intents {
        +Intents.all
    }

    members {
        all()
    }

    extensions {
        add(::FilterExtension)
        add(::MessageLogExtension)
        add(::MinecraftExtension)
        add(::SubteamsExtension)
        add(::SuggestionsExtension)
        add(::SyncExtension)
        add(::UtilityExtension)

        if (GITHUB_TOKEN != null) {
            add(::GithubExtension)
        }

        extMappings { }

        extPhishing {
            appName = "QuiltMC's Cozy Bot"
            detectionAction = DetectionAction.Kick
            logChannelName = "cozy-logs"
            requiredCommandPermission = null

            check { inQuiltGuild() }
            check { notHasBaseModeratorRole() }

            regex("([^\\s</]+\\s*(?:\\.|dot)+\\s*[^\\s>/]+)")
        }

        sentry {
            distribution = "community"
        }
    }
}

suspend fun setupShowcase() = ExtensibleBot(DISCORD_TOKEN) {
    common()
    database()

    extensions {
        sentry {
            distribution = "showcase"
        }
    }
}

suspend fun main() {
    val bot = when (MODE) {
        "collab" -> setupCollab()
        "quilt" -> setupQuilt()
        "showcase" -> setupShowcase()

        else -> error("Invalid mode: $MODE")
    }

    bot.start()
}
