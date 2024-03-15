package com.demidovn.fruitbounty.gameapi.model;

import java.util.List;
import lombok.Data;

@Data
public class GameStory {

  private GameStoryType type;
  private Game gameState;
  private List<Cell> specialCells;
  private int storyIdxCounterMax;
  private boolean extraMove;

  private boolean newRound; // For 'GameStoryType.PLAYER_CHANGED' state

  public GameStory(GameStoryType type, Game game, int storyIdxCounterMax) {
    this.type = type;
    this.gameState = game;
    this.storyIdxCounterMax = storyIdxCounterMax;
  }

  public GameStory(GameStoryType type, Game game, int storyIdxCounterMax, boolean extraMove) {
    this.type = type;
    this.gameState = game;
    this.storyIdxCounterMax = storyIdxCounterMax;
    this.extraMove = extraMove;
  }

  public GameStory(GameStoryType type, Game game, int storyIdxCounterMax, List<Cell> specialCells) {
    this.type = type;
    this.gameState = game;
    this.storyIdxCounterMax = storyIdxCounterMax;
    this.specialCells = specialCells;
  }

}
