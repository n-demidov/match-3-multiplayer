package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.model.Point;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveCorrectness extends AbstractGameRules {
  private static final MatchesFinder matchesFinder = new MatchesFinder();

  public boolean isMoveValid(GameAction gameAction) {
    return isPlayerCurrent(gameAction)
        && isActionBeforeGameExpired(gameAction)
        && areCellsNeighbors(gameAction.getPoint1(), gameAction.getPoint2())
        && isMatch(gameAction.getGame().getBoard().getCells());
  }

  private boolean isPlayerCurrent(GameAction gameAction) {
    Player actionedPlayer = gameAction.findActionedPlayer();
    return actionedPlayer.equals(gameAction.getGame().getCurrentPlayer());
  }

  private boolean isActionBeforeGameExpired(GameAction gameAction) {
    return gameAction.getGame().getCurrentMoveStarted() + gameAction.getGame().getTimePerMoveMs()
      + GameOptions.MOVE_TIME_DELAY_CORRECTION > Instant.now().toEpochMilli();
  }

  private boolean areCellsNeighbors(Point point1, Point point2) {
    return Math.abs(point1.getX() - point2.getX()) + Math.abs(point1.getY() - point2.getY()) == 1;
  }

  private boolean isMatch(Cell[][] cells) {
    return matchesFinder.findMatches(cells).size() > 0;
  }

}
