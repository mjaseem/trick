package com.mjaseem.prophecy.engine;

import java.util.Map;

public record GameState(Map<String, Player> players, int turnCount, GameHistory gameHistory,
                        Map<String, Integer> scores) {
}
