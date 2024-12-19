from typing import List

from engine import Strategy, Card, Trick
from engine.card import Suit


class HighCardStrategy(Strategy):
    def choose_card(
            self, hand: List[Card], game_history: List['Trick'], trick: Trick, trump_suit: Suit
    ) -> int:
        """
        Select the highest-ranked card of the leading suit if possible; otherwise, play the lowest-ranked card.
        """
        # Check if there is a leading suit in the current trick
        leading_suit = trick.plays[0][1].suit if trick.plays else None

        # Try to find the highest-ranked card in the leading suit
        high_card_index = (
            max(
                (i for i in range(len(hand)) if not leading_suit or hand[i].suit == leading_suit),
                key=lambda i: hand[i].rank,
                default=None,
            )
        )

        # If no card of the leading suit is found, play the lowest-ranked card
        if high_card_index is not None:
            return high_card_index

        # Fallback to the lowest-ranked card in the hand
        return min(range(len(hand)), key=lambda i: hand[i].rank)
