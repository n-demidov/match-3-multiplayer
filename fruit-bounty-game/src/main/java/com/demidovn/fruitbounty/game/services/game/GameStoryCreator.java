package com.demidovn.fruitbounty.game.services.game;

import static com.demidovn.fruitbounty.game.GameOptions.STANDARD_ANIMATION_ITER;

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

  private static final Map<GameStoryType, Integer> storyIdxCounterMaxs = new HashMap<>();

  static {
    storyIdxCounterMaxs.put(GameStoryType.SWIPE, STANDARD_ANIMATION_ITER);
    storyIdxCounterMaxs.put(GameStoryType.MATCH, STANDARD_ANIMATION_ITER);
    storyIdxCounterMaxs.put(GameStoryType.RECREATE_BOARD, STANDARD_ANIMATION_ITER * 7);
    storyIdxCounterMaxs.put(GameStoryType.PLAYER_CHANGED, STANDARD_ANIMATION_ITER * 7);
  }

  public GameStory create(GameStoryType type, Game gameState) {
    return new GameStory(type, gameState, storyIdxCounterMaxs.get(type));
  }

  public GameStory create(GameStoryType type, Game gameState, boolean extraMove) {
    return new GameStory(type, gameState, storyIdxCounterMaxs.get(type), extraMove);
  }

  public GameStory createPlayerChanged(GameStoryType type, Game gameState, boolean newRound) {
    GameStory gameStory = new GameStory(type, gameState, storyIdxCounterMaxs.get(type));
    gameStory.setNewRound(newRound);
    return gameStory;
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
