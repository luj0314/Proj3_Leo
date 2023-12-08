package byow.Core.WorldGeneration;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WorldGenerator {
    public static final TETile NOTHING = Tileset.WATER;
    public static final TETile FLOOR = Tileset.FLOOR;
    public static final TETile WALL = Tileset.WALL;
    public static final TETile BALL = Tileset.BALL;

    private static final int MAX_NUM_ROOMS = 10;
    private static final int MIN_NUM_ROOMS = 5;
    private final int mapWidth;
    private final int mapHeight;
    private final int numRooms;

    private final Random generator;
    private final TETile[][] map;
    private final EdgeWeightedGraph graph;
    private final ArrayList<Room> rooms;

    /**
     * Creates a WorldGenerator object whose sole objective is to create a map that can be rendered
     * using the TERenderer class.
     *
     * @param seed   used for the pseudo-random number generator
     * @param width  sets the width of the screen
     * @param height sets the height of the screen
     */
    public WorldGenerator(long seed, int width, int height) {
        mapWidth = width;
        mapHeight = height;
        generator = new Random(seed);
        map = new TETile[mapWidth][mapHeight];
        numRooms = RandomUtils.uniform(generator, MIN_NUM_ROOMS, MAX_NUM_ROOMS);
        rooms = new ArrayList<>();
        graph = new EdgeWeightedGraph(numRooms);
        createMap();
    }

    /**
     * This method consolidates having to call initializeMap(), createNewRooms(), and createHallways() into one
     * command
     */
    private void createMap() {
        initializeMap();
        createNewRooms();
        createHallways();
        addBallsToRooms();
    }

    /**
     * Starting at 0, it will create valid rooms as long as there are still rooms left to make, which is decided
     * by numRooms. Code for the ternary operator and lambda expression was found on the java documentation:
     * ternary operator: <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html">...</a>
     * lambda expression: <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html">...</a>
     */
    private void createNewRooms() {
        int roomsSoFar = 0;
        while (roomsSoFar < this.numRooms) {
            Room newRoom = Room.createNewRoom(generator, mapWidth, mapHeight);
            if (rooms.stream().noneMatch(room -> overlap(newRoom))) {
                addRoom(newRoom);
                addEdges(newRoom, roomsSoFar);
                roomsSoFar++;
            }
        }
    }

    /**
     * Just consolidates having to call addRoomToMap and adding the room to the list of rooms.
     *
     * @param room instance of room to be added to the instance variable rooms.
     */
    private void addRoom(Room room) {
        addRoomToMap(room);
        rooms.add(room);
    }

    /**
     * World Generator uses a EdgeWeightedGraph to store how far apart each vertex is from each other, which will be
     * used later to calculate which hallways should be created.
     *
     * @param newRoom      newly created instance of a room that will be added to the graph
     * @param newRoomIndex the index of the newly created room that is used to store it into rooms
     */
    private void addEdges(Room newRoom, int newRoomIndex) {
        for (int i = 0; i < newRoomIndex; i++) {
            Room oldRoom = rooms.get(i);
            double distanceTo = findDistance(newRoom, oldRoom);
            Edge distance = new Edge(newRoomIndex, i, distanceTo);
            graph.addEdge(distance);
        }
    }

    /**
     * Creates a new instance of an MST using Kruskal's algorithm and calculates which hallways to build based on the
     * outputted MST. Calls digHallwayBetweenRooms() for every edge found in the MST.
     */
    private void createHallways() {
        KruskalMST kruskal = new KruskalMST(this.graph);
        Iterable<Edge> edges = kruskal.edges();
        for (Edge edge : edges) {
            Room firstRoom = rooms.get(edge.either());
            Room secondRoom = rooms.get(edge.other(edge.either()));
            digHallwayBetweenRooms(firstRoom, secondRoom);
        }
    }

    /**
     * This method consolidates making a new digger for every edge and calls the digger.dig() function to create
     * the hallway between firstRoom and secondRoom
     *
     * @param firstRoom  instance of Room that is the starting point for the digger
     * @param secondRoom instance of Room that is the end point for the digger
     */
    private void digHallwayBetweenRooms(Room firstRoom, Room secondRoom) {
        Digger digger = new Digger(firstRoom.getCenterX(), firstRoom.getCenterY(),
                secondRoom.getCenterX(), secondRoom.getCenterY());
        digger.dig();
    }

    /**
     * Basic getter method to return the map to the TileEngine so that it can render the map.
     *
     * @return the 2D Tile array that is associated with this WorldGenerator instance
     */
    public TETile[][] getMap() {
        return this.map;
    }

    /**
     * Sets the map to nothing tiles since the rendering of the map will fail if there are null values.
     * Code was improved by ChatGPT, it was initially a nested for loop that set each value to NOTHING, documentation
     * was looked at after to understand what the code was doing.
     * Use of lambda expressions, stream, and fill found in java documentation:
     * lambda expressions: <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html">...</a>
     * stream(): <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html">...</a>
     * fill(): <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html">...</a>
     */
    private void initializeMap() {
        Arrays.stream(map).forEach(row -> Arrays.fill(row, NOTHING));
    }

    /**
     * Take a given room and adds the tiles needs to the map. When we are iterating, if the position is in the perimeter
     * we will add a wall tile instead of a floor tile.
     *
     * @param room instance of Room that is going to be added to the map
     */
    private void addRoomToMap(Room room) {
        int left = room.getLeft();
        int right = room.getRight();
        int bottom = room.getBottom();
        int top = room.getTop();

        for (int x = left; x <= right; x++) {
            for (int y = bottom; y <= top; y++) {
                TETile tile = (x == left || x == right || y == bottom || y == top) ? WALL : FLOOR;
                addTile(tile, x, y);
            }
        }
    }

    /**
     * Simply sets the corresponding x and y value in the map to the tile given.
     *
     * @param tile type of tile that map[x][y] is going to be set as.
     * @param x    x position of the tile
     * @param y    y position of the tile
     */
    private void addTile(TETile tile, int x, int y) {
        this.map[x][y] = tile;
    }

    /**
     * Finds the Euclidean distance between two rooms.
     *
     * @param room1 the instance of the first room that we will use to get the first set of x,y coordinates
     * @param room2 the instance of the second room that we will use to get the second set of x,y coordinates
     * @return returns the distance between two rooms using the midpoint formula.
     */
    private double findDistance(Room room1, Room room2) {
        int xDistance = room1.getCenterX() - room2.getCenterX();
        int yDistance = room1.getCenterY() - room2.getCenterY();
        return Math.sqrt((double) xDistance * xDistance + yDistance * yDistance);
    }

    /**
     * Checks to see if a room overlaps another room by iterating over map and checking to see if any of the potential
     * room's tiles are taken already.
     *
     * @param room instance of Room that we are trying to insert into the map.
     * @return returns true if there is an overlap, false otherwise
     */
    private boolean overlap(Room room) {
        for (int x = room.getLeft(); x <= room.getRight(); x++) {
            for (int y = room.getBottom(); y <= room.getTop(); y++) {
                if (map[x][y] != NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getStartingRoomX() {
        return rooms.get(0).getCenterX();
    }

    public int getStartingRoomY() {
        return rooms.get(0).getCenterY();
    }

    private void addBallsToRooms() {
        for (Room room : rooms) {
            addTile(BALL, room.getCenterX(), room.getCenterY());
        }
    }

    public int getNumRooms() {
        return numRooms;
    }


    /**
     * Digger class is the abstraction used to create hallways between rooms. The idea was that if you had a digger
     * that starts in one room, and you give it a path, it will carve said path between the two rooms, creating
     * a hallway.
     */
    private class Digger {
        private final int endX;
        private final int endY;
        private final int deltaX;
        private final int deltaY;
        private int currX;
        private int currY;

        /**
         * Creates a digger instance to be used to create hallways. Not listed as a parameter, but the digger will use
         * a deltaX and deltaY instance variable to determine which direction it should go. For example, if the digger
         * needs to go right and down, deltaX = 1 and deltaY = -1, which indicates in which x and y direction it will
         * have to take steps in.
         * ternary operator: <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op2.html">...</a>
         *
         * @param currX the digger's initial x position
         * @param currY the digger's initial y position
         * @param endX  the digger's targeted x position
         * @param endY  the digger's targeted y position
         */
        private Digger(int currX, int currY, int endX, int endY) {
            this.currX = currX;
            this.currY = currY;
            this.endX = endX;
            this.endY = endY;
            this.deltaX = (currX > endX) ? -1 : 1;
            this.deltaY = (currY > endY) ? -1 : 1;
        }

        /**
         * Checks to see if the digger has first met its target x position and makes horizontal hallways in that
         * direction. Once it reaches the targeted x position, it will proceed in the y direction and make vertical
         * hallways until it reaches its desired destination.
         */
        public void dig() {
            while (currX != endX) {
                makeHorizontalHallways();
            }
            while (currY != endY) {
                makeVerticalHallways();
            }
        }

        /**
         * Makes horizontal hallways by setting the tile it's on to a floor tile and its adjacent tiles to wall tiles
         * if they are empty. There is a check for when it reaches the corner. The check was put in because there was
         * an error in which it would get to the targeted x position, but would not place tiles and instead go upwards,
         * leaving open corners. This check was added to fix that.
         * <p>
         * #### for example, where # is a wall tile and - is a floor tile
         * ----
         * ####
         */
        private void makeHorizontalHallways() {
            addTile(FLOOR, currX, currY);
            addAdjacentHorizontalHallwayTiles(currX, currY);
            if (currX + deltaX == endX) {
                addTile(FLOOR, currX + deltaX, currY);
                addAdjacentCornerTiles(currX + deltaX, currY);
            }
            currX += deltaX;
        }

        /**
         * Makes vertical hallways by setting the tile it's on to a floor tile and the horizontally adjacent tiles
         * to wall tiles if they are empty.
         * <p>
         * #-# for example, where # is a wall tile and - is a floor tile.
         * #-#
         * #-#
         * #-#
         */
        private void makeVerticalHallways() {
            addAdjacentVerticalHallwayTiles(currX, currY);
            addTile(FLOOR, currX, currY);
            currY += deltaY;
        }

        /**
         * Add the tiles to map that will create a 1 width, 3 height hallway.
         * # for example.
         * -
         * #
         *
         * @param x the current x position of the digger
         * @param y the current y position of the digger
         */
        private void addAdjacentHorizontalHallwayTiles(int x, int y) {
            addAdjacentTileIfEmpty(x, y - 1);
            addAdjacentTileIfEmpty(x, y + 1);
        }

        /**
         * Adds the tiles to map that will create a 3 width, 1 height hallway.
         * #-# for example.
         *
         * @param x the current x position of the digger
         * @param y the current y position of the digger
         */
        public void addAdjacentVerticalHallwayTiles(int x, int y) {
            addAdjacentTileIfEmpty(x - 1, y);
            addAdjacentTileIfEmpty(x + 1, y);
        }

        /**
         * Is used for the corner edge case. If the digger is 1 before the intended x position, it will look ahead and
         * create a corner there.
         *
         * @param x the current x position of the digger
         * @param y the current y position of the digger
         */
        private void addAdjacentCornerTiles(int x, int y) {
            addAdjacentTileIfEmpty(x, y - 1);
            addAdjacentTileIfEmpty(x, y + 1);
            addAdjacentTileIfEmpty(x + deltaX, y - 1);
            addAdjacentTileIfEmpty(x + deltaX, y + 1);
        }

        /**
         * Checks to see if a given tile is set to NOTHING in the map and if it is, it will set map[x][y] to tile.
         *
         * @param x the x position of the tile
         * @param y the y position of the tile
         */
        private void addAdjacentTileIfEmpty(int x, int y) {
            if (map[x][y] == NOTHING) {
                addTile(WorldGenerator.WALL, x, y);
            }
        }
    }
}
