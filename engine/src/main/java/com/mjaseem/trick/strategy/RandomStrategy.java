package com.mjaseem.trick.strategy;

import com.mjaseem.trick.engine.*;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

// Random strategy implementation
public class RandomStrategy implements Strategy {
    @Override
    public int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit) {
        List<Map.Entry<Player, Card>> plays = trick.plays();
        OptionalInt any = IntStream.range(0, hand.size()).filter(i ->
                plays.isEmpty() || hand.get(i).suit().equals(plays.getFirst().getValue().suit())).findAny();
        return any.orElse(new Random().nextInt(hand.size()));
    }
}
