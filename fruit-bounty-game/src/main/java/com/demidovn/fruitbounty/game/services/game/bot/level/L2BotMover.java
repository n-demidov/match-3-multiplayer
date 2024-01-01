package com.demidovn.fruitbounty.game.services.game.bot.level;

import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.game.bot.movefinder.Level2ThresholdDeepMoveFinder;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Point;

public class L2BotMover implements BotMover {
  private final int maxDeepLimit;

  private final Level2ThresholdDeepMoveFinder l2MoveFinder = new Level2ThresholdDeepMoveFinder();

  public L2BotMover(int maxDeepLimit) {
    this.maxDeepLimit = maxDeepLimit;
  }

  @Override
  public Pair<Point, Point> findMove(Game game) {
    throw new UnsupportedOperationException();
//    return l2MoveFinder.findBestMove(game.getCurrentPlayer(), game, maxDeepLimit);
  }

}
