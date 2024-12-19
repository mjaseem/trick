from dataclasses import dataclass
from typing import List, Tuple, Optional

from .card import Card, Suit


@dataclass(frozen=True)
class PlayerState:
    name: str
    hand: Tuple[Card, ...]  # Assuming you have a Card class defined elsewhere
    score: int

    def __str__(self) -> str:
        return f"{self.name} : (Score: {self.score}, Hand: {[str(card) for card in self.hand]})"


@dataclass(frozen=True)
class ImmutableTrick:
    plays: Tuple[Tuple[str, Card], ...] = ()
    winner: Optional[str] = None

    def __str__(self) -> str:
        return ", ".join(
            f"{player}: {str(card)}{'!' if player == self.winner else ''}"
            for player, card in self.plays
        )


@dataclass(frozen=True)
class GameState:
    players: Tuple[PlayerState, ...]
    turn_count: int
    tricks: Tuple[ImmutableTrick, ...]
    trump: Suit

    def __str__(self) -> str:
        players_str = "\n".join([str(player) for player in self.players])
        tricks_str = "\n".join([f"{index} : {str(trick)}" for  index, trick in enumerate(self.tricks)])
        return f"turn count : {self.turn_count} \n{players_str} \n{tricks_str} \nTrump: {self.trump.value}"
