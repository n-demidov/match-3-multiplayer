package com.demidovn.fruitbounty.game.services.game.bot.level;

import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.game.bot.movefinder.Level1SimpleMovementToCenterMoveFinder;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Point;

public class L1BotMover implements BotMover {

  private final Level1SimpleMovementToCenterMoveFinder l1MoveFinder = new Level1SimpleMovementToCenterMoveFinder();

  @Override
  public Pair<Point, Point> findMove(Game game) {
    return l1MoveFinder.findMove(game);
  }

}
