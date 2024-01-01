package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.gameapi.model.Point;
import com.demidovn.fruitbounty.server.AppConstants;
import com.demidovn.fruitbounty.server.dto.operations.request.GameRequestOperation;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GameRequestOperation2GameActionConverter implements FruitServerConverter<GameRequestOperation, GameAction> {

  @Override
  public GameAction convert(GameRequestOperation operation) {
    GameActionType gameActionType = GameActionType.valueOf((String)
        operation.getData().get(AppConstants.GAME_ACTION_TYPE));

    Point point1 = null, point2 = null;
    if (gameActionType == GameActionType.Move) {
      point1 = new Point(
          (Integer) ((Map) operation.getData().get("point1")).get("x"),
          (Integer) ((Map) operation.getData().get("point1")).get("y"));
      point2 = new Point(
          (Integer) ((Map) operation.getData().get("point2")).get("x"),
          (Integer) ((Map) operation.getData().get("point2")).get("y"));
    }

    return new GameAction(
        operation.getGame(),
        operation.getConnection().getUserId(),
        gameActionType,
        point1,
        point2);
  }

}
