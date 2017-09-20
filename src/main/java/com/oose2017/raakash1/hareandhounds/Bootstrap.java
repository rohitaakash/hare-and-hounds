/**
 * Main class to start the SparkJava server
 * @author Rohit Aakash
 */

package com.oose2017.raakash1.hareandhounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.*;

public class Bootstrap {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 8080;

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception{

        logger.info("Bootstrap working");
        //Specify the IP address and Port at which the server should be run
        ipAddress(IP_ADDRESS);
        port(PORT);

        //Specify the sub-directory from which to serve static resources (like html and css)
        staticFileLocation("/public");

        try {
            GameService model = new GameService();
            new GameController(model);
        } catch (Exception ex) {
            logger.error("Failed to create a GameService instance. Aborting");
        }
    }

}
