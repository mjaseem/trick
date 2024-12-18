package com.mjaseem.trick.strategy;

import com.mjaseem.trick.engine.*;

import java.util.*;

public class TwoPlayerStrategy implements Strategy {

    private final int version;

    public TwoPlayerStrategy(int version) {
        this.version = version;
    }

    @Override
    public int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit) {
        // Determine if we are the first player (no previous plays)
        boolean isFirstPlayer = trick.plays().isEmpty();

        if (isFirstPlayer) {
            if (version == 4) {
                return selectMidRangeNonTrumpCard(history, hand, trumpSuit).orElseGet(() -> playLowestCard(hand));
            } else if (version == 3) {
                return playHighestNonTrumpCard(hand, trumpSuit).orElseGet(() -> playLowestCard(hand));
            } else if (version == 2 && hand.size() < 8) {
                return playHighestNonTrumpCard(hand, trumpSuit).orElseGet(() -> playLowestCard(hand));
            } else {
                return playLowestNonTrumpCard(hand, trumpSuit).orElseGet(() -> playLowestCard(hand));
            }
        } else {
            // If it's not the first player, handle based on the leading card
            Card leadingCard = trick.plays().getFirst().getValue();

            // Play the closest higher card in the leading suit, or the lowest if none higher
            return playClosestHigherCard(hand, leadingCard)
                    .or(() -> playLowestCard(hand, leadingCard))
                    .or(() -> playLowestTrumpCard(hand, trumpSuit))
                    .orElseGet(() -> playLowestCard(hand));
        }
    }

    private Optional<Integer> selectMidRangeNonTrumpCard(GameHistory history, List<Card> hand, Records.Suit trumpSuit) {

        Records.Suit weakSuit = findWeakSuitFromHistory(history, trumpSuit);
        List<Card> weakSuitCards = filterCardsBySuit(hand, weakSuit);

        if (!weakSuitCards.isEmpty()) {
            // Play a high card from opponent's weak suit
            return Optional.of(hand.indexOf(Collections.max(weakSuitCards, Comparator.comparingInt(Card::rank))));
        }

        // Sort cards by rank
        List<Card> cards = filterCardsExcludingSuits(hand, Collections.singletonList(trumpSuit));
        if (cards.isEmpty()) return Optional.empty();
        Card card = cards.stream()
                .min(Comparator.comparingInt(c -> Math.abs(c.rank() - 7)))
                .orElseThrow(() -> new IllegalStateException("Hand should never be empty."));
        // Return the middle card
        return Optional.of(hand.indexOf(card));
    }

    private List<Card> filterCardsBySuit(List<Card> hand, Records.Suit suit) {
        if (suit == null) return Collections.emptyList();
        List<Card> filtered = new ArrayList<>();
        for (Card card : hand) {
            if (card.suit() == suit) {
                filtered.add(card);
            }
        }
        return filtered;
    }

    private List<Card> filterCardsExcludingSuits(List<Card> hand, List<Records.Suit> suits) {
        List<Card> filtered = new ArrayList<>();
        for (Card card : hand) {
            if (!suits.contains(card.suit())) {
                filtered.add(card);
            }
        }
        return filtered;
    }

    private Optional<Integer> playHighestNonTrumpCard(List<Card> hand, Records.Suit trumpSuit) {
        return hand.stream()
                .filter(card -> card.suit() != trumpSuit)
                .max(Comparator.comparingInt(Card::rank))
                .map(hand::indexOf);
    }

    private Optional<Integer> playLowestNonTrumpCard(List<Card> hand, Records.Suit trumpSuit) {
        return hand.stream()
                .filter(card -> card.suit() != trumpSuit)
                .min(Comparator.comparingInt(Card::rank))
                .map(hand::indexOf);
    }

    private Optional<Integer> playClosestHigherCard(List<Card> hand, Card leadingCard) {
        return hand.stream()
                .filter(card -> card.suit() == leadingCard.suit())
                .filter(card -> card.rank() > leadingCard.rank())
                .min(Comparator.comparingInt(Card::rank))
                .map(hand::indexOf);
    }

    private Optional<Integer> playLowestCard(List<Card> hand, Card leadingCard) {
        return hand.stream()
                .filter(card -> card.suit() == leadingCard.suit())
                .min(Comparator.comparingInt(Card::rank))
                .map(hand::indexOf);
    }


    private Optional<Integer> playLowestTrumpCard(List<Card> hand, Records.Suit trumpSuit) {
        return hand.stream()
                .filter(card -> card.suit() == trumpSuit)
                .min(Comparator.comparingInt(Card::rank))
                .map(hand::indexOf);

    }

    private Records.Suit findWeakSuitFromHistory(GameHistory history, Records.Suit trumpSuit) {
        // Analyze history to find suits opponent is weak in
        Map<Records.Suit, Integer> opponentSuitCounts = countOpponentSuitPlays(history);
        return opponentSuitCounts.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .filter(x -> !x.equals(trumpSuit))
                .orElse(null);
    }

    private Map<Records.Suit, Integer> countOpponentSuitPlays(GameHistory history) {
        Map<Records.Suit, Integer> suitCounts = new HashMap<>();
        for (Trick trick : history.getTricks()) {
            for (Map.Entry<Player, Card> play : trick.plays()) {
                Records.Suit suit = play.getValue().suit();
                suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
            }
        }
        return suitCounts;
    }

    private int playLowestCard(List<Card> hand) {
        return hand.stream()
                .min(Comparator.comparingInt(Card::rank))
                .map(hand::indexOf)
                .orElseThrow(() -> new IllegalStateException("No card to play"));
    }
}
