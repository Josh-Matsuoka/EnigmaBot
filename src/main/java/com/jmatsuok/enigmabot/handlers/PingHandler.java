package com.jmatsuok.enigmabot.handlers;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingHandler extends ListenerAdapter {

    private final Pattern PING_PATTERN = Pattern.compile("-ping");

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Matcher matcher = PING_PATTERN.matcher(event.getMessage().getContentRaw());
        if (matcher.matches()) {
            MessageChannel channel = event.getChannel();

            long time = System.currentTimeMillis();
            channel.sendMessage("Pong")
                    .queue(response -> {
                        response.editMessageFormat("Pong: %d ms " + event.getAuthor().getAsMention(),
                                System.currentTimeMillis() - time).queue();
                    });
        }
    }
}
