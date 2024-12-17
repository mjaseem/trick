package com.mjaseem.prophecy.engine;

import com.mjaseem.prophecy.Config;

import java.util.*;

// Main game engine class
public class GameEngine {
    private final List<Player> players = new ArrayList<>();
    private final Map<String, Integer> scores = new HashMap<>();
    private final Deck deck = new Deck();
    private int currentTrump;
    private Player leadingPlayer;
    private final GameHistory gameHistory = new GameHistory();
    private int turnCount = 0;

    public GameEngine(List<Records.PlayerConfig> playerConfigs) {
        playerConfigs.stream().map(Player::new).forEach(players::add);
        reset();
    }

    private void reset() {
        deck.reset();
        deck.shuffle();
        dealCards();
        currentTrump = new Random().nextInt(4);
        if (Config.DEBUG)
            System.out.println("Trump getSuit: " + com.mjaseem.prophecy.engine.Records.Suit.values()[currentTrump]);
        leadingPlayer = players.get(new Random().nextInt(players.size()));
    }


    private void dealCards() {
        List<List<Card>> hands = deck.deal(players.size(), 13);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setHand(hands.get(i));
        }
    }

    public void playTrick() {
        if (Config.DEBUG) {
            players.forEach(System.out::print);
            System.out.println();
        }
        Trick trick = new Trick();
        Records.Suit leadSuit = null;
        int startingIndex = players.indexOf(leadingPlayer);

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get((startingIndex + i) % players.size());
            Card playedCard = currentPlayer.playCard(trick, gameHistory, com.mjaseem.prophecy.engine.Records.Suit.values()[currentTrump]);
            trick.addPlay(currentPlayer, playedCard);

            if (leadSuit == null) {
                leadSuit = playedCard.getSuit();
            }
        }

        Player winner = determineWinner(trick, com.mjaseem.prophecy.engine.Records.Suit.values()[currentTrump]);
        if (Config.DEBUG) System.out.println(trick);
        scores.merge(winner.getName(), 1, Integer::sum);
        leadingPlayer = winner; // Update leading player for the next trick
        gameHistory.addTrick(trick);
        turnCount++;
    }

    public GameState getGameState() {
        return new GameState(players, turnCount, gameHistory, scores);
    }

    private Player determineWinner(Trick trick, Records.Suit trumpSuit) {
        Records.Suit leadSuit = trick.getPlays().getFirst().getValue().getSuit();
        Card bestCard = null;
        Player player = null;

        for (Map.Entry<Player, Card> entry : trick.getPlays()) {
            Card card = entry.getValue();
            if (bestCard == null || card.beats(bestCard, leadSuit, trumpSuit)) {
                bestCard = card;
                player = entry.getKey();
            }
        }

        trick.setWinner(player);
        return player;
    }


    public Map<String, Integer> getScores() {
        return scores;
    }

    public void displayScores() {
        if (Config.DEBUG) System.out.println("Final Scores:");
        scores.forEach((name, score) -> {
            if (Config.DEBUG) System.out.println(name + ": " + score);
        });
    }
}
