from typing import List

from .trick import Trick


class GameHistory:
    def __init__(self) -> None:
        self.tricks: List["Trick"] = []

    def add_trick(self, trick: "Trick") -> None:
        self.tricks.append(trick)

    def get_tricks(self) -> List["Trick"]:
        return self.tricks[:]

    def __str__(self) -> str:
        return "\n".join(
            f"Trick {i + 1}: {trick}" for i, trick in enumerate(self.tricks)
        )

    def reset(self):
        self.tricks = []
