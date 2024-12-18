import gymnasium as gym
import requests
import numpy as np


class TrickEnv(gym.Env):
    def __init__(self):
        super(TrickEnv, self).__init__()
        self.server_url = "http://localhost:8080"
        self.max_hand = 13
        self.action_space = gym.spaces.Discrete(self.max_hand)  # 13 possible actions (cards to play)
        self.vector_size = 260
        self.observation_space = gym.spaces.Box(low=0, high=1, shape=(260,), dtype=np.float32)

    def reset(self, seed=None, options=None):
        print("Resetting the environment")
        response = requests.get(f"{self.server_url}/reset")
        if response.status_code != 200:
            raise RuntimeError("Server reset endpoint failed.")

        return np.array(response.json()['state'], dtype=np.float32), {}

    def step(self, action):
        response = requests.post(f"{self.server_url}/step", json={"action": action})
        if response.status_code != 200:
            raise RuntimeError("Server step endpoint failed.")
        data = response.json()
        state = data['state']
        reward = data['reward']
        done = data['done']
        return np.array(state, dtype=np.float32), reward, done, {}

    def render(self, mode="human"):
        pass  # Optional visualization logic


gym.register("TrickEnv-v0", entry_point="trickenv:TrickEnv", max_episode_steps=13)
