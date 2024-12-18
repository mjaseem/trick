
import gymnasium as gym
from stable_baselines3 import PPO
import trick_env  # Ensures the environment is registered

def main():
    env = gym.make("TrickEnv-v0")
    model = PPO("MlpPolicy", env, verbose=1)

    # Training the model (you can adjust total_timesteps as needed)
    model.learn(total_timesteps=10000)

    # Save the trained model
    model.save("trick_env_model")
    print("Model training completed and saved as 'trick_env_model'.")

    # Test the trained model
    obs = env.reset()
    for _ in range(100):  # Play 100 steps
        action, _states = model.predict(obs, deterministic=True)
        obs, reward, done, info = env.step(action)
        env.render()
        if done:
            obs = env.reset()

if __name__ == "__main__":
    main()