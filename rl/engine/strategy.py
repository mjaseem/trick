from typing import List, TYPE_CHECKING

if TYPE_CHECKING:
    from engine import Card, GameHistory, Trick, Suit


class Strategy:
    def choose_card(
            self, hand: List['Card'], game_history: 'GameHistory', trick: 'Trick', trump_suit: 'Suit'
    ) -> int:
        raise NotImplementedError

