package ratingsystems.common.interpreter;

import ratingsystems.common.interpreter.datatypes.Game;

public interface LimitingFunction {
    /**
     * Determines whether or not the game should be included
     *
     * @param game the game to be considered
     * @return true if the game should be included, false otherwise
     */
    boolean includeData(Game game);
}
