from enum import Enum


class Suit(Enum):
    HEARTS = "♥"
    CLUBS = "♣"
    DIAMONDS = "♦"
    SPADES = "♠"


class Card:
    def __init__(self, suit: Suit, rank: int) -> None:
        self.suit = suit
        self.rank = rank

    def beats(self, other: "Card", lead_suit: Suit, trump_suit: Suit) -> bool:
        if self.suit == trump_suit and other.suit != trump_suit:
            return True
        if self.suit != trump_suit and other.suit == trump_suit:
            return False
        if self.suit == other.suit:
            return self.rank > other.rank
        if other.suit == lead_suit:
            return False
        return self.suit == lead_suit

    def __str__(self) -> str:
        return f"{self.rank}{self.suit.value}"

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Card):
            return False
        return self.suit == other.suit and self.rank == other.rank