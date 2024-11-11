package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import com.demidovn.fruitbounty.gameapi.model.Player;

public class ScoreAddedScoreCalculator {
  public static final int POINTS_COEF = 20;

  public int findWinnerAddedScore(Player winner, Player looser) {
    int points = winner.getPointsWhileGame();

    int result = 1;
    result += points / POINTS_COEF;

    return result;
  }
  
}
