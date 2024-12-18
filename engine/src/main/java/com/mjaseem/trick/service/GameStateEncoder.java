package com.mjaseem.trick.service;

import com.mjaseem.trick.engine.Records;
import com.mjaseem.trick.engine.Trick;

import java.util.ArrayList;
import java.util.List;

public interface GameStateEncoder {

    double[] encode(Records.GameState gameState, String player);
}
