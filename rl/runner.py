from collections import defaultdict
from itertools import combinations

import config
from engine import GameEngine
from strategy import TwoPlayerStrategy

ITERATIONS = 5 if config.DEBUG else 5000

def display_scores(scores: dict) -> None:
    print("Scores Against Each Opponent:")
    for player, opponent_scores in scores.items():
        print(f"{player}:")
        for opponent, score in opponent_scores.items():
            print(f"  Against {opponent}: {score}/{ITERATIONS}")
        print()

def main():
    # List of player configurations with strategy (replace TwoPlayerStrategy with the actual strategy)
    players = {
        "V3": TwoPlayerStrategy(),
        "V4": TwoPlayerStrategy()
    }

    # Create a dictionary to store results (scores against each opponent)
    scores = defaultdict(lambda: defaultdict(int))

    # Run games for each pair of players
    for (player1_name, player1_strategy), (player2_name, player2_strategy) in combinations(players.items(), 2):
        for _ in range(ITERATIONS):

            engine = GameEngine({player1_name: player1_strategy, player2_name: player2_strategy})
            engine.run()

            # Get the scores from the engine (assuming it has a way to return scores by player)
            score1 = engine.get_scores()[player1_name]
            score2 = engine.get_scores()[player2_name]

            # Update scores against each other
            scores[player1_name][player2_name] += 1 if score1 > score2 else 0
            scores[player2_name][player1_name] += 1 if score2 > score1 else 0

    # Display final scores for each player against all opponents
    display_scores(scores)

if __name__ == "__main__":
    main()