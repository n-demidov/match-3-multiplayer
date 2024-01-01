package com.demidovn.fruitbounty.game.services.game.bot.level;

import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Point;

public interface BotMover {

  Pair<Point, Point> findMove(Game game);

}
