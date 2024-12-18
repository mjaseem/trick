package com.mjaseem.trick.strategy;

import com.mjaseem.trick.engine.Card;
import com.mjaseem.trick.engine.GameHistory;
import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.engine.Trick;

import java.util.List;

// Strategy interface
public interface Strategy {

    int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit);
}
