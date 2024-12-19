import os

import gymnasium as gym
from stable_baselines3 import PPO
from stable_baselines3.common.callbacks import CheckpointCallback
import trick_env  # Ensures the environment is registered


def main():
    train = True
    models_dir = "./models/"
    log_dir = "./logs/"
    checkpoints_dir = './model_checkpoints/'
    os.makedirs(models_dir, exist_ok=True)
    os.makedirs(log_dir, exist_ok=True)
    os.makedirs(checkpoints_dir, exist_ok=True)
    timesteps = 1000000
    checkpoint_callback = CheckpointCallback(save_freq=timesteps // 3, save_path=checkpoints_dir)
    env = gym.make("TrickEnv-v0")
    #     model = PPO("MlpPolicy", env, verbose=1, tensorboard_log=log_dir, learning_rate=0.01,
    #             clip_range=0.5, ent_coef=0.02,
    #             )
    model = PPO.load(models_dir + "model-v8_reward-v1.zip", env=env)
    model.verbose = 1

    if train:
        model.learn(total_timesteps=timesteps, progress_bar=True, callback=checkpoint_callback)
        model.save(models_dir + "model-v9_reward-v1.zip")
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


if __name__ == "__main__":
    main()
