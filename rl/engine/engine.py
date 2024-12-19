import random
from typing import Dict, List, Optional

import config

from .constants import MAX_CARDS_IN_HAND
from .state import GameState, PlayerState
from .strategy import Strategy
from .player import Player
from .history import GameHistory
from .deck import Deck
from .card import Suit
from .trick import Trick


class GameEngine:

    def __init__(self, player_configs: Dict[str, Strategy]) -> None:
        self.players: List[Player] = [
            Player(name, strategy) for name, strategy in player_configs.items()
        ]
        self.deck = Deck()
        self.trump_suit: Optional[Suit] = None
        self.current_player_index = 0
        self.game_history = GameHistory()
        self.turn_count = 0
        self.reset()

    def reset(self) -> None:
        self.deck.reset()
        self.deck.shuffle()
        self.deal_cards()
        for player in self.players:
            player.reset()
        self.turn_count = 0
        self.game_history.reset()
        self.trump_suit = random.choice(list(Suit))
        self.current_player_index = random.randint(0, len(self.players) - 1)
        if config.DEBUG:
            print(f"Trump suit: {self.trump_suit}")

    def deal_cards(self) -> None:
        hands = self.deck.deal(len(self.players), MAX_CARDS_IN_HAND)
        for player, hand in zip(self.players, hands):
            player.set_hand(hand)

    def run(self):
        if self.turn_count >= MAX_CARDS_IN_HAND:
            raise Exception("Game over. No more turns.")

        current_player = self.players[self.current_player_index]
        should_wait = getattr(current_player.strategy, 'should_wait', lambda: False)()
        if should_wait:
            if self.turn_count != 0:
                raise Exception(f"Player {current_player.name} is waiting.")
            return

        while self.turn_count < MAX_CARDS_IN_HAND:
            if config.DEBUG:
                print("; ".join(str(player) for player in self.players))

            starting_a_trick = (self.game_history.get_tricks() and
                                len(self.game_history.get_tricks()[-1].plays) < len(self.players))
            current_trick = self.game_history.get_tricks()[-1] if starting_a_trick else Trick()
            self.game_history.add_trick(current_trick)

            while len(current_trick.plays) < len(self.players):
                current_player = self.players[self.current_player_index]

                if current_player.should_wait():
                    if config.DEBUG:
                        print(f"Waiting on {current_player.name}'s input...")
                    return  # Return to allow the player to provide input or wait

                # Otherwise, let the player play a card
                played_card = current_player.play_card(current_trick, self.game_history, self.trump_suit)
                current_trick.add_play(current_player.name, played_card)
                self.current_player_index = (self.current_player_index + 1) % len(self.players)

            # Determine winner of the current trick
            winner_name = self.determine_winner(current_trick)
            current_trick.winner = winner_name
            winner = next(player for player in self.players if player.name == winner_name)
            winner.count_win()
            self.turn_count += 1
            self.current_player_index = self.players.index(winner)  # Update leading player
            if config.DEBUG:
                print(f"Trick {self.turn_count}: {current_trick}")

        # After all tricks, determine the overall winner
        final_winner = max(self.players, key=lambda player: player.score)
        if config.DEBUG:
            print(f"Game over. Winner: {final_winner.name}")

    def determine_winner(self, trick: Trick) -> str:
        lead_suit = trick.plays[0][1].suit
        best_card = None
        winner = None

        for player, card in trick.plays:
            if best_card is None or card.beats(best_card, lead_suit, self.trump_suit):
                best_card = card
                winner = player

        if winner is None:
            raise ValueError("No winner found for trick")
        return winner

    def get_game_state(self) -> GameState:
        """Returns the current game state."""
        player_states = [
            PlayerState(player.name, player.hand, player.score)  # Assuming 'hand' and 'score' are attributes of Player
            for player in self.players
        ]
        return GameState(players=player_states, turn_count=self.turn_count, game_history=self.game_history,
                         trump=self.trump_suit)

    def get_scores(self) -> Dict[str, int]:
        return {player.name: player.score for player in self.players}
