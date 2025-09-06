package com.guessing.gamemaster.services;

import com.guessing.gamemaster.utils.GuessResult;

import java.time.LocalTime;
import java.util.Scanner;
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

    public static int calculateScore(int attempts, LocalTime playTime, int rangeLimit) {
        int score = 20;

        // Add difficulty bonus
        if (rangeLimit > 100 && rangeLimit < 501) {
            score += (int)(score/2);
        } else if (rangeLimit > 500) {
            score *= 2;
        }

        // Add speed bonus
        if (playTime.isBefore(LocalTime.of(0, 0, 30))){
            score += 30;
        }

        // Short attempts bonus
        if (attempts == 1) {
            score += 50;
        } else if (attempts < 4) {
            score += 10;
        }

        return score;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int target = generateTargetNumber(1, 100);
        int guess;
        GuessResult res;

        System.out.println("=== Guess a number between 1 and 100 ===");
        while (true){
            System.out.print("Enter your Guess: ");
            guess = sc.nextInt();

            res = checkGuess(guess, target);

            if (res.isCorrect()){
                System.out.println(res.message());
                break;
            } else {
                System.out.println(res.message());
            }
        }

    }
}
