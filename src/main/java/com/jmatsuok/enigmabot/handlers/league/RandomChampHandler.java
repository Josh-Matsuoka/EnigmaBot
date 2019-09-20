package com.jmatsuok.enigmabot.handlers.league;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * RandomChampHandler
 *
 * Usage: -randomchamp [role] (--detailed)
 *
 * Finds a random champion for the user to play, displaying
 * detailed statistics if specified
 */
public class RandomChampHandler extends ListenerAdapter {

    // We pull from champion.gg for the statistics and champion list
    private final String URL = "https://champion.gg/statistics/#?sortBy=general.winPercent&order=descend&roleSort=";
    private final String SPAGHETTI = "https://www.thespruceeats.com/thmb/d2snX_gkTaZY3rdVMU1Sk-1Cny8=/2500x1875/smart/filters:no_upscale()/meat-sauce-spaghetti-2500-56dc8ae53df78c5ba053531b.jpg";
    //TODO: Rework this, we don't need 2 regexes
    private final Pattern ROLE_PATTERN = Pattern.compile("-randomchamp (.*)( --detailed)?");
    private final Pattern ROLE_PATTERN2 = Pattern.compile("-randomchamp (.*) --detailed");
    private Logger logger = LoggerFactory.getLogger(RandomChampHandler.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            Matcher matcher1 = ROLE_PATTERN.matcher(event.getMessage().getContentRaw());
            Matcher matcher =  ROLE_PATTERN2.matcher(event.getMessage().getContentRaw());
            if (matcher1.matches() || matcher.matches()) {
                MessageChannel channel = event.getChannel();
                List<String> matches = new ArrayList<>();
                HashMap<String, String> details = new HashMap<>();
                String role;
                if (matcher.matches()) {
                    role = matcher.group(1);
                } else {
                    role = matcher1.group(1);
                }
                //FIXME: This is ugly, but it's the only way to do this for the moment
                // until a proper statistic aggregator has a public API
                Document doc = Jsoup.connect(URL + role).get();
                Elements scripts = doc.select("script");
                for (Element element : scripts) {
                    if (element.toString().contains("matchupData.stats")) {
                        JSONArray array = new JSONArray(element.toString().split(" = ")[1]);
                        for (Object obj : array) {
                            JSONObject item = (JSONObject) obj;
                            if (item.getString("role").equalsIgnoreCase(role)) {
                                matches.add(item.getString("key"));
                                details.put(item.getString("key"), item.toString());
                            }
                        }
                    }
                }
                int possibilities = matches.size();
                Random random = new Random();
                String champion = matches.get(random.nextInt(possibilities));
                if (!event.getMessage().getContentRaw().contains("--detailed")) {
                    channel.sendMessage(SPAGHETTI)
                            .queue(response -> {
                                response.editMessageFormat("I think you should play %s go do that now. " + event.getAuthor().getAsMention(),
                                        champion).queue();
                            });
                } else {
                    channel.sendMessage(SPAGHETTI)
                            .queue(response -> {
                                response.editMessageFormat(formatMessage(champion, details.get(champion))
                                        + event.getAuthor().getAsMention()).queue();
                            });
                }
            }
        } catch (Exception e) {
            logger.error("Exception thrown", e);
        }
    }

    /***
     * Formats the detailed information if requested
     *
     * @param champion that was chosen randomly
     * @param detailed statistics about that champion
     * @return
     */
    private String formatMessage(String champion, String detailed) {
        JSONObject details = new JSONObject(detailed);
        JSONObject object = details.getJSONObject("general");
        StringBuilder builder = new StringBuilder();
        // FIXME: There are statistics for banrate and playrate but if those numbers are too low
        // they don't play nice with the JSON Parser
        try {
            builder.append("I think you should play " + champion + " go do that now. \n");
            builder.append("```Detailed Statistics \n");
            builder.append("=====================================\n");
            builder.append("Winrate: " + (object.getDouble("winPercent") * 100) + "\n");
            builder.append("=====================================\n");
            builder.append("Average kills per game: " + object.getDouble("kills") + "\n");
            builder.append("Average assists per game: " + object.getDouble("assists") + "\n");
            builder.append("Average deaths per game: " + object.getDouble("deaths") + "\n");
            builder.append("Average killing spree: " + object.getDouble("largestKillingSpree") + "\n");
            builder.append("=====================================\n");
            builder.append("Average CS per game: " + object.getDouble("minionsKilled") + "\n");
            builder.append("Average jungle monsters killed per game: " + object.getDouble("neutralMinionsKilledTeamJungle") + "\n");
            builder.append("Average counterjungled monsters per game: " + object.getDouble("neutralMinionsKilledEnemyJungle") + "\n");
            builder.append("Average gold per game: " + object.getDouble("goldEarned") + "\n");
            builder.append("=====================================\n");
            builder.append("Average Damage Taken per game: " + object.getDouble("totalDamageTaken") + "\n");
            builder.append("Average Damage Dealt per game: " + object.getDouble("totalDamageDealtToChampions") + "\n");
            builder.append("Average healing per game: " + object.getDouble("totalHeal") + "\n");
            builder.append("=====================================\n");
            builder.append("```");
        } catch (Exception e) {
            logger.error("Exception thrown while parsing details", e);
        }
        return builder.toString();
    }
}
