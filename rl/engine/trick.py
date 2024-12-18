from typing import List, Tuple, Optional

from .card import Card
from .player import Player


class Trick:
    def __init__(self) -> None:
        self.plays: List[Tuple[Player, Card]] = []
        self.winner: Optional[str] = None

    def add_play(self, player: Player, card: Card) -> None:
        self.plays.append((player, card))

    def __str__(self) -> str:
        return ", ".join(
            f"{player.name}: {card}{'!' if player.name == self.winner else ''}"
            for player, card in self.plays
        )
