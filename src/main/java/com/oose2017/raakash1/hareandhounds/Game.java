/**
 * Class containing the game model
 * A game has an id, 2 players, their corresponding pieceType and a Board
 * @author Rohit Aakash
 */

package com.oose2017.raakash1.hareandhounds;

import java.util.HashMap;

public class Game {

    private String gameId; // unique id of the game
    private String playerId1; // id of the first player
    private String playerId2; // id of the second player
    private String pieceType1; // pieceType of the first player
    private String pieceType2; // pieceType of the second player
    private Board board; // an object of the Board class
    private String state; // state of the game
    private HashMap<String, Integer> boardStatus; // state of the board


    public Game() {
    }

    //Define getters and setters for all member variables declared
    public HashMap<String, Integer> getBoardStatus() {
        return boardStatus;
    }

    public void setBoardStatus(HashMap<String, Integer> boardStatus) {
        this.boardStatus = boardStatus;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId1() {
        return playerId1;
    }

    public void setPlayerId1(String playerId1) {
        this.playerId1 = playerId1;
    }

    public String getPlayerId2() {
        return playerId2;
    }

    public void setPlayerId2(String playerId2) {
        this.playerId2 = playerId2;
    }

    public String getPieceType1() {
        return pieceType1;
    }

    public void setPieceType1(String pieceType1) {
        this.pieceType1 = pieceType1;
    }

    public String getPieceType2() {
        return pieceType2;
    }

    public void setPieceType2(String pieceType2) {
        this.pieceType2 = pieceType2;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", playerId1='" + playerId1 + '\'' +
                ", playerId2='" + playerId2 + '\'' +
                ", pieceType1='" + pieceType1 + '\'' +
                ", pieceType2='" + pieceType2 + '\'' +
                '}';
    }
}



