/**
 * Class defining each playable position on a board
 * @author: Rohit Aakash
 */

package com.oose2017.raakash1.hareandhounds;

public class Vertex {

    private int x; //x-coordinate of a vertex
    private int y; //y-coordinate of a vertex
    private String pieceType; //pieceType present on a vertex

    public Vertex(int x, int y, String pieceType) {
        this.x = x;
        this.y = y;
        this.pieceType = pieceType;
    }

    //Define getters and setters for all the member variable declared

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }


}
