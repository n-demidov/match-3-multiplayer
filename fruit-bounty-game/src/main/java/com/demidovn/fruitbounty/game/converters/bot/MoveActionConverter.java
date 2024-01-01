package com.demidovn.fruitbounty.game.converters.bot;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.gameapi.model.Point;

public class MoveActionConverter {

  public GameAction convert2MoveAction(Game game, Point point1, Point point2) {
    return new GameAction(
        game,
        game.getCurrentPlayer().getId(),
        GameActionType.Move,
        point1,
        point2
    );
  }

}
