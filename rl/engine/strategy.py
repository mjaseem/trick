from typing import List, TYPE_CHECKING

if TYPE_CHECKING:
    from engine import Card, Trick, Suit


class Strategy:
    def choose_card(
            self, hand: List['Card'], game_history: List['Trick'], trick: 'Trick', trump_suit: 'Suit'
    ) -> int:
        raise NotImplementedError

