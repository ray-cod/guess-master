package com.guessing.gamemaster.services;

import com.guessing.gamemaster.utils.GuessResult;

public class GameService {

    // Checks the validity of the guess
    public static GuessResult checkGuess(int guess, int target) {

        if (guess == target) {
            return new GuessResult(true, "Correct! You win!");
        } else if (guess < target) {
            return new GuessResult(false, "Too low!");
        } else {
            return new GuessResult(false, "Too high!");
        }
    }
}
