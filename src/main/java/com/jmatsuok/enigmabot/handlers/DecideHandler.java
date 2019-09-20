package com.jmatsuok.enigmabot.handlers;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * DecideHandler
 *
 * Usage: -decide [choice] | [choice 2] ...
 *
 * Chooses one of the specified options
 */
public class DecideHandler extends ListenerAdapter {

    private final Pattern DECIDE_PATTERN = Pattern.compile("-decide ((.*)\\|+(.*))");
    private final String SPAGHETTI = "https://www.thespruceeats.com/thmb/d2snX_gkTaZY3rdVMU1Sk-1Cny8=/2500x1875/smart/filters:no_upscale()/meat-sauce-spaghetti-2500-56dc8ae53df78c5ba053531b.jpg";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Matcher matcher = DECIDE_PATTERN.matcher(event.getMessage().getContentRaw());
        if (matcher.matches()) {
            MessageChannel channel = event.getChannel();
            String msg = matcher.group(1);
            String[] choices = msg.split("\\|");
            Random random = new Random();
            channel.sendMessage(SPAGHETTI)
                    .queue(response -> {
                        response.editMessageFormat("I think you should: %s go do that now. " + event.getAuthor().getAsMention(),
                                choices[random.nextInt(choices.length)]).queue();
                    });
        }
    }
}
