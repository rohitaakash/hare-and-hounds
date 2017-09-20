/**
 * Controller class to receive requests and send response to server
 * @author Rohit Aakash
 */

package com.oose2017.raakash1.hareandhounds;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class GameController {

    private static final String API_CONTEXT = "/hareandhounds/api/";

    private final GameService gameService;

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public GameController(GameService gameService) {
        logger.info("Game Controller working");
        this.gameService = gameService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        //Start a game
        post(API_CONTEXT + "/games", "application/json", (request, response) -> {
            try {
                if(request.body().isEmpty()){
                    response.status(400);
                    return Collections.EMPTY_MAP;
                }
                JSONObject result = this.gameService.createNewGame(request.body());
                if(result.containsKey("reason")) {
                    String reason = (String) result.get("reason");
                    if (reason.equals("MALFORMED_PIECETYPE")) {
                        response.status(400);
                        return result;
                    }
                }
                response.status(201);
                return result;
            } catch (GameService.GameServiceException ex) {
                logger.error("Failed to create new game");
                response.status(400);

            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        //Join a game
        put(API_CONTEXT + "/games/:id", "application/json", (request, response) -> {
            try {

                JSONObject result = this.gameService.joinGame(request.params(":id"));
                if(result.containsKey("reason")) {
                    String reason = (String) result.get("reason");
                    if (reason.equals("GAME_IS_FULL")) {
                        response.status(410);
                        return result;
                    }
                }
                response.status(200);
                return result;
            } catch (GameService.GameServiceException ex) {
                response.status(404);
                JSONObject obj = new JSONObject();
                obj.put("reason", "Invalid Game ID");
                return obj;
            }
        }, new JsonTransformer());

        //Play a game
        post(API_CONTEXT + "/games/:id/turns", "application/json", (request, response) -> {
            try {
                JSONObject result = this.gameService.takeTurn(request.body(), request.params(":id"));
                if(result.containsKey("reason")) {
                    String reason = (String) result.get("reason");
                    if(reason.equals("INVALID_GAME_ID")){
                        response.status(404);
                        return result;
                    }else if(reason.equals("INVALID_PLAYER_ID")){
                        response.status(404);
                        return result;
                    }else if(reason.equals("INCORRECT_TURN")){
                        response.status(422);
                        return result;
                    }else if(reason.equals("ILLEGAL_MOVE")){
                        response.status(422);
                        return result;
                    }else if(reason.equals("INVALID_PLAYER_ID")){
                        response.status(422);
                        return result;
                    }else if(reason.equals("INVALID_COORDINATES")){
                        response.status(422);
                        return result;
                    }
                }
                response.status(200);
                return result;
            } catch (GameService.GameServiceException ex) {
                logger.error("Failed to create new game");
                response.status(500);
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        //Describe the game board
        get(API_CONTEXT + "/games/:id/board", "application/json", (request, response)-> {
            try {
                response.status(200);
                return gameService.fetchBoard(request.params(":id"));
            } catch  (GameService.GameServiceException ex) {
                logger.error("Failed to fetch the board for game. Invalid game ID");
                response.status(404);
                JSONObject obj = new JSONObject();
                obj.put("reason", "INVALID_GAME_ID");
                return obj;
            }
        }, new JsonTransformer());

        //Describe the game state
        get(API_CONTEXT + "/games/:id/state", "application/json", (request, response)-> {
            try {
                response.status(200);
                return gameService.fetchState(request.params(":id"));
            } catch  (GameService.GameServiceException ex) {
                logger.error("Failed to fetch the state for game. Invalid game ID");
                response.status(404);
                JSONObject obj = new JSONObject();
                obj.put("reason", "INVALID_GAME_ID");
                return obj;
            }
        }, new JsonTransformer());

    }
}
