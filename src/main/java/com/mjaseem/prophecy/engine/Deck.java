package com.mjaseem.prophecy.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Deck of cards
public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        for (Records.Suit suit : com.mjaseem.prophecy.engine.Records.Suit.values()) {
            for (int i = 1; i <= 14; i++) {
                cards.add(new Card(suit, i));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<List<Card>> deal(int players, int cardsPerPlayer) {
        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            hands.add(new ArrayList<>(cards.subList(i * cardsPerPlayer, (i + 1) * cardsPerPlayer)));
        }
        return hands;
    }

    public void reset() {
        cards.clear();
        for (Records.Suit suit : com.mjaseem.prophecy.engine.Records.Suit.values()) {
            for (int i = 1; i <= 14; i++) {
                cards.add(new Card(suit, i));
            }
        }
    }
}
