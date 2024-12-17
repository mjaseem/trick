package com.mjaseem.prophecy.service;

import com.mjaseem.prophecy.engine.GameEngine;
import com.mjaseem.prophecy.engine.GameState;
import com.mjaseem.prophecy.engine.Records;
import com.mjaseem.prophecy.strategy.InputPlayerStrategy;
import com.mjaseem.prophecy.strategy.TwoPlayerStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class GameController {

    private final InputPlayerStrategy inputPlayerStrategy;
    private GameEngine gameEngine;
    private final List<Records.PlayerConfig> players;


    public GameController() {
        this.inputPlayerStrategy = new InputPlayerStrategy();
        this.players = List.of(new Records.PlayerConfig("V4", new TwoPlayerStrategy(4)),
                new Records.PlayerConfig("AI", inputPlayerStrategy));
        this.gameEngine = new GameEngine(players); // Initialize your engine here
    }

    @GetMapping("/reset")
    public Map<String, Object> resetGame() {
        this.gameEngine = new GameEngine(players); // Reset the thegame
        Map<String, Object> response = new HashMap<>();
        response.put("state", gameEngine.getGameState()); // Encode initial state
        return response;
    }

    @PostMapping("/step")
    public Map<String, Object> takeStep(@RequestBody Map<String, Integer> actionRequest) {
        int action = actionRequest.get("action");

        inputPlayerStrategy.setNextPlay(action);
        gameEngine.playTrick(); // Perform action in engine
        GameState gameState = gameEngine.getGameState();

        Map<String, Object> response = new HashMap<>();
        response.put("state", "1"); // Encode new state
        response.put("reward", "1"); // Reward for the action
        response.put("done", "1"); // Is game over
        return response;
    }

//    private
}
