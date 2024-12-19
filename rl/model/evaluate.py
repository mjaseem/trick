import os
from stable_baselines3 import PPO
from stable_baselines3.common.env_util import make_vec_env
from stable_baselines3.common.evaluation import evaluate_policy
import trick_env  # Ensures the environment is registered

# Directory containing your models
models_folder = "models"

# Define the environment you want to evaluate the models on
env_name = "TrickEnv-v0"  # Replace with your environment if different
env = make_vec_env(env_name, n_envs=1)

# List of model filenames (adjust if necessary)
model_files = [
    # "model-v1_lr0.01_reward-v0_ts-30000.zip",
    # "model-v2_lr0.01_reward-v0_ts-130000.zip",
    # "model-v3_lr0.001_reward-v0_ts-120000.zip",
    # "model-v4_lr0.005_reward-v0_ts-120000.zip",
    # "model-v6_lr0.005_reward-v0.zip",
    # "model-v7_lr0.005_reward-v0.zip",
    "model-v8_reward-v1.zip"
]

# Function to evaluate a model
def evaluate_model(model, env, n_eval_episodes=10):
    mean_reward, std_reward = evaluate_policy(model, env, n_eval_episodes=n_eval_episodes)
    return mean_reward, std_reward

# Store results for all models
results = []

# Loop through each model, load it, and evaluate
for model_file in model_files:
    model_path = os.path.join(models_folder, model_file)

    print(f"\nEvaluating model: {model_file}")

    # Load the model
    model = PPO.load(model_path, env=env)

    # Evaluate the model
    mean_reward, std_reward = evaluate_model(model, env)

    # Store results
    results.append({
        "model": model_file,
        "mean_reward": mean_reward,
        "std_reward": std_reward
    })

# Print the comparison results
print("\nComparison of Models:")
for result in results:
    print(f"{result['model']}: Mean Reward = {result['mean_reward']:.2f}, Std Reward = {result['std_reward']:.2f}")
