from dataclasses import dataclass
from typing import List

from .card import Card, Suit
from .history import GameHistory


@dataclass
class PlayerState:
    name: str
    hand: List[Card]  # Assuming you have a Card class defined elsewhere
    score: int
    def __str__(self) -> str:
        return f"{self.name} (Score: {self.score}, Hand: {[str(card) for card in self.hand]})"


@dataclass
class GameState:
    players: List[PlayerState]
    turn_count: int
    game_history: GameHistory  # Assuming you have a GameHistory class defined elsewhere
    trump: Suit
    def __str__(self) -> str:
        return f"{self.turn_count} : {str(self.players)})"