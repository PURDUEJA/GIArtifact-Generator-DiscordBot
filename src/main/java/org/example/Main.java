package org.example;

import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.entity.message.component.ButtonStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.example.ArtifactGenerator.generateRandomArtifact;

public class Main {
    // Store artifacts with message ID as key
    private static final Map<String, Artifact> artifactDatabase = new HashMap<>();

    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder()
                .setToken("") // Token should be set here, do not commit it to version control.
                .addIntents(Intent.MESSAGE_CONTENT)
                .login().join();

        // Create commands
        SlashCommand.with("ping", "Check if bot is running")
                .createGlobal(api)
                .join();

        SlashCommand.with("generate_artifact", "Generate a random artifact")
                .createGlobal(api)
                .join();


        api.addSlashCommandCreateListener(Main::handleSlashCommand);
        api.addButtonClickListener(Main::handleButtonClick);
    }

    private static void handleSlashCommand(SlashCommandCreateEvent event) {
        System.out.println(event);
        String commandName = event.getSlashCommandInteraction().getCommandName();

        switch (commandName.toLowerCase()) {
            case "ping":
                event.getSlashCommandInteraction().createImmediateResponder()
                        .setContent("Pong!")
                        .respond();
                break;
            case "generate_artifact":
                handleGenerateArtifact(event);
                break;
        }
    }

    private static void handleGenerateArtifact(SlashCommandCreateEvent event) {
        Artifact artifact = generateRandomArtifact();
        String artifactId = java.util.UUID.randomUUID().toString();

        artifactDatabase.put(artifactId, artifact);

        // Respond with the original message and include components
        event.getSlashCommandInteraction().createImmediateResponder()
                .setContent(artifact.toString())
                .addComponents(
                        ActionRow.of(
                                Button.create(
                                        "level_up:" + artifactId,
                                        ButtonStyle.SUCCESS,
                                        "Level to next"
                                ),
                                Button.create(
                                        "reroll",
                                        ButtonStyle.SUCCESS,
                                        "Reroll"
                                )
                        )
                )
                .respond();
    }

    private static void handleButtonClick(ButtonClickEvent event) {
        String customId = event.getButtonInteraction().getCustomId();

        if (customId.startsWith("level_up:")) {
            String artifactId = customId.split(":")[1];
            Artifact artifact = artifactDatabase.get(artifactId);

            if (artifact != null) {
                try {
                    // Check if the artifact is already at max level
                    if (artifact.getLevel() >= 20) {
                        // Delete the original message first
                        event.getButtonInteraction().getMessage().delete();

                        // Send a new message with artifact stats and only the reroll button
                        event.getButtonInteraction().respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                            interactionOriginalResponseUpdater.setContent(artifact.toString())
                                    .addComponents(
                                            ActionRow.of(
                                                    Button.create(
                                                            "reroll",
                                                            ButtonStyle.SUCCESS,
                                                            "Reroll"
                                                    )
                                            )
                                    )
                                    .update();
                        });
                        return;
                    }

                    // Level up the artifact
                    artifact.levelUp(new Random());

                    // Delete the original message first
                    event.getButtonInteraction().getMessage().delete();

                    // Send a new message with the updated artifact stats and buttons
                    event.getButtonInteraction().respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent(artifact.toString())
                                .addComponents(
                                        ActionRow.of(
                                                Button.create(
                                                        "level_up:" + artifactId,
                                                        ButtonStyle.SUCCESS,
                                                        "Level to next"
                                                ),
                                                Button.create(
                                                        "reroll",
                                                        ButtonStyle.SUCCESS,
                                                        "Reroll"
                                                )
                                        )
                                ).update();
                    });

                    // Update the stored artifact
                    artifactDatabase.put(artifactId, artifact);

                } catch (IllegalStateException e) {
                    // Handle error and send the reroll button
                    event.getButtonInteraction().getMessage().delete();

                    event.getButtonInteraction().respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                        interactionOriginalResponseUpdater.setContent("An error occurred while leveling the artifact.")
                                .addComponents(
                                        ActionRow.of(
                                                Button.create(
                                                        "reroll",
                                                        ButtonStyle.SUCCESS,
                                                        "Reroll"
                                                )
                                        )
                                ).update();
                    });
                }
            } else {
                // Artifact not found, delete original message and send an error message
                event.getButtonInteraction().getMessage().delete();

                event.getButtonInteraction().respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent("Sorry, I couldn't find the artifact data. Please generate a new one.")
                            .addComponents(
                                    ActionRow.of(
                                            Button.create(
                                                    "reroll",
                                                    ButtonStyle.SUCCESS,
                                                    "Reroll"
                                            )
                                    )
                            ).update();
                });
            }
        } else if (customId.equals("reroll")) {
            // Reroll the artifact
            handleGenerateArtifact(event); // Generate a new artifact directly
        }
    }

    // Modify handleGenerateArtifact to accept ButtonClickEvent
    private static void handleGenerateArtifact(ButtonClickEvent event) {
        Artifact artifact = generateRandomArtifact();
        String artifactId = java.util.UUID.randomUUID().toString();

        artifactDatabase.put(artifactId, artifact);

        // Delete the original message first
        event.getButtonInteraction().getMessage().delete();

        // Send a new message with the new artifact stats and buttons
        event.getButtonInteraction().respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            interactionOriginalResponseUpdater.setContent(artifact.toString())
                    .addComponents(
                            ActionRow.of(
                                    Button.create(
                                            "level_up:" + artifactId,
                                            ButtonStyle.SUCCESS,
                                            "Level to next"
                                    ),
                                    Button.create(
                                            "reroll",
                                            ButtonStyle.SUCCESS,
                                            "Reroll"
                                    )
                            )
                    ).update();
        });
    }
}