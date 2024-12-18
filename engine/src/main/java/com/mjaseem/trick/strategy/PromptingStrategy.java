package com.mjaseem.trick.strategy;

import com.mjaseem.trick.engine.Card;
import com.mjaseem.trick.engine.GameHistory;
import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.engine.Trick;

import java.util.List;

public class PromptingStrategy implements Strategy {
    int nextPlay = Records.MAX_CARDS;

    @Override
    public int chooseCard(List<Card> hand, GameHistory history, Trick trick, Records.Suit trumpSuit) {
        int tmp = this.nextPlay;
        this.nextPlay = Records.MAX_CARDS;
        return tmp;
    }

    public boolean shouldWait() {
        return nextPlay == Records.MAX_CARDS;
    }

    public void setNextPlay(int nextPlay) {
        this.nextPlay = nextPlay;
    }
}
