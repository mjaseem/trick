package com.mjaseem.prophecy.engine;

// Card class
public record Card(Records.Suit getSuit, int getRank) {

    public boolean beats(Card other, Records.Suit leadSuit, Records.Suit trumpSuit) {
        if (this.getSuit == trumpSuit && other.getSuit() != trumpSuit) {
            return true;
        }
        if (this.getSuit != trumpSuit && other.getSuit == trumpSuit) {
            return false;
        }
        if (this.getSuit == other.getSuit()) {
            return this.getRank > other.getRank();
        }
        if (other.getSuit() == leadSuit) {
            return false;
        }
        return this.getSuit == leadSuit;
    }

    @Override
    public String toString() {
        String suitSymbol = switch (getSuit) {
            case HEARTS -> "♥";
            case CLUBS -> "♣";
            case DIAMONDS -> "♦";
            case SPADES -> "♠";
        };
        return getRank + suitSymbol;
    }
}
