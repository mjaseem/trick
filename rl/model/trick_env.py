import random

import gymnasium as gym
import numpy as np

import config
from engine import GameEngine, IllegalMoveException, constants, Card, Trick, GameState, ImmutableTrick
from model import encoder
from strategy import TwoPlayerStrategy
from strategy.prompting_strategy import PromptingStrategy

PLAYER = "AI"


class TrickEnv(gym.Env):
    def __init__(self):
        super(TrickEnv, self).__init__()
        self.faked_state = None
        self.state_vector = None
        self.prompting_strategy = PromptingStrategy()
        players = {
            "AI": self.prompting_strategy,
            "Engine": TwoPlayerStrategy()
        }
        self.game_engine = GameEngine(players)
        self.action_space = gym.spaces.Discrete(constants.MAX_CARDS_IN_HAND)  # 13 possible actions (cards to play)
        self.vector_size = encoder.VECTOR_SIZE
        self.observation_space = gym.spaces.Box(low=0, high=1, shape=(self.vector_size,), dtype=np.float32)

    def reset(self, seed=None, options=None):
        # print("Resetting the environment")
        self.game_engine.reset()
        self.game_engine.run()  # Start run to get the first player's hand
        state = self.game_engine.get_game_state()
        self.state_vector = encoder.encode(game_state=state, player="AI")
        return np.array(self.state_vector, dtype=np.float32), state.__dict__

    def step(self, action: int):
        if self.faked_state:
            return self._handle_response_to_fake_state(action)

        valid_move = True
        try:
            decoded_action = encoder.decode_and_validate(state_vector=self.state_vector,
                                                         game_state=self.game_engine.get_game_state(),
                                                         action=action, player="AI")
            self.prompting_strategy.set_next_play(decoded_action)
            try:
                self.game_engine.run()
            except IllegalMoveException as e:
                print(self.game_engine.get_game_state())
                print(self.state_vector)
                raise Exception("Illegal move despite validation. Giving up!", e)
        except IllegalMoveException as e:
            valid_move = False
            if config.DEBUG:
                print("Illegal move. Shall be punished. ", e)


        state = self.game_engine.get_game_state()
        done = state.turn_count >= constants.MAX_CARDS_IN_HAND

        # fake states with leading hand sporadically
        if not done and random.randint(0, 1):
            self.faked_state = self._fake_game_state(state)
            state = self.faked_state
            if config.DEBUG: print("Faked state: ", state)

        self.state_vector = encoder.encode(game_state=state, player="AI")
        return np.array(self.state_vector, dtype=np.float32), 10 if valid_move else -100, done, False, state.__dict__

    def _handle_response_to_fake_state(self, action: int):
        valid_move = True
        try:
            game_state = self.faked_state
            self.faked_state = None
            encoder.decode_and_validate(state_vector=self.state_vector,
                                        game_state=game_state,
                                        action=action, player=PLAYER)
        except IllegalMoveException as e:
            valid_move = False
            if config.DEBUG:
                print("Illegal move. Shall be punished. ", e)

        state = self.game_engine.get_game_state()
        done = state.turn_count >= constants.MAX_CARDS_IN_HAND
        self.state_vector = encoder.encode(game_state=state, player=PLAYER)
        return np.array(self.state_vector, dtype=np.float32), 10 if valid_move else -100, done, False, state.__dict__

    def _fake_game_state(self, state: GameState) -> GameState:
        player_state = next((p for p in state.players if p.name == PLAYER), None)
        random_card_from_hand = random.choice(player_state.hand)
        lead_card_for_training = Card(random_card_from_hand.suit,
                                      random.choice([i for i in range(1, 15) if i != random_card_from_hand.rank]))
        trick = ImmutableTrick(plays=(("Engine", lead_card_for_training),))
        tricks_as_list = list(state.tricks)
        if not state.tricks or len(state.tricks[-1].plays) == len(state.players):
            tricks_as_list.append(trick)
        else:
            tricks_as_list[-1] = trick
        return GameState(players=state.players, trump=state.trump, tricks=tuple(tricks_as_list), turn_count=state.turn_count)

    def render(self, mode="human"):
        state = self.game_engine.get_game_state()
        print("; ".join(str(player) for player in state.players))  # Optional visualization logic


gym.register("TrickEnv-v0", entry_point="trick_env:TrickEnv", max_episode_steps=200)

# if __name__ == "__main__":
#     env = TrickEnv()
#     env.reset()
#     env.step(0)
#     env.render()
