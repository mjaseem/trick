package com.mjaseem.trick.engine;

import com.mjaseem.trick.strategy.Strategy;

import java.util.List;

public interface Records {
    int MAX_CARDS = 13;

    // Representing a player
    record PlayerConfig(String name, Strategy strategy) {
    }

    record PlayerState(String name, List<Card> hand, int score) {
    }

    // Suit enumeration
    enum Suit {
        HEARTS, CLUBS, DIAMONDS, SPADES
    }

    record GameState(List<PlayerState> players, int turnCount, GameHistory gameHistory) {
    }
}
