package com.mjaseem.trick.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameHistory {
    private final List<Trick> tricks = new ArrayList<>();

    public void addTrick(Trick trick) {
        tricks.add(trick);
    }

    public List<Trick> getTricks() {
        return Collections.unmodifiableList(tricks);
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
