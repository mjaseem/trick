import argparse
import os

import gymnasium as gym
from stable_baselines3 import PPO
from stable_baselines3.common.callbacks import CheckpointCallback

import config
import trick_env  # Ensures the environment is registered


def main():
    args = parse_args()
    train = args.train
    config.DEBUG = args.debug

    models_dir = "./models/"
    log_dir = "./logs/"
    checkpoints_dir = './model_checkpoints/'
    os.makedirs(models_dir, exist_ok=True)
    os.makedirs(log_dir, exist_ok=True)
    os.makedirs(checkpoints_dir, exist_ok=True)

    timesteps = 300000
    checkpoint_callback = CheckpointCallback(save_freq=timesteps // 20, save_path=checkpoints_dir)
    env = gym.make("TrickEnv-v0")
    # model = PPO("MlpPolicy", env, verbose=1, tensorboard_log=log_dir, learning_rate=0.01,
    #         clip_range=0.5, ent_coef=0.02,
    #         )
    model = PPO.load(models_dir + "model-v13_reward-v1.zip", env=env,
                     custom_objects={'learning_rate': linear_decay_schedule(0.00001)})
    model.verbose = 1

    if train:
        model.learn(total_timesteps=timesteps, progress_bar=True, callback=checkpoint_callback,
                    # reset_num_timesteps=False)
                    )
        model.save(models_dir + "model-v14_reward-v2.zip")
        print("Model training completed and saved")

    # Test the trained model
    obs = env.reset()[0]
    while True:
        action, _states = model.predict(obs, deterministic=False)  # Predict the action
        obs, reward, done, truncated, info = env.step(action)  # Take the step in the environment
        env.render()  # Render the environment

        # Exit the loop if the episode is done or truncated
        if done or truncated:
            if any(player.hand for player in info['players']):
                raise Exception("episode is done but there are still cards in hand!")
            return


def parse_args():
    parser = argparse.ArgumentParser(description="Set train and debug flags.")
    parser.add_argument('--train', action='store_true', help="Flag to enable training")
    parser.add_argument('--debug', action='store_true', help="Flag to enable debug mode")
    return parser.parse_args()


def linear_decay_schedule(initial_lr: float):
    def schedule(progress_remaining: float) -> float:
        return initial_lr * progress_remaining

    return schedule

def exponential_decay_schedule(initial_lr: float, decay_rate: float):
    def schedule(progress_remaining: float) -> float:
        return initial_lr * (progress_remaining ** decay_rate )

    return schedule


if __name__ == "__main__":
    main()
