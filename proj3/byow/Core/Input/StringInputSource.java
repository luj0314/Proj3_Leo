package byow.Core.Input;

/**
 * Code courtesy of Josh Hug.
 */
public class StringInputSource implements InputSource {
    private final String input;
    private int index;

    public StringInputSource(String s) {
        index = 0;
        input = s;
    }

    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return returnChar;
    }

    @Override
    public boolean possibleInput() {
        return index < input.length();
    }

}
