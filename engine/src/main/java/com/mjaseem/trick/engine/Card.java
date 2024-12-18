package com.mjaseem.trick.engine;

// Card class
public record Card(Records.Suit suit, int rank) {

    public boolean beats(Card other, Records.Suit leadSuit, Records.Suit trumpSuit) {
        if (this.suit == trumpSuit && other.suit() != trumpSuit) {
            return true;
        }
        if (this.suit != trumpSuit && other.suit == trumpSuit) {
            return false;
        }
        if (this.suit == other.suit()) {
            return this.rank > other.rank();
        }
        if (other.suit() == leadSuit) {
            return false;
        }
        return this.suit == leadSuit;
    }

    @Override
    public String toString() {
        String suitSymbol = switch (suit) {
            case HEARTS -> "♥";
            case CLUBS -> "♣";
            case DIAMONDS -> "♦";
            case SPADES -> "♠";
        };
        return rank + suitSymbol;
    }
}
