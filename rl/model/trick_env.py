import json

import gymnasium as gym
import requests
import numpy as np

import config
from engine import GameEngine, IllegalMoveException, constants
from model import encoder
from strategy import TwoPlayerStrategy
from strategy.prompting_strategy import PromptingStrategy


class TrickEnv(gym.Env):
    def __init__(self):
        super(TrickEnv, self).__init__()
        self.prompting_strategy = PromptingStrategy()
        players = {
            "AI": self.prompting_strategy,
            "Engine": TwoPlayerStrategy()
        }
        self.game_engine = GameEngine(players)
        self.action_space = gym.spaces.Discrete(constants.MAX_CARD_RANK)  # 13 possible actions (cards to play)
        self.vector_size = encoder.VECTOR_SIZE
        self.observation_space = gym.spaces.Box(low=0, high=1, shape=(self.vector_size,), dtype=np.float32)

    def reset(self, seed=None, options=None):
        # print("Resetting the environment")
        self.game_engine.reset()
        state = self.game_engine.get_game_state()
        return np.array(encoder.encode(game_state=state, player="AI"), dtype=np.float32), state.__dict__

    def step(self, action):
        valid_move = True
        try:
            self.prompting_strategy.set_next_play(action)
            self.game_engine.run()
        except IllegalMoveException as e:
            valid_move = False
            if config.DEBUG:
                print("Illegal move. Shall be punished. ", e)

        state = self.game_engine.get_game_state()
        done = state.turn_count >= constants.MAX_CARDS_IN_HAND
        return np.array(encoder.encode(game_state=state, player="AI"),
                        dtype=np.float32), 1 if valid_move else -100, done, False, state.__dict__

    def render(self, mode="human"):
        state = self.game_engine.get_game_state()
        print("; ".join(str(player) for player in state.players)) # Optional visualization logic


gym.register("TrickEnv-v0", entry_point="trick_env:TrickEnv", max_episode_steps=200)

# if __name__ == "__main__":
#     env = TrickEnv()
#     env.reset()
#     env.step(0)
#     env.render()
