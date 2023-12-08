package byow.Core.WorldGeneration;

import byow.TileEngine.TETile;

public class Player {
    private final TETile avatar;
    private int xPos;
    private int yPos;

    public Player(int x, int y, TETile a) {
        xPos = x;
        yPos = y;
        avatar = a;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public TETile getAvatar() {
        return avatar;
    }

}
