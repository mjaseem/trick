package com.mjaseem.trick.service;

import com.mjaseem.trick.engine.GameEngine;
import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.strategy.PromptingStrategy;
import com.mjaseem.trick.strategy.TwoPlayerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mjaseem.trick.engine.Records.MAX_CARDS;

@RestController
@RequestMapping("/")
public class GameController {
    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private static final String AI = "AI";

    private final PromptingStrategy promptingStrategy;
    private GameEngine gameEngine;
    private final List<Records.PlayerConfig> players;


    public GameController() {
        this.promptingStrategy = new PromptingStrategy();
        this.players = List.of(new Records.PlayerConfig("V4", new TwoPlayerStrategy(4)),
                new Records.PlayerConfig(AI, promptingStrategy));
        this.gameEngine = new GameEngine(players); // Initialize your engine here
    }

    @GetMapping("/reset")
    public Map<String, Object> resetGame() {
        this.gameEngine = new GameEngine(players); // Reset the thegame
        Map<String, Object> response = new HashMap<>();
        response.put("state", GameStateEncoder.encode(gameEngine.getGameState())); // Encode initial state
        return response;
    }

    @PostMapping("/step")
    public Map<String, Object> takeStep(@RequestBody Map<String, Integer> actionRequest) {
        int action = actionRequest.get("action");

        promptingStrategy.setNextPlay(action);
        try {
            gameEngine.run();
            Records.GameState gameState = gameEngine.getGameState();

            Map<String, Object> response = new HashMap<>();
            response.put("state", GameStateEncoder.encode(gameState)); // Encode new state
            response.put("reward", 1); //TODO  Reward for the action
            response.put("done", gameState.turnCount() == MAX_CARDS); // Is game over
            return response; // Perform action in engine
        } catch (GameEngine.BadMoveException e) {
            log.info("Bad move. Try again", e);
            Records.GameState gameState = gameEngine.getGameState();
            log.info("Current trick {}", gameState.gameHistory().getTricks().getLast());
            Map<String, Object> response = new HashMap<>();
            response.put("state", GameStateEncoder.encode(gameState)); // Encode new state
            response.put("reward", 0); // TODO what is a horrible reward
            response.put("done", false); // Is game over
            return response;
        }

    }

//    private
}
