package com.mjaseem.prophecy.engine;

import com.mjaseem.prophecy.strategy.Strategy;

import java.util.List;

public interface Records {
    // Representing a player
    record PlayerConfig(String name, Strategy strategy) {
    }

    record PlayerState(String name, List<Card> hand) {
    }

    // Suit enumeration
    enum Suit {
        HEARTS, CLUBS, DIAMONDS, SPADES
    }
}
