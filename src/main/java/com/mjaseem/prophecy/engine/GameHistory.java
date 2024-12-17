package com.mjaseem.prophecy.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GameHistory {
    private final List<Trick> tricks = new ArrayList<>();

    public void addTrick(Trick trick) {
        tricks.add(trick);
    }

    public List<Trick> getTricks() {
        return Collections.unmodifiableList(tricks);
    }

    public List<Card> getPlayedCards() {
        return tricks.stream().map(Trick::getPlays)
                .flatMap(e -> e.stream().map(Map.Entry::getValue))
                .toList();
    }

    public void reset() {
        tricks.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int trickNumber = 1;
        for (Trick trick : tricks) {
            sb.append("Trick ").append(trickNumber++).append(": ");
            sb.append(trick).append("\n");
        }
        return sb.toString();
    }

}
