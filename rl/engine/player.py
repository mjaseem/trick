from typing import List, TYPE_CHECKING

from .exceptions import IllegalMoveException
from .card import Card, Suit
if TYPE_CHECKING:
    from .history import GameHistory
    from .strategy import Strategy
    from .trick import Trick

class Player:
    def __init__(self, name: str, strategy: 'Strategy') -> None:
        self.name = name
        self.strategy = strategy
        self.hand: List[Card] = []
        self.score = 0

    def set_hand(self, hand: List['Card']) -> None:
        self.hand = hand

    def play_card(self, trick: 'Trick', history:'GameHistory' , trump_suit: 'Suit') -> Card:
        if not self.hand:
            raise ValueError(f"Hand is empty. Can't play!")  # Equivalent to IllegalStateException in Java

        # Get the index of the card to play
        card_index = self.strategy.choose_card(self.hand, history, trick, trump_suit)

        # Check if the card index is valid
        if card_index < 0 or card_index >= len(self.hand):
            raise IllegalMoveException(self.name, "Chosen index out of bounds")

        card = self.hand[card_index]
        lead_suit = trick.plays[0][1].suit if trick.plays else None  # First play's suit or None

        # Check if the player follows the lead suit, if applicable
        if lead_suit and card.suit != lead_suit and any(c.suit == lead_suit for c in self.hand):
            raise IllegalMoveException(self.name, f"Did not follow the lead suit. Lead card : {trick.plays[0][1]} played card : {card}")

        # Remove the card from the player's hand and return it
        self.hand.remove(card)
        return card

    def count_win(self) -> None:
        self.score += 1

    def should_wait(self):
        # Check if the strategy has a 'should_wait' method and call it if it exists
        return getattr(self.strategy, 'should_wait', lambda: False)()

    def __str__(self) -> str:
        return f"{self.name} (Score: {self.score}, Hand: {[str(card) for card in self.hand]})"

