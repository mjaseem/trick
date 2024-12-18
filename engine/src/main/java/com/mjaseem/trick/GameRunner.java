package com.mjaseem.trick;

import com.mjaseem.trick.engine.GameEngine;
import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.strategy.TwoPlayerStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRunner {

    public static final int ITERATIONS = Config.DEBUG ? 5 : 500000;

    public static void main(String[] args) throws GameEngine.BadMoveException {

        List<Records.PlayerConfig> players = List.of(new Records.PlayerConfig("V3", new TwoPlayerStrategy(3)),
                new Records.PlayerConfig("V4", new TwoPlayerStrategy(4)));

        // Create a map to store results (scores against each opponent)
        Map<String, Map<String, Integer>> scores = new HashMap<>();

        for (Records.PlayerConfig player : players) {
            scores.put(player.name(), new HashMap<>());
        }

        // Run games for each pair of strategies
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                for (int k = 0; k < ITERATIONS; k++) {
                    Records.PlayerConfig player1 = players.get(i);
                    Records.PlayerConfig player2 = players.get(j);
                    GameEngine engine = new GameEngine(players);

                    engine.run();

                    // Get the scores from the engine (assuming it has a way to return scores by player)
                    int score1 = engine.getScores().get(player1.name());
                    int score2 = engine.getScores().get(player2.name());

                    // Update scores against each other
                    Map<String, Integer> player1Scores = scores.get(player1.name());
                    player1Scores.merge(player2.name(), (score1 > score2 ? 1 : 0), Integer::sum);
                    Map<String, Integer> player2Scores = scores.get(player2.name());
                    player2Scores.merge(player1.name(), (score2 > score1 ? 1 : 0), Integer::sum);
                }
            }
        }

        // Display final scores for each player against all opponents
        displayScores(scores);
    }

    private static void displayScores(Map<String, Map<String, Integer>> scores) {
        System.out.println("Scores Against Each Opponent:");
        for (String player : scores.keySet()) {
            System.out.println(player + ":");
            Map<String, Integer> opponentScores = scores.get(player);
            for (String opponent : opponentScores.keySet()) {
                System.out.println("  Against " + opponent + ": " + opponentScores.get(opponent) + "/" + ITERATIONS);
            }
            System.out.println();
        }
    }
}

