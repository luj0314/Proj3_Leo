package byow.Core.WorldGeneration;

import byow.Core.Input.InputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {
    private static final int HUD_SIZE = 2;
    private static final TETile PLAYER_TILE = Tileset.AVATAR;
    private static final TETile BALL = Tileset.BALL;
    private static final TETile FLOOR = Tileset.FLOOR;
    private static final String SAVE_FILE = "saveFile.txt";
    private static final String DIGITS = "10987654321";
    private static final String POSSIBLE_ACTIONS = "aswdl";
    private static final int PAUSE_TIME = 30;
    private static final int MENU_FONT_SIZE = 36;
    private static final String GAME_OVER_MESSAGE = "Congratulations! You Won!";
    private static final int GAME_OVER_PAUSE = 1000;
    private static final Font MAIN_FONT = new Font("Arial", Font.BOLD, MENU_FONT_SIZE);
    private final int width;
    private final int height;
    private final TERenderer ter;
    private final InputSource inputSource;
    private final List<Character> playerActions;
    private final int worldHeight;
    private int winCondition;
    private int ballsCollected;
    private TETile[][] map;
    private TETile[][] toShow;
    private TETile[][] lightsOn;
    private TETile[][] lightsOff;
    private Player player;
    private long seed;
    private boolean gameOver;
    private TETile onHold;
    private boolean lights = true;
    private String date;

    public Game(int width, int height, TERenderer ter, InputSource inputSource) {
        this.width = width;
        this.worldHeight = height;
        this.height = height + HUD_SIZE;
        this.ter = ter;
        this.inputSource = inputSource;
        this.playerActions = new ArrayList<>();
        this.ballsCollected = 0;
        this.onHold = FLOOR;
    }

    private void updateWorld(long s) {
        WorldGenerator world;
        world = new WorldGenerator(s, width, worldHeight);
        this.map = world.getMap();
        this.player = new Player(world.getStartingRoomX(), world.getStartingRoomY(), PLAYER_TILE);
        this.lightsOn = this.map;
        this.toShow = this.lightsOn;
        updateMaps();
        this.winCondition = world.getNumRooms() - 1;
    }

    private void updateMaps() {
        lightsOff = new TETile[map.length][map[0].length];
        int xPos = player.getxPos();
        int yPos = player.getyPos();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < worldHeight; y++) {
                if (isWithinPlayerLight(x, xPos, y, yPos)) {
                    lightsOff[x][y] = (x == xPos && y == yPos) ? player.getAvatar() : map[x][y];
                } else {
                    lightsOff[x][y] = Tileset.GRASS;
                }
            }
        }

        lightsOn = map;
        toShow = (lights) ? lightsOff : lightsOn;
    }

    private boolean isWithinPlayerLight(int x, int playerX, int y, int playerY) {
        return (x >= playerX - 4 && x <= playerX + 4) && (y >= playerY - 4 && y <= playerY + 4);
    }

    private void drawHUD(String s) {
        this.ter.renderFrame(toShow);
        if (!gameOver) {
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.line(0, (double) this.height - HUD_SIZE, this.width, (double) this.height - HUD_SIZE);
            StdDraw.textLeft(0, this.height - (double) HUD_SIZE / 2, s);
            updateDate();
            String ballSoFar = ballsUpdate();
            String lightStatus = onOrOff();
            StdDraw.textLeft((double) this.width / 4, this.height - (double) HUD_SIZE / 2, lightStatus);
            StdDraw.textRight((double) 3 * this.width / 4, this.height - (double) HUD_SIZE / 2, ballSoFar);
            StdDraw.textRight(this.width, this.height - (double) HUD_SIZE / 2, "Date and Time: " + this.date);
        }
        StdDraw.show();
        StdDraw.pause(PAUSE_TIME);
    }

    private String onOrOff() {
        return "Press L to turn lights " + ((lights) ? "off" : "on");
    }

    private String ballsUpdate() {
        return "Dragon Balls Collected: " + ballsCollected + " and You need " + winCondition;
    }

    // This is from ChatGPT
    private void drawText(String text, double x, double y) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, MENU_FONT_SIZE));
        StdDraw.text(x, y, text);
    }

    private void startingHUD() {
        StdDraw.clear(StdDraw.BLACK);

        double centerX = (double) this.width / 2;
        double centerY = (double) this.height / 2;

        drawText("New Game (N)", centerX, centerY);
        drawText("Load Game (L)", centerX, centerY - 7.5);
        drawText("Quit (Q)", centerX, centerY - 10);

        StdDraw.show();

        boolean exitLoop = false;
        while (!exitLoop) {
            if (inputSource.possibleInput()) {
                char key = Character.toLowerCase(inputSource.getNextKey());
                switch (key) {
                    case 'n' -> {
                        this.seed = Long.parseLong(inputSeedHUD());
                        exitLoop = true;
                    }
                    case 'l' -> {
                        String values = readFile();
                        if (values.isEmpty()) {
                            System.exit(0);
                        }
                        parseInputString(values);
                        exitLoop = true;
                    }
                    case 'q' -> System.exit(0);
                    default -> { // do nothing
                    }
                }
            }
        }
    }

    // BigInteger documentation: https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html
    private String inputSeedHUD() {
        StringBuilder toReturn = new StringBuilder();
        BigInteger maxLong = new BigInteger(String.valueOf(Long.MAX_VALUE));
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(MAIN_FONT);
        double centerX = (double) this.width / 2;
        double centerY = (double) this.height / 2;
        while (true) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(centerX, centerY + 2.5, "Enter Seed, Press (S) when done.");
            StdDraw.text(centerX, centerY, toReturn.toString());
            if (toReturn.length() >= 18) {
                StdDraw.text(centerX, centerY - 2.5, "Can't enter anymore digits.");
            }
            StdDraw.show();
            StdDraw.pause(PAUSE_TIME / 3);
            if (inputSource.possibleInput()) {
                char toAdd = Character.toLowerCase(inputSource.getNextKey());
                if (toAdd == 's') {
                    break;
                } else if (DIGITS.indexOf(toAdd) != -1 && toReturn.length() < 18) {
                    toReturn.append(toAdd);
                }
            }
        }
        if (toReturn.isEmpty()) {
            toReturn.append("0");
        }
        BigInteger seedValue = new BigInteger(toReturn.toString());
        String returnSeed;
        if (seedValue.compareTo(maxLong) > 0) {
            returnSeed = String.valueOf(Long.MAX_VALUE);
        } else {
            returnSeed = toReturn.toString();
        }
        return returnSeed;
    }

    // Only for startGameWithStringInputSource
    private String inputSeed() {
        StringBuilder toReturn = new StringBuilder();
        while (true) {
            if (inputSource.possibleInput()) {
                char toAdd = Character.toLowerCase(inputSource.getNextKey());
                if (toAdd == 's') {
                    break;
                }
                if (DIGITS.indexOf(toAdd) != -1) {
                    toReturn.append(toAdd);
                }
            }
        }
        if (toReturn.isEmpty()) {
            toReturn.append("0");
        }
        return toReturn.toString();
    }

    // This is from ChatGPT
    private void parseInputString(String input) {
        int indexS = input.indexOf('S');
        this.seed = Long.parseLong(input.substring(1, indexS));
        String charArraySubstring = input.substring(indexS + 1, input.indexOf(']'));
        String charArray = charArraySubstring.replaceAll("\\s+", "");
        for (int i = 0; i < charArray.length(); i++) {
            if (POSSIBLE_ACTIONS.indexOf(charArray.charAt(i)) != -1) {
                playerActions.add(charArray.charAt(i));
            }
        }
    }

    public void startGameWithStringInputSource() {
        boolean exitLoop = false;
        while (!exitLoop) {
            if (inputSource.possibleInput()) {
                char key = Character.toLowerCase(inputSource.getNextKey());
                switch (key) {
                    case 'n' -> {
                        this.seed = Long.parseLong(inputSeed());
                        exitLoop = true;
                    }
                    case 'l' -> {
                        String values = readFile();
                        if (values == null) {
                            break;
                        }
                        parseInputString(values);
                        exitLoop = true;
                    }
                    default -> {/* do nothing */}
                }
            }
        }
        updateWorld(this.seed);
        onHold = WorldGenerator.FLOOR;
        swap(player.getxPos(), player.getyPos(), player.getxPos(), player.getyPos());
        for (char action : playerActions) {
            doNextAction(Character.toLowerCase(action), player.getxPos(), player.getyPos());
        }
        while (inputSource.possibleInput()) {
            char key = Character.toLowerCase(inputSource.getNextKey());
            if (key == ':') {
                quitStringInput(inputSource);
            } else if (POSSIBLE_ACTIONS.indexOf(key) != 1) {
                doNextAction(key, player.getxPos(), player.getyPos());
                playerActions.add(key);
            }
        }
    }

    public void startGame() {
        this.ter.initialize(this.width, this.height);
        startingHUD();
        updateWorld(this.seed);
        this.gameOver = false;
        swap(player.getxPos(), player.getyPos(), player.getxPos(), player.getyPos());
        drawIntroScreen();
        StdDraw.setFont();
        if (!playerActions.isEmpty()) {
            for (char key : playerActions) {
                doNextAction(key, player.getxPos(), player.getyPos());
            }
        }
        while (!gameOver) {
            int xPos = (int) StdDraw.mouseX();
            int yPos = (int) StdDraw.mouseY();
            if (isValidTile(xPos, yPos)) {
                String tile = toShow[xPos][yPos].description() + " tile";
                drawHUD(tile);
            } else {
                drawHUD("grass tile");
            }
            if (inputSource.possibleInput()) {
                char key = inputSource.getNextKey();
                if (key == 'q') {
                    quit(inputSource);
                } else if (POSSIBLE_ACTIONS.indexOf(key) != -1) {
                    doNextAction(key, player.getxPos(), player.getyPos());
                    playerActions.add(key);
                }
            }
            StdDraw.pause(PAUSE_TIME / 3);
            if (ballsCollected >= winCondition) {
                gameOver = true;
            }
        }
        drawGameOverHUD();
    }

    private void drawIntroScreen() {
        StdDraw.setFont(MAIN_FONT);
        for (int i = 0; i < 3; i++) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(((double) this.width / 2), ((double) this.height / 2 + 3),
                    "Collect " + winCondition + " DragonBalls.");
            StdDraw.text(((double) this.width / 2), ((double) this.height / 2) - 3,
                    "The game will start in " + (3 - i) + " seconds.");
            StdDraw.show();
            StdDraw.pause(GAME_OVER_PAUSE);
        }
    }

    private void drawGameOverHUD() {
        StdDraw.setFont(MAIN_FONT);
        for (int i = 0; i < 6; i++) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.text(((double) this.width / 2), ((double) this.height / 2) + 3, GAME_OVER_MESSAGE);
            StdDraw.text(((double) this.width / 2), ((double) this.height / 2) - 3,
                    "Closing in " + (5 - i) + " seconds.");
            StdDraw.show();
            StdDraw.pause(GAME_OVER_PAUSE);
        }
        System.exit(0);
    }

    // Got this from ChatGPT
    private void updateDate() {
        Date newDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
        this.date = formatter.format(newDate);
    }

    // From ChatGPT
    private boolean isValidTile(int xPos, int yPos) {
        return (xPos >= 0 && xPos < width && yPos >= 0 && yPos < height - HUD_SIZE);
    }

    // From ChatGPT
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && map[x][y] != WorldGenerator.WALL;
    }


    private void swap(int x1, int y1, int x2, int y2) {
        map[x1][y1] = onHold;
        onHold = map[x2][y2];
        map[x2][y2] = player.getAvatar();
        if (onHold == BALL) {
            ballsCollected++;
            onHold = FLOOR;
        }
    }

    // Switch case code from ChatGPT
    private void doNextAction(Character c, int xPos, int yPos) {
        int newXPos = xPos;
        int newYPos = yPos;

        switch (c) {
            case 'w' -> newYPos += 1;
            case 'a' -> newXPos -= 1;
            case 's' -> newYPos -= 1;
            case 'd' -> newXPos += 1;
            case 'l' -> switchMaps();
            default -> {
                return; // do nothing if invalid character input
            }
        }
        if (isValidMove(newXPos, newYPos)) {
            player.setxPos(newXPos);
            player.setyPos(newYPos);
            swap(xPos, yPos, newXPos, newYPos);
            updateMaps();
        }
    }

    private void switchMaps() {
        if (!lights) {
            toShow = lightsOn;
            lights = true;
        } else {
            toShow = lightsOff;
            lights = false;
        }
    }

    private void quit(InputSource input) {
        char nextKey;
        do {
            if (input.possibleInput()) {
                nextKey = Character.toLowerCase(input.getNextKey());
                if (nextKey == 'q') {
                    writeFile("N" + seed + "S" + playerActions);
                    System.exit(0);
                } else {
                    break;
                }
            }
        } while (true);
    }

    private void quitStringInput(InputSource input) {
        char nextKey;
        do {
            if (input.possibleInput()) {
                nextKey = Character.toLowerCase(input.getNextKey());
                if (nextKey == 'q') {
                    writeFile("N" + seed + "S" + playerActions);
                }
                break;
            }
        } while (true);
    }

    private String readFile() {
        String content;
        In reader = new In(SAVE_FILE);
        content = reader.readAll();
        return content;
    }

    private void writeFile(String toSave) {
        Out writer = new Out(SAVE_FILE);
        writer.print(toSave);
    }

    public TETile[][] getMap() {
        return this.map;
    }
}
