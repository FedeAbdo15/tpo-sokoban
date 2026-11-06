package com.sokoban.exception;

public class InvalidLevelException extends Exception {
    private final int line;

    /**
     * @param message the rule that was broken
     * @param line    1-based line of the level file, or -1 if the rule is not tied to a line
     */
    public InvalidLevelException(String message, int line) {
        super(line > 0 ? message + " (line " + line + ")" : message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
