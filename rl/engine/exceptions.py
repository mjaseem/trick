class IllegalMoveException(Exception):
    """Custom exception to indicate an invalid move made by a strategy."""

    def __init__(self, player: str, message: str) -> None:
        super().__init__(player + " " + message)
        self.player = player
