package com.mjaseem.trick.strategy;

import com.mjaseem.trick.engine.Card;
import com.mjaseem.trick.engine.GameHistory;
import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.engine.Trick;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

// High-card strategy implementation
public class HighCardStrategy implements Strategy {

    @Override
    public int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit) {
        return IntStream.range(0, hand.size()).boxed()
                .filter(i -> trick.plays().isEmpty() || hand.get(i).suit().equals(trick.plays().getFirst().getValue().suit()))
                .max(Comparator.comparing(i -> hand.get(i).rank()))
                .or(() -> IntStream.range(0, hand.size()).boxed().min(Comparator.comparing(i -> hand.get(i).rank())))
                .orElseThrow();

    }

}
