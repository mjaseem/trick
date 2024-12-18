
import gymnasium as gym
from stable_baselines3 import PPO
import trick_env  # Ensures the environment is registered

def main():
    env = gym.make("TrickEnv-v0")
    model = PPO("MlpPolicy", env, verbose=1)

    model.load("trick_env_model")

    # Test the trained model
    obs = env.reset()[0]
    while True:
        action, _states = model.predict(obs, deterministic=False)  # Predict the action
        obs, reward, done, truncated, info = env.step(action)  # Take the step in the environment
        env.render()  # Render the environment

        # Print info at each step
        print(info)

        # Exit the loop if the episode is done or truncated
        if done or truncated:
            break

if __name__ == "__main__":
    main()