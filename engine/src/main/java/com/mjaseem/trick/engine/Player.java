package com.mjaseem.trick.engine;

import com.mjaseem.trick.strategy.PromptingStrategy;
import com.mjaseem.trick.strategy.Strategy;

import java.util.List;

// Representing a player
public class Player {
    private final String name;
    private final Strategy strategy;
    private List<Card> hand;
    private int score;

    public Player(Records.PlayerConfig playerConfig) {
        this.name = playerConfig.name();
        this.strategy = playerConfig.strategy();
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void countWin() {
        score++;
    }

    public Card playCard(Trick trick, GameHistory history, Records.Suit trumpSuit) throws GameEngine.BadMoveException {
        if (hand.isEmpty()) {
            throw new IllegalStateException("Hand is empty. Can't play!");
        }
        int cardI = strategy.chooseCard(hand, history, trick, trumpSuit);
        if (cardI < 0 || cardI >= hand.size()) {
            throw new GameEngine.BadMoveException(name, "chosen index out of bound");
        }
        Card card = hand.get(cardI);
        Records.Suit leadSuit = trick.plays().isEmpty() ? null : trick.plays().getFirst().getValue().suit();
        if (leadSuit != null && card.suit() != leadSuit && hand.stream().anyMatch(c -> c.suit() == leadSuit)) {
            throw new GameEngine.BadMoveException(name, "did not follow the lead suit");
        }
        hand.remove(cardI);
        return card;
    }

    public boolean shouldWait() {
        return strategy instanceof PromptingStrategy && ((PromptingStrategy) strategy).shouldWait();
    }

    @Override
    public String toString() {
        return name + ": " + hand;
    }


    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Records.PlayerState playerState() {
        return new Records.PlayerState(name, hand, score);
    }
}
