package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import static com.demidovn.fruitbounty.game.GameOptions.GAME_ROUNDS_NUM;
import static com.demidovn.fruitbounty.game.GameOptions.MIN_CELLS_TO_MATCH;
import static com.demidovn.fruitbounty.game.GameOptions.PLAYER_MOVES_PER_ROUND;

import com.demidovn.fruitbounty.gameapi.model.Player;

public class ScoreAddedScoreCalculator {
  private static final int MIN_SCORE_PER_WIN = 1;
  private static final int MIN_SCORES = GAME_ROUNDS_NUM * PLAYER_MOVES_PER_ROUND * MIN_CELLS_TO_MATCH;
  private static final int POINTS_COEF = 10;

  public int findWinnerAddedScore(Player winner, Player looser) {
    int points = winner.getPointsWhileGame();

    int result = MIN_SCORE_PER_WIN;

    points = points - MIN_SCORES;
    if (points > 0) {
      result += points / POINTS_COEF;
    }

    return result;
  }
  
}
