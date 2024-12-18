package com.mjaseem.trick.engine;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

// Main game engine class
public class GameEngine {
    private final List<Player> players;
    private final Deck deck = new Deck();
    private int trumpSuit;
    private int currentPlayerIndex;
    private final GameHistory gameHistory = new GameHistory();
    private int turnCount = 0;
    private static final Logger log = LoggerFactory.getLogger(GameEngine.class);

    public GameEngine(List<Records.PlayerConfig> playerConfigs) {
        players = playerConfigs.stream().map(Player::new).toList();
        if (players.stream().map(Player::getName).distinct().count() != players.size()) {
            throw new IllegalArgumentException("Players have non distinct names");
        }
        reset();
    }

    private void reset() {
        deck.reset();
        deck.shuffle();
        dealCards();
        trumpSuit = new Random().nextInt(4);
        log.debug("Trump suit: {}", Records.Suit.values()[trumpSuit]);
        currentPlayerIndex = new Random().nextInt(players.size());
    }


    private void dealCards() {
        List<List<Card>> hands = deck.deal(players.size(), Records.MAX_CARDS);
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setHand(hands.get(i));
        }
    }

    public void run() throws BadMoveException {
        Preconditions.checkArgument(turnCount < Records.MAX_CARDS);

        if (turnCount != 0 && players.get(currentPlayerIndex).shouldWait()) {
            throw new IllegalStateException("Game run without responding to prompt");
        }
        if (players.get(currentPlayerIndex).shouldWait()) {
            return;
        }
        while (turnCount < Records.MAX_CARDS) {
            log.debug(players.stream().map(Player::toString).collect(Collectors.joining("; ")));
            Trick currentTrick;
            List<Trick> tricks = gameHistory.getTricks();
            if (tricks.isEmpty() || tricks.getLast().plays().size() == players.size()) {
                // Game was halted. Continuing with the last trick
                currentTrick = new Trick();
                gameHistory.addTrick(currentTrick);
            } else {
                currentTrick = tricks.getLast();
            }

            while (currentTrick.plays().size() < players.size()) {
                Player currentPlayer = players.get(currentPlayerIndex);;
                if (currentPlayer.shouldWait()) {
                    log.debug("halting operation to get external input. Current trick: {}", currentTrick);
                    return;
                }
                Card playedCard = currentPlayer.playCard(currentTrick, gameHistory, Records.Suit.values()[trumpSuit]);
                currentTrick.addPlay(currentPlayer, playedCard);
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }

            Player winner = determineWinner(currentTrick, Records.Suit.values()[trumpSuit]);
            log.debug(currentTrick.toString());
            winner.countWin();
            currentPlayerIndex = players.indexOf(winner); // Update leading player for the next trick
            turnCount++;
        }
    }

    public Records.GameState getGameState() {
        List<Records.PlayerState> playerStates = players.stream().map(Player::playerState).toList();
        return new Records.GameState(playerStates, turnCount, gameHistory);
    }

    private Player determineWinner(Trick trick, Records.Suit trumpSuit) {
        Records.Suit leadSuit = trick.plays().getFirst().getValue().suit();
        Card bestCard = null;
        Player player = null;

        for (Map.Entry<Player, Card> entry : trick.plays()) {
            Card card = entry.getValue();
            if (bestCard == null || card.beats(bestCard, leadSuit, trumpSuit)) {
                bestCard = card;
                player = entry.getKey();
            }
        }

        trick.setWinner(Objects.requireNonNull(player));
        return player;
    }


    public Map<String, Integer> getScores() {
        return players.stream().collect(Collectors.toMap(Player::getName, Player::getScore));
    }


    public static class BadMoveException extends Exception {
        String player;
        String fault;

        BadMoveException(String player, String fault) {
            super(String.format("%s made an illegal move: %s", player, fault));
            this.player = player;
            this.fault = fault;
        }
    }
}
