package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('+', Color.white, Color.black, "you", "/byow/img.png");
    public static final TETile WALL = new TETile('|', new Color(216, 128, 128), Color.darkGray,
            "wall", "/byow/img_1.png");
    public static final TETile FLOOR = new TETile(' ', new Color(128, 192, 128), Color.black,
            "floor", "/byow/img_2.png");
    public static final TETile GRASS = new TETile(' ', Color.black, Color.black, "grass", "/byow/img_6.png");
    public static final TETile WATER = new TETile(' ', Color.blue, Color.black, "water", "/byow/img_4.png");
    public static final TETile BALL = new TETile('0', Color.magenta, Color.pink, "ball", "/byow/img_3.png");
}


