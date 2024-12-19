from typing import List, Tuple, Optional

from .card import Card


class Trick:
    def __init__(self) -> None:
        self.plays: List[Tuple[str, Card]] = []
        self.winner: Optional[str] = None

    def add_play(self, player: str, card: Card) -> None:
        self.plays.append((player, card))

    def __str__(self) -> str:
        return ", ".join(
            f"{player}: {card}{'!' if player == self.winner else ''}"
            for player, card in self.plays
        )
