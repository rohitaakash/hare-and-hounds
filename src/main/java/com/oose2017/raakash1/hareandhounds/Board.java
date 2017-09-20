/**
 * @description This class describes a game board as list of 11 vertices
 * @author Rohit Aakash
 */

package com.oose2017.raakash1.hareandhounds;

import java.util.*;

public class Board {

    private ArrayList<Vertex> vertexList;

    public Board(ArrayList<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

    /**
     * Default constructor to initialize an empty board
     * set the pieces on board in their respective start positions
     */
    public Board() {
        this.initBoard();
    }

    public void initBoard() {

        //Create a two dimensional array of all playable coordinates defining the board
        int[][] allCoords = { { 1, 0 }, { 2, 0 }, { 3, 0 },
                { 0, 1 }, { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 },
                { 1, 2 }, { 2, 2 }, { 3, 2 } };
        this.vertexList = new ArrayList<Vertex>();

        //Add these coordinates to the vertex list
        for (int[] coord : allCoords) {
            this.vertexList.add(new Vertex(coord[0], coord[1],"NULL"));
        }
        this.setPieceOnBoard(0, 1, "HOUND");
        this.setPieceOnBoard(1, 0, "HOUND");
        this.setPieceOnBoard(1, 2, "HOUND");
        this.setPieceOnBoard(4, 1, "HARE");
    }

    /**
     * Method to place a piece on a vertex
     * @param x x-coordinate of the vertex
     * @param y y-coordinate of the vertex
     * @param pieceType value of "HARE" or "HOUND" or "NULL"
     */
    public void setPieceOnBoard(int x, int y, String pieceType){
        if (pieceType.equals("HARE") || pieceType.equals("HOUND") || pieceType.equals("NULL")) {
            for (Vertex vertex : this.vertexList) {
                if (vertex.getX() == x && vertex.getY() == y) {
                    vertex.setPieceType(pieceType);
                }
            }
        } else {
            System.err.println("Wrong pieceType: " + pieceType);
        }
    }

    /**
     * Get the piecetype present on a particular vertex
     * @param x x-coordinate of the vertex
     * @param y y-coordinate of the vertex
     * @return pieceType value of "HARE" or "HOUND" or "NULL"
     */
    public String getPieceFromBoard(int x, int y){
       String pieceType = "NULL";
        for (Vertex vertex : this.vertexList) {
            if (vertex.getX() == x && vertex.getY() == y) {
                pieceType = vertex.getPieceType();
            }
        }
        return pieceType;
    }

    public ArrayList<Vertex> getVertexList() {
        return vertexList;
    }

    public void setVertexList(ArrayList<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

    /**
     * Method to get the vertices of a passed pieceType
     * @param pieceType pieceType value of "HARE" or "HOUND"
     * @return list of vertices where the pieceType is present
     */
    public List<Vertex> getVerticesByPieceType(String pieceType) {
        List<Vertex> pieces = new ArrayList<Vertex>();
        for (Vertex v : this.vertexList) {
            if (v.getPieceType().equals(pieceType)) {
                pieces.add(v);
            }
        }
        return pieces;
    }

    /**
     * Method to create a ":" delimited string representing the board pieceTypes
     * @return a string with such a representation
     */
    public String getBoardStatusString() {
        List<String> status = new ArrayList<String>();
        for (Vertex v : this.vertexList) {
            status.add(v.getPieceType());
        }
        return String.join(":", status);
    }

}
