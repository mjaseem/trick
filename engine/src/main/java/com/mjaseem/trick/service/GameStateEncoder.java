package com.mjaseem.trick.service;

import com.mjaseem.trick.engine.Card;
import com.mjaseem.trick.engine.Player;
import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.engine.Trick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStateEncoder {
    private static final int MAX_HAND_SIZE = Records.MAX_CARDS; // Max number of cards per player

    public static double[] encode(Records.GameState gameState) {
        List<Double> stateVector = new ArrayList<>();

        // Encode player states
        List<Records.PlayerState> players = gameState.players();
        for (Records.PlayerState playerState : players) {
            stateVector.addAll(encodePlayerState(playerState));
        }

        // Encode game history (flatten tricks)
        for (Trick trick : gameState.gameHistory().getTricks()) {
            stateVector.addAll(encodeTrick(trick, players.size()));
        }
        for (int i = gameState.gameHistory().getTricks().size(); i < MAX_HAND_SIZE; i++) {
            stateVector.addAll(encodeTrick(new Trick(), players.size()));
        }

        // Convert List<Double> to array
        return stateVector.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private static List<Double> encodePlayerState(Records.PlayerState playerState) {
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

    private static List<Double> encodeCard(Card card) {
        List<Double> cardVector = new ArrayList<>();
        if (card == null) {
            return List.of(0.0, 0.0, 0.0, 0.0, 0.0);
        }
        // Encode suit as one-hot (4 elements)
        switch (card.suit()) {
            case HEARTS -> cardVector.addAll(List.of(1.0, 0.0, 0.0, 0.0));
            case CLUBS -> cardVector.addAll(List.of(0.0, 1.0, 0.0, 0.0));
            case DIAMONDS -> cardVector.addAll(List.of(0.0, 0.0, 1.0, 0.0));
            case SPADES -> cardVector.addAll(List.of(0.0, 0.0, 0.0, 1.0));
        }

        // Normalize rank between 0 and 1 (assuming rank 1-Records.MAX_CARDS)
        cardVector.add(card.rank() / (double) Records.MAX_CARDS);

        return cardVector;
    }

    private static List<Double> encodeTrick(Trick trick, int playerCount) {
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
