package byow.Core.WorldGeneration;

import java.util.Random;

import static byow.Core.RandomUtils.uniform;


public class Room {
    private static final int MAX_WIDTH = 20;
    private static final int MAX_HEIGHT = 20;
    private static final int MIN_WIDTH = 4;
    private static final int MIN_HEIGHT = 4;

    private final int left;
    private final int bottom;
    private final int right;
    private final int top;


    /**
     * Creates a new Room instance that will be used for the WorldGenerator class. Subtract by 1 for right and top
     * to remove off by 1 error that will happen since map is an array and is zero indexed.
     *
     * @param x the x position of the bottom left corner of the room
     * @param y the y position of the bottom left corner of the room
     * @param w the width of the room
     * @param h the height of the room
     */
    private Room(int x, int y, int w, int h) {
        this.left = x;
        this.bottom = y;
        this.right = left + w - 1;
        this.top = bottom + h - 1;
    }

    /**
     * Creates and returns a new instance of Room that has pseudo-random parameters.
     *
     * @param generator an instance of Random that will be used to generate the parameters
     * @param mapWidth  the width of the map, used to give us a range of valid x values
     * @param mapHeight the height of the map, used to give us a range of valid y values
     * @return returns the instance of Room created with random parameters
     */
    public static Room createNewRoom(Random generator, int mapWidth, int mapHeight) {
        int width = uniform(generator, MIN_WIDTH, MAX_WIDTH);
        int height = uniform(generator, MIN_HEIGHT, MAX_HEIGHT);
        int xCoordinate = uniform(generator, 0, mapWidth - width);
        int yCoordinate = uniform(generator, 0, mapHeight - height);
        return new Room(xCoordinate, yCoordinate, width, height);
    }

    /**
     * Getter for the left attribute of a room.
     *
     * @return the x coordinate of the left side of the room
     */
    public int getLeft() {
        return left;
    }

    /**
     * Getter for the right attribute of a room.
     *
     * @return returns the right side x coordinate of the room
     */
    public int getRight() {
        return right;
    }

    /**
     * Getter for the bottom attribute of a room.
     *
     * @return returns the bottom side y coordinate of the room
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * Getter for the top attribute of a room.
     *
     * @return returns the top side y coordinate of the room
     */
    public int getTop() {
        return top;
    }

    /**
     * Calculates the center x position by taking the midpoint between the left and right sides of a room.
     *
     * @return returns the center x position of a given room.
     */
    public int getCenterX() {
        return (left + right) / 2;
    }

    /**
     * Calculates the center y position by taking the midpoint between the bottom and top sides of the room.
     *
     * @return returns the center y position of a given room.
     */
    public int getCenterY() {
        return (bottom + top) / 2;
    }
}