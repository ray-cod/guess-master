package com.guessing.gamemaster.services;

import com.guessing.gamemaster.utils.GuessResult;

import java.util.concurrent.ThreadLocalRandom;

public class GameService {

    public static int generateTargetNumber(int minNumber, int maxNumber) {
        return ThreadLocalRandom.current().nextInt(minNumber, maxNumber + 1);
    }

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
