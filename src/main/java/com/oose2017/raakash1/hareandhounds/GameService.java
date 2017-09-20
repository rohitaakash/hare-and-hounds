/**
 * Class to run implement the game methods:
 * create, join, play, fetchBoard and fetchState
 * @author Rohit Aakash
 */
package com.oose2017.raakash1.hareandhounds;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameService {

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    ArrayList<Game> gameList;

    /**
     * Create a new ArrayList to store all games
     * Set up the adjacency matrix
     */
    public GameService() {
        gameList = new ArrayList<Game>();
        Coordinate.setupAdjacencyMatrix();
    }

    /**
     * Create a new game
     * @param body the body to the request passed on by the controller
     * @return a JSONObject containing the desired response key values
     * @throws GameServiceException In case the request is a bad request
     */
    public JSONObject createNewGame(String body) throws GameServiceException  {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try{
            jsonObject = (JSONObject)jsonParser.parse(body);
            JSONObject obj = new JSONObject();
            String pieceType = (String)jsonObject.get("pieceType");
            logger.info("Player 1 chose : " +pieceType);

            //Fail if the pieceType isn't one of "HARE" or "HOUND"
            if (!"HARE".equals(pieceType) && !"HOUND".equals(pieceType)) {
                logger.error(String.format("Wrong pieceType passed"));
                obj.put("reason", "MALFORMED_PIECETYPE");
                return obj;
            }

            //Initalize game and add it to the game list
            Game game = initGame(pieceType);
            gameList.add(game);

            //Add json key values to the for the response object
            obj.put("gameId", game.getGameId());
            obj.put("playerId", game.getPlayerId1());
            obj.put("pieceType", game.getPieceType1());

            return obj;
        }catch(ParseException e){
            logger.error("Failed to parse pieceType");
            throw new GameServiceException("Failed to parse pieceType");
        }

    }

    /**
     * Initializes game, sets the game state and the board
     * @param pieceType pieceType of player 1
     * @return a Game object with entities initialized
     */
    public Game initGame(String pieceType) {
        Game game = new Game();
        game.setGameId("" + gameList.size());
        game.setPlayerId1("player1");
        game.setPieceType1(pieceType);
        game.setState("WAITING_FOR_SECOND_PLAYER");

        Board board = new Board();
        game.setBoard(board);

        HashMap<String, Integer> boardStatus = new HashMap<String, Integer>();
        Integer count = new Integer(1);
        String boardStatusString = board.getBoardStatusString();
        boardStatus.put(boardStatusString, count);
        game.setBoardStatus(boardStatus);
        return game;
    }

    /**
     * Method to join an already running game which is WAITING_FOR_SECOND_PLAYER
     * @param id id of the game to be joined
     * @return JSONObject consisting of desired response key and value
     * @throws GameServiceException
     */
    public JSONObject joinGame(String id) throws GameServiceException, ParseException{
        Game game;
        String pieceType1;
        JSONObject obj = new JSONObject();
        try{

            game = gameList.get(Integer.parseInt(id));
            pieceType1 = game.getPieceType1();

            //Join a game possible only if it's waiting for second player
            if(!game.getState().contains("WAITING")){
                logger.error("Second Player already joined");
                obj.put("reason", "GAME_IS_FULL");
                return obj;
            }

            //If player2 is already set, the game is full
            if("player2".equals(game.getPlayerId2())){
                logger.error("Second Player already joined");
                obj.put("reason", "GAME_IS_FULL");
                return obj;
            }

            if("HARE".equals(pieceType1)){
                game.setPieceType2("HOUND");
            }else{
                game.setPieceType2("HARE");
            }

            game.setPlayerId2("player2");
            game.setState("TURN_HOUND");

            obj.put("gameId", id);
            obj.put("playerId", game.getPlayerId2());
            obj.put("pieceType", game.getPieceType2());

            return obj;
        }catch( Exception e){
            logger.error("Couldn't join the game");
            throw new GameServiceException("Couldn't join the game");
        }
    }

    /**
     * Fetches the board in a particular game
     * @param gameId id of the game for which the board is to be fetched
     * @return a JSONArray of vertices
     * @throws GameServiceException
     */
    public JSONArray fetchBoard(String gameId) throws GameServiceException{
        Game game;
        try {
            game = gameList.get(Integer.parseInt(gameId));
            Board board = game.getBoard();
            JSONArray jsonArray = new JSONArray();

            for(Vertex v : board.getVertexList()) {
                if("HARE".equals(v.getPieceType()) || "HOUND".equals(v.getPieceType())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("pieceType", v.getPieceType());
                    jsonObject.put("x", v.getX());
                    jsonObject.put("y", v.getY());
                    jsonArray.add(jsonObject);
                }
            }

            return jsonArray;
        } catch (Exception e) {
            logger.error("Couldn't fetch board");
            throw new GameServiceException("Couldn't fetch board");
        }
    }

    /**
     * Method to fetch the state of the game
     * WAITING_FOR_SECOND_PLAYER, TURN_HARE, TURN_HOUND, WIN_HARE_BY_ESCAPE, WIN_HARE_BY_STALLING, WIN_HOUND
     * @param gameId the id of the game for which the state is to be fetched
     * @return JSONObject consisting of the desired response key and value
     * @throws GameServiceException
     */
    public JSONObject fetchState(String gameId) throws GameServiceException{
        Game game;
        try {
            game = gameList.get(Integer.parseInt(gameId));
            JSONObject obj = new JSONObject();
            obj.put("state", game.getState());
            return obj;
        } catch (Exception e) {
            logger.error("Couldn't fetch state");
            throw new GameServiceException("Couldn't fetch state");
        }
    }

    /**
     * Method to compute turns for a player
     * @param body body of the request sent by the server
     * @param id id of the game being played
     * @return JSONObject containing the pieceType which is to play next move
     * @throws GameServiceException
     * @throws ParseException
     */
    public JSONObject takeTurn(String body, String id) throws GameServiceException, ParseException{

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject, obj = new JSONObject();
        Game game;
        String pieceTypeTurn, playerPlaying;
        int fromX, fromY, toX, toY;

        try {
            jsonObject = (JSONObject)jsonParser.parse(body);

            if(!isNumeric(id)){
                logger.error("Invalid Game ID");
                obj.put("reason", "INVALID_GAME_ID");
                return obj;
            }

            if(gameList.size() <= Integer.parseInt(id)){
                logger.error("Invalid Game ID");
                obj.put("reason", "INVALID_GAME_ID");
                return obj;
            }
            game = gameList.get(Integer.parseInt(id));
            if(game == null){
                logger.error("Invalid Game ID");
                obj.put("reason", "INVALID_GAME_ID");
                return obj;
            }
            //playerId = (String)jsonObject.get("playerId");
            if(!game.getState().contains("TURN")){
                logger.error("INCORRECT_TURN");
                obj.put("reason", "INCORRECT_TURN");
                return obj;
            }
            playerPlaying = (String)jsonObject.get("playerId");
            if (!playerPlaying.equals("player1") && !playerPlaying.equals("player2")) {
                logger.error("Invalid Player ID");
                obj.put("reason", "INVALID_PLAYER_ID");
                return obj;
            }

            fromX = Integer.parseInt((String)jsonObject.get("fromX"));
            fromY = Integer.parseInt((String)jsonObject.get("fromY"));
            toX = Integer.parseInt((String)jsonObject.get("toX"));
            toY = Integer.parseInt((String)jsonObject.get("toY"));

            if(areIllegalCoordinates(fromX, fromY,toX,toY)) {
                logger.error("ILLEGAL_MOVE");
                obj.put("reason", "ILLEGAL_MOVE");
                return obj;
            }

            if("NULL".equals(game.getBoard().getPieceFromBoard(fromX,fromY))){
                logger.error("ILLEGAL_MOVE");
                obj.put("reason", "ILLEGAL_MOVE");
                return obj;
            }

            if(!"NULL".equals(game.getBoard().getPieceFromBoard(toX,toY))){
                logger.error("ILLEGAL_MOVE");
                obj.put("reason", "ILLEGAL_MOVE");
                return obj;
            }

            if(!(playerPlaying.equals("player1") || playerPlaying.equals("player2"))){
                logger.error("INVALID_PLAYER_ID");
                obj.put("reason", "INVALID_PLAYER_ID");
                return obj;

            }

            pieceTypeTurn = game.getState().split("_")[1];
            logger.info("pieceTypeTurn = " +pieceTypeTurn);

            if((playerPlaying.equals("player1") && pieceTypeTurn.equals(game.getPieceType2()))
                    || (playerPlaying.equals("player2"))&& (pieceTypeTurn.equals(game.getPieceType1()))) {
                logger.error("INCORRECT_TURN");
                obj.put("reason", "INCORRECT_TURN");
                return obj;
            }

            if(isIllegalMove(game, playerPlaying, pieceTypeTurn, fromX, fromY, toX, toY)) {
                logger.error("ILLEGAL MOVE");
                obj.put("reason", "ILLEGAL_MOVE");
                return obj;
            }

            movePiece(game, pieceTypeTurn, fromX, fromY, toX, toY);

            String winStatus = checkGameState(game);
            if(winStatus.equals("NULL")){
                if(game.getState().equals("TURN_HARE")){
                    game.setState("TURN_HOUND");
                }else if(game.getState().equals("TURN_HOUND")){
                    game.setState("TURN_HARE");
                }
            } else {
                game.setState(winStatus);
            }

            if(playerPlaying == "player1") {
                obj.put("playerId", "player2");
            } else if(playerPlaying == "player2") {
                obj.put("playerId", "player1");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return obj;
    }

    /**
     * Moves a piece from one coordinate to the destination coordinate
     * @param game Game object of the game being played
     * @param pieceType current pieceType being moved
     * @param fromX x-coordinate of the initial position
     * @param fromY y-coordinate of the initial positon
     * @param toX x-coordinate of the final position
     * @param toY y-coordinate of the final position
     */
    public void movePiece(Game game, String pieceType, int fromX, int fromY, int toX, int toY) {
        game.getBoard().setPieceOnBoard(fromX, fromY, "NULL");
        game.getBoard().setPieceOnBoard(toX, toY, pieceType);
    }

    /**
     * Check if the coordinates exist on the board and are playable
     * @param fromX x-coordinate of the initial position
     * @param fromY y-coordinate of the initial positon
     * @param toX x-coordinate of the final position
     * @param toY y-coordinate of the final position
     * @return whether the coordinates are playable
     */
    public boolean areIllegalCoordinates(int fromX, int fromY, int toX, int toY) {

        boolean isIllegal = false;
        if((fromX < 0 || fromX > 4) || (fromY < 0 || fromY > 2) || (toX < 0 || toX > 4) || (toY < 0 || toY > 2)) {
            isIllegal = true;
        }

        if((fromX == 0 && fromY == 0) || (fromX == 4 && fromY == 0) || (fromX == 0 && fromY == 2)  || (fromX == 4 && fromY == 2)) {
            isIllegal = true;
        }

        if((toX == 0 && toY == 0) || (toX == 4 && toY == 0) || (toX == 0 && toY == 2)  || (toX == 4 && toY == 2)) {
            isIllegal = true;
        }

        return isIllegal;
    }

    /**
     * Check if the move being made is allowed or not
     * @param game Game object of the game being played
     * @param playerPlaying id of the player who is currently making a move
     * @param pieceType the pieceType being moved
     * @param fromX x-coordinate of the initial position
     * @param fromY y-coordinate of the initial positon
     * @param toX x-coordinate of the final position
     * @param toY y-coordinate of the final position
     * @return if the move is allowed
     */
    public boolean isIllegalMove(Game game, String playerPlaying, String pieceType,  int fromX, int fromY, int toX, int toY) {

        boolean isIllegal = false;

        if(!"NULL".equals(game.getBoard().getPieceFromBoard(toX,toY))) {
            isIllegal = true;
        }

        if(pieceType.equals("HOUND") && toX < fromX){
            isIllegal = true;
        }

        if(Math.abs(toX - fromX) > 1 || Math.abs(toY - fromY ) > 1){
            isIllegal = true;
        }

        if((toX == 2 && toY == 0 && fromY == 1) || (toX == 2 && toY == 2 && fromY == 1)){
            if(fromX == 1 || fromX == 3) {
                isIllegal = true;
            }
        }

        if((toX == 1 && toY == 1 && fromX == 2) || (toX == 3 && toY == 1 && fromX == 2)){
            if(fromY == 0 || fromY == 2) {
                isIllegal = true;
            }
        }

        return isIllegal;
    }

    /**
     * Checks game state after every move
     * @param game Game object of the game being played
     * @return updated win state of the game after a move is made
     */
    public String checkGameState(Game game) {
        Vertex hare = game.getBoard().getVerticesByPieceType("HARE").get(0);
        List<Vertex> hounds = game.getBoard().getVerticesByPieceType("HOUND");
        if (hounds.get(0).getX() >= hare.getX() &&
                hounds.get(1).getX() >= hare.getX() &&
                hounds.get(2).getX() >= hare.getX()) {
            return "WIN_HARE_BY_ESCAPE";
        }

        boolean flag = true;
        Coordinate hare_c = new Coordinate(hare.getX(), hare.getY());
        for (Coordinate c : Coordinate.getMap().get(hare_c)) {
            String piece = game.getBoard().getPieceFromBoard(c.x,c.y);
            flag &= !piece.equals("NULL");
        }

        if (flag) {
            return "WIN_HOUND";
        }

        String boardStatusString = game.getBoard().getBoardStatusString();
        Integer count = new Integer(0);
        if(game.getBoardStatus().containsKey(boardStatusString)) {
            count = game.getBoardStatus().get(boardStatusString);
        }
        game.getBoardStatus().put(boardStatusString, count + 1);
        if(count.intValue() + 1 == 3) {
            return "WIN_HARE_BY_STALLING";
        }
        return "NULL";
    }

    public static class GameServiceException extends Exception {
        public GameServiceException(String message) {
            super(message);
        }
    }

    public static class InvalidPlayerException extends Exception {
        public InvalidPlayerException(String message) {
            super(message);
        }
    }

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
}
