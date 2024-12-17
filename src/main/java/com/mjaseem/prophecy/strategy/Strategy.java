package com.mjaseem.prophecy.strategy;

import com.mjaseem.prophecy.engine.Card;
import com.mjaseem.prophecy.engine.GameHistory;
import com.mjaseem.prophecy.engine.Records;
import com.mjaseem.prophecy.engine.Trick;

import java.util.List;

// Strategy interface
public interface Strategy {

    int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit);
}
