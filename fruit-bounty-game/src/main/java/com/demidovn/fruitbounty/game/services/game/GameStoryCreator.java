package com.demidovn.fruitbounty.game.services.game;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameStory;
import com.demidovn.fruitbounty.gameapi.model.GameStoryType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GameStoryCreator {
  private static final int STANDARD_ANIMATION_ITER = 4;  // todo: extract to general consts or delete

  private static final Map<GameStoryType, Integer> storyIdxCounterMaxs = new HashMap<>();

  static {
    storyIdxCounterMaxs.put(GameStoryType.SWIPE, STANDARD_ANIMATION_ITER);
    storyIdxCounterMaxs.put(GameStoryType.MATCH, STANDARD_ANIMATION_ITER);
    storyIdxCounterMaxs.put(GameStoryType.RECREATE_BOARD, STANDARD_ANIMATION_ITER * 5);
  }

  public GameStory create(GameStoryType type, Game gameState) {
    return new GameStory(type, gameState, storyIdxCounterMaxs.get(type));
  }

  public GameStory create(GameStoryType type, Game gameState, boolean extraMove) {
    return new GameStory(type, gameState, storyIdxCounterMaxs.get(type), extraMove);
  }

  public GameStory create(GameStoryType type, Game gameState, List<Cell> specialCells, int dropDepth) {
    if (!(type == GameStoryType.DROP_CELLS || type == GameStoryType.CREATED_CELLS)) {
      throw new UnsupportedOperationException();
    }

    return new GameStory(type, gameState, getStandardAnimationIter(dropDepth), specialCells);
  }

  private static int getStandardAnimationIter(int dropDepth) {
    return STANDARD_ANIMATION_ITER * dropDepth + 2;
  }

}
