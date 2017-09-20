/**
 * Class to set up an adjacency matrix for each vertex
 * @author Rohit Aakash
 */

package com.oose2017.raakash1.hareandhounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Coordinate {

    private static final Logger logger = LoggerFactory.getLogger(Coordinate.class);

    int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private static HashMap<Coordinate, List<Coordinate>> map;

    /**
     * Define all coordinates
     * Add the list of coordinates neighbouring a vertex to the map
     */
    public static void setupAdjacencyMatrix() {

        Coordinate c10 = new Coordinate(1,0);
        Coordinate c20 = new Coordinate(2,0);
        Coordinate c30 = new Coordinate(3,0);
        Coordinate c01 = new Coordinate(0,1);
        Coordinate c11 = new Coordinate(1,1);
        Coordinate c21 = new Coordinate(2,1);
        Coordinate c31 = new Coordinate(3,1);
        Coordinate c41 = new Coordinate(4,1);
        Coordinate c12 = new Coordinate(1,2);
        Coordinate c22 = new Coordinate(2,2);
        Coordinate c32 = new Coordinate(3,2);

        map = new HashMap<Coordinate, List<Coordinate>>();

        List<Coordinate> list  = new ArrayList<Coordinate>();

        list.add(c01);
        list.add(c21);
        list.add(c11);
        list.add(c20);
        map.put(c10, list);
        list = new ArrayList<Coordinate>();

        list.add(c10);
        list.add(c30);
        list.add(c21);
        map.put(c20,list);
        list = new ArrayList<Coordinate>();

        list.add(c20);
        list.add(c21);
        list.add(c31);
        list.add(c41);
        map.put(c30,list);
        list = new ArrayList<Coordinate>();

        list.add(c10);
        list.add(c12);
        list.add(c11);
        map.put(c01,list);
        list = new ArrayList<Coordinate>();

        list.add(c01);
        list.add(c10);
        list.add(c12);
        list.add(c21);
        map.put(c11,list);
        list = new ArrayList<Coordinate>();

        list.add(c10);
        list.add(c20);
        list.add(c30);
        list.add(c11);
        list.add(c32);
        list.add(c22);
        list.add(c12);
        list.add(c31);
        map.put(c21,list);
        list = new ArrayList<Coordinate>();

        list.add(c41);
        list.add(c32);
        list.add(c21);
        list.add(c30);
        map.put(c31,list);
        list = new ArrayList<Coordinate>();

        list.add(c31);
        list.add(c30);
        list.add(c32);
        map.put(c41,list);
        list = new ArrayList<Coordinate>();

        list.add(c01);
        list.add(c11);
        list.add(c21);
        list.add(c22);
        map.put(c12,list);
        list = new ArrayList<Coordinate>();

        list.add(c12);
        list.add(c21);
        list.add(c32);
        map.put(c22,list);
        list = new ArrayList<Coordinate>();

        list.add(c22);
        list.add(c21);
        list.add(c31);
        list.add(c41);
        map.put(c32,list);

    }

    public static HashMap<Coordinate, List<Coordinate>> getMap() {
        return map;
    }

    /**
     * Overriding the default equals comparison to compare the value rather than reference
     * @param o the object to be compared
     * @return whether the value comparison is true or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        boolean result = true;
        if (x != that.x)
            result = false ;
        if (y == that.y)
            result = true;
        return result;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }


}


