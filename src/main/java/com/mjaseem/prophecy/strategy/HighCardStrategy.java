package com.mjaseem.prophecy.strategy;

import com.mjaseem.prophecy.engine.Card;
import com.mjaseem.prophecy.engine.GameHistory;
import com.mjaseem.prophecy.engine.Records;
import com.mjaseem.prophecy.engine.Trick;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

// High-card strategy implementation
public class HighCardStrategy implements Strategy {

    @Override
    public int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit) {
        return IntStream.range(0, hand.size()).boxed()
                .filter(i -> trick.getPlays().isEmpty() || hand.get(i).getSuit().equals(trick.getPlays().getFirst().getValue().getSuit()))
                .max(Comparator.comparing(i -> hand.get(i).getRank()))
                .or(() -> IntStream.range(0, hand.size()).boxed().min(Comparator.comparing(i -> hand.get(i).getRank())))
                .orElseThrow();

    }

}
