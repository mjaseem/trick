from typing import List, Optional

from engine import Strategy
from engine.card import Card, Suit
from engine.history import GameHistory
from engine.trick import Trick


class TwoPlayerStrategy(Strategy):
    def choose_card(
            self, hand: List[Card], game_history: GameHistory, trick: Trick, trump_suit: Suit
    ) -> int:
        is_first_player = len(trick.plays) == 0

        if is_first_player:
            return hand.index(self.play_highest_non_trump_card(hand, trump_suit))

        leading_card = trick.plays[0][1]
        card = (self.play_closest_higher_card(hand, leading_card)
                or self.play_lowest_card(hand, leading_card)
                or self.play_lowest_trump_card(hand, trump_suit)
                or self.play_lowest_card(hand))
        return hand.index(card)

    def play_highest_non_trump_card(self, hand: List[Card], trump_suit: Suit) -> Card:
        non_trump_cards = [card for card in hand if card.suit != trump_suit]
        if non_trump_cards:
            return max(non_trump_cards, key=lambda c: c.rank)
        return self.play_lowest_card(hand)

    def play_lowest_non_trump_card(self, hand: List[Card], trump_suit: Suit) -> Card:
        non_trump_cards = [card for card in hand if card.suit != trump_suit]
        if non_trump_cards:
            return min(non_trump_cards, key=lambda c: c.rank)
        return self.play_lowest_card(hand)

    def play_closest_higher_card(  self, hand: List[Card], leading_card: Card ) -> Optional[Card]:
        candidates = [card for card in hand if card.suit == leading_card.suit and card.rank > leading_card.rank]
        if candidates:
            return min(candidates, key=lambda c: c.rank)
        return None

    def play_lowest_card(self, hand: List[Card], leading_card: Optional[Card] = None) -> Card:
        if leading_card:
            same_suit_cards = [card for card in hand if card.suit == leading_card.suit]
            if same_suit_cards:
                return min(same_suit_cards, key=lambda c: c.rank)
        return min(hand, key=lambda c: c.rank)

    def play_lowest_trump_card(self, hand: List[Card], trump_suit: Suit) -> Optional[Card]:
        trump_cards = [card for card in hand if card.suit == trump_suit]
        if trump_cards:
            return min(trump_cards, key=lambda c: c.rank)
        return None
