import random
from typing import List

from . import constants
from .card import Card, Suit


class Deck:
    def __init__(self) -> None:
        self.cards: List[Card] = [
            Card(suit, rank) for suit in Suit for rank in range(1, constants.MAX_CARD_RANK + 1)
        ]

    def shuffle(self) -> None:
        random.shuffle(self.cards)

    def deal(self, num_players: int, cards_per_player: int) -> List[List[Card]]:
        return [
            self.cards[i * cards_per_player: (i + 1) * cards_per_player]
            for i in range(num_players)
        ]

    def reset(self) -> None:
        self.__init__()
