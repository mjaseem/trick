package com.mjaseem.prophecy.strategy;

import com.mjaseem.prophecy.engine.Card;
import com.mjaseem.prophecy.engine.GameHistory;
import com.mjaseem.prophecy.engine.Records;
import com.mjaseem.prophecy.engine.Trick;

import java.util.List;

public class InputPlayerStrategy implements Strategy {
    int nextPlay = 14;

    @Override
    public int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit) {
        return nextPlay;
    }

    public void setNextPlay(int nextPlay) {
        this.nextPlay = nextPlay;
    }
}
