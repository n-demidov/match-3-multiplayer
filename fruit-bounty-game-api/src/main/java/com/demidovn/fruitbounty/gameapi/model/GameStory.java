package com.demidovn.fruitbounty.gameapi.model;

import java.util.List;
import lombok.Data;

@Data
public class GameStory {
  private GameStoryType type;
  private Game gameState;
  private List<Cell> specialCells;
  private int dropDepth;

  public GameStory(Game game) {
    this.gameState = game;
  }

  public GameStory(GameStoryType type, Game game, List<Cell> specialCells, int dropDepth) {
    this.type = type;
    this.gameState = game;
    this.specialCells = specialCells;
    this.dropDepth = dropDepth;
  }
}
