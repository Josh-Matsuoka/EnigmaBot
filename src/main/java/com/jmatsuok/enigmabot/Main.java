package com.jmatsuok.enigmabot;

import com.jmatsuok.enigmabot.handlers.DecideHandler;
import com.jmatsuok.enigmabot.handlers.PingHandler;
import com.jmatsuok.enigmabot.handlers.league.RandomChampHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static Configuration configuration;
    private static final String BOT_TOKEN = "TOKEN";

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        if (args.length < 0) {
            logger.error("Must specify configuration file path!");
            return;
        }
        logger.debug("Initializing configuration");
        try {
            configuration = new Configuration(args[0]);
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
        }
        logger.debug("Attempting connection");
        try {
            JDA jda = new JDABuilder(configuration.getConfigOption(BOT_TOKEN))
                    .addEventListeners(new PingHandler(),
                            new DecideHandler(),
                            new RandomChampHandler())
                    .build();
            jda.awaitReady();
        } catch (Exception e) {
            logger.error("Exception Caught: ", e);
        }
    }

}
