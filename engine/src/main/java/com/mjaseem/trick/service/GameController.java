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
    private static final String CLIENT_PLAYER_NAME = "AI";

    private final PromptingStrategy promptingStrategy;
    private GameEngine gameEngine;
    private final List<Records.PlayerConfig> players;


    public GameController() {
        this.promptingStrategy = new PromptingStrategy();
        this.players = List.of(new Records.PlayerConfig("V4", new TwoPlayerStrategy(4)),
                new Records.PlayerConfig(CLIENT_PLAYER_NAME, promptingStrategy));
        this.gameEngine = new GameEngine(players); // Initialize your engine here
    }

    @GetMapping("/reset")
    public Map<String, Object> resetGame() {
        this.gameEngine = new GameEngine(players); // Reset the thegame
        Map<String, Object> response = new HashMap<>();
        response.put("state", new DetailedEncoder().encode(gameEngine.getGameState(), CLIENT_PLAYER_NAME)); // Encode initial state
        response.put("info", gameEngine.getGameState());
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
            response.put("state", new DetailedEncoder().encode(gameState, CLIENT_PLAYER_NAME)); // Encode new state
            response.put("info", gameState);
            response.put("reward", 1);
            response.put("done", gameState.turnCount() == MAX_CARDS); // Is game over
            return response; // Perform action in engine
        } catch (GameEngine.BadMoveException e) {
            log.info("Bad move. Try again", e);
            Records.GameState gameState = gameEngine.getGameState();
            log.info("Current trick {}", gameState.gameHistory().getTricks().getLast());
            Map<String, Object> response = new HashMap<>();
            response.put("state", new DetailedEncoder().encode(gameState, CLIENT_PLAYER_NAME)); // Encode new state
            response.put("reward", -10);
            response.put("done", false);
            response.put("info", gameState);
            return response;
        }

    }

//    private
}
