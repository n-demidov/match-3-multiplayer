package com.demidovn.fruitbounty.gameapi.model;

import java.util.List;
import lombok.Data;

@Data
public class GameStory {
  private static final int STANDARD_ANIMATION_ITER = 4;  // todo: extract to general consts or delete

  private GameStoryType type;
  private Game gameState;
  private List<Cell> specialCells;
  private int storyIdxCounterMax;

  public GameStory(Game game) {
    this.gameState = game;
    this.storyIdxCounterMax = getStandardAnimationIter(STANDARD_ANIMATION_ITER);
  }

  public GameStory(GameStoryType type, Game game) {
    this.type = type;
    this.gameState = game;
    this.storyIdxCounterMax = getStandardAnimationIter(STANDARD_ANIMATION_ITER);
  }

  public GameStory(GameStoryType type, Game game, List<Cell> specialCells, int dropDepth) {
    this.type = type;
    this.gameState = game;
    this.specialCells = specialCells;
    this.storyIdxCounterMax = getStandardAnimationIter(dropDepth);
  }

  private static int getStandardAnimationIter(int dropDepth) {
    return STANDARD_ANIMATION_ITER * dropDepth;
  }

}
