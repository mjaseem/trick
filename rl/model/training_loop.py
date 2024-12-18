
import gymnasium as gym
from stable_baselines3 import PPO
from stable_baselines3.common.callbacks import ProgressBarCallback
import trick_env  # Ensures the environment is registered

def main():
    env = gym.make("TrickEnv-v0")
    model = PPO("MlpPolicy", env, verbose=1)
    # model.load("trick_env_model")
    # Training the model (you can adjust total_timesteps as needed)
    model.learn(total_timesteps=10000,  progress_bar=True)

    # Save the trained model
    model.save("trick_env_model")
    print("Model training completed and saved as 'trick_env_model'.")

    # Test the trained model
    obs = env.reset()[0]
    while True:
        action, _states = model.predict(obs, deterministic=False)  # Predict the action
        obs, reward, done, truncated, info = env.step(action)  # Take the step in the environment
        env.render()  # Render the environment


    # Exit the loop if the episode is done or truncated
        if done or truncated:
            break

if __name__ == "__main__":
    main()