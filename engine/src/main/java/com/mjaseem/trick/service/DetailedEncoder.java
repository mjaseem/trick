package com.mjaseem.trick.service;

import com.google.common.base.Preconditions;
import com.mjaseem.trick.engine.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailedEncoder implements GameStateEncoder {
    private final int MAX_HAND_SIZE = Records.MAX_CARDS; // Max number of cards per player

    @Override
    public double[] encode(Records.GameState gameState, String player) {
        List<Double> stateVector = new ArrayList<>();

        // Encode player states
        Records.PlayerState playerState = gameState.players().stream()
                .filter(p -> p.name().equals(player))
                .findAny().orElseThrow();
        stateVector.addAll(encodePlayerState(playerState)); // [0, playercount*5*MAX_HAND_SIZE = 130)

//        List<Double> tricksVector = encodeTrickHistory(gameState);

        List<Double> tricksVector = encodeTricks(gameState, true);
        stateVector.addAll(tricksVector);
        stateVector.addAll(encodeSuit(gameState.trump()));
        // Convert List<Double> to array
        return stateVector.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private List<Double> encodeTricks(Records.GameState gameState, boolean onlyCurrentTrick) {
        return onlyCurrentTrick ? encodeCurrentTrick(gameState) : encodeTrickHistory(gameState);
    }

    private List<Double> encodeTrickHistory(Records.GameState gameState) {
        List<Double> tricksVector = new ArrayList<>();
        for (Trick trick : gameState.gameHistory().getTricks()) {
            tricksVector.addAll(encodeTrick(trick, gameState.players().size()));
        }
        for (int i = gameState.gameHistory().getTricks().size(); i < MAX_HAND_SIZE; i++) {
            tricksVector.addAll(encodeTrick(new Trick(), gameState.players().size()));
        }
        return tricksVector;
    }

    private List<Double> encodeCurrentTrick(Records.GameState gameState) {
        List<Trick> trick = gameState.gameHistory().getTricks();
        if (trick.isEmpty() || trick.getLast().plays().size() == gameState.players().size()) {
            return encodeTrick(new Trick(), gameState.players().size());
        } else {
            return encodeTrick(trick.getLast(), gameState.players().size());
        }
    }

    private List<Double> encodePlayerState(Records.PlayerState playerState) {
        List<Double> playerVector = new ArrayList<>();

        // Encode the player's hand (each card as a vector of 5 elements)
        for (Card card : playerState.hand()) {
            playerVector.addAll(encodeCard(card));
        }

        // Pad with zero vectors if hand size is less than MAX_HAND_SIZE
        for (int i = playerState.hand().size(); i < MAX_HAND_SIZE; i++) {
            playerVector.addAll(encodeCard(null)); // Empty card
        }

        return playerVector;
    }

    private List<Double> encodeCard(Card card) {
        if (card == null) {
            return List.of(0.0, 0.0, 0.0, 0.0, 0.0);
        }
        // Encode suit as one-hot (4 elements)
        List<Double> cardVector = new ArrayList<>(encodeSuit(card.suit()));

        // Normalize rank between 0 and 1 (assuming rank 1-Records.MAX_CARDS)
        Preconditions.checkArgument(card.rank() > 0 && card.rank() <= Deck.MAX_CARD_RANK, "Bad card");
        cardVector.add(Math.min(1.0, (card.rank() - 1) / (double) (Deck.MAX_CARD_RANK - 1)));

        return cardVector;
    }

    private List<Double> encodeSuit(Records.Suit suit) {
        return switch (suit) {
            case HEARTS -> List.of(1.0, 0.0, 0.0, 0.0);
            case CLUBS -> List.of(0.0, 1.0, 0.0, 0.0);
            case DIAMONDS -> List.of(0.0, 0.0, 1.0, 0.0);
            case SPADES -> List.of(0.0, 0.0, 0.0, 1.0);
        };
    }

    private List<Double> encodeTrick(Trick trick, int playerCount) {
        List<Double> trickVector = new ArrayList<>();

        // Encode each card played in the trick (up to 13 cards)
        for (Map.Entry<Player, Card> play : trick.plays()) {
            trickVector.addAll(encodeCard(play.getValue()));
        }

        // Pad with zero vectors if fewer than MAX_TRICK_SIZE cards were played
        for (int i = trick.plays().size(); i < playerCount; i++) {
            trickVector.addAll(encodeCard(null)); // Empty card
        }

        return trickVector;
    }


}
