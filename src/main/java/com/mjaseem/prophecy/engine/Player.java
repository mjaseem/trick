package com.mjaseem.prophecy.engine;

import com.mjaseem.prophecy.strategy.Strategy;

import java.util.List;

// Representing a player
public class Player {
    private final String name;
    private final Strategy strategy;
    private List<Card> hand;

    public Player(Records.PlayerConfig playerConfig) {
        this.name = playerConfig.name();
        this.strategy = playerConfig.strategy();
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public Card playCard(Trick trick, GameHistory history, Records.Suit trumpSuit) {
        if (hand.isEmpty()) throw new IllegalStateException("Hand is empty. Can't play!");
        int cardI = strategy.chooseCard(hand, history, trick, trumpSuit);
        Card card = hand.get(cardI);
        Records.Suit leadSuit = trick.getPlays().isEmpty() ? null : trick.getPlays().getFirst().getValue().getSuit();
        if (leadSuit != null && card.getSuit() != leadSuit && hand.stream().anyMatch(c -> c.getSuit() == leadSuit)) {
            throw new IllegalArgumentException(name + " did not follow the lead getSuit.");
        }
        hand.remove(cardI);
        return card;
    }

    @Override
    public String toString() {
        return name + ": " + hand + "   ";
    }


    public String getName() {
        return name;
    }
}
