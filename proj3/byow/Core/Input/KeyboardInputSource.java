package byow.Core.Input;

import edu.princeton.cs.algs4.StdDraw;

/**
 * Code courtesy of Josh Hug
 */
public class KeyboardInputSource implements InputSource {
    @Override
    public char getNextKey() {
        return Character.toLowerCase(StdDraw.nextKeyTyped());
    }

    @Override
    public boolean possibleInput() {
        return StdDraw.hasNextKeyTyped();
    }
}
