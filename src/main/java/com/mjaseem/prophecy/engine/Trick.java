package com.mjaseem.prophecy.engine;

import java.util.*;

// Inner class to represent a single trick
public final class Trick {
    private final List<Map.Entry<Player, Card>> plays;
    private Player winner;

    public Trick() {
        this.plays = new ArrayList<>();
    }

    public void addPlay(Player player, Card card) {
        plays.add(new AbstractMap.SimpleEntry<>(player, card));
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public List<Map.Entry<Player, Card>> getPlays() {
        return Collections.unmodifiableList(plays);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Player, Card> entry : plays) {
            sb.append(entry.getKey().getName()).append(": ")
                    .append(entry.getValue())
                    .append(entry.getKey().equals(winner) ? "!" : " ")
                    .append("\n");
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Trick) obj;
        return Objects.equals(this.plays, that.plays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plays);
    }

}
