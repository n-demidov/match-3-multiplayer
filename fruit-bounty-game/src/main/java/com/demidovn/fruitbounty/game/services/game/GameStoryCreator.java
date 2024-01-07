package com.demidovn.fruitbounty.game.services.game;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameStory;
import com.demidovn.fruitbounty.gameapi.model.GameStoryType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GameStoryCreator {

  public GameStory create(Game gameState) {
    return new GameStory(gameState);
  }

  public GameStory create(GameStoryType type, Game gameState) {
    return new GameStory(type, gameState);
  }

  public GameStory create(GameStoryType type, Game gameState, List<Cell> specialCells, int dropDepth) {
    return new GameStory(type, gameState, specialCells, dropDepth);
  }

}
