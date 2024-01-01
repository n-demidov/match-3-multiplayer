package com.demidovn.fruitbounty.gameapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // todo: remove it later
@Data
public class GameAction {

  private long createdTime = System.currentTimeMillis();
  private Game game;
  private long actionedPlayerId;

  private GameActionType type;
  private Point point1;
  private Point point2;

  public GameAction(Game game, long actionedPlayerId, GameActionType type, Point point1, Point point2) {
    this.game = game;
    this.actionedPlayerId = actionedPlayerId;
    this.type = type;
    this.point1 = point1;
    this.point2 = point2;
  }

  public Player findActionedPlayer() {
    return game.getPlayers()
        .stream()
        .filter(player -> player.getId() == actionedPlayerId)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(String.format(
          "Can't find player by actionedPlayerId=%s, players=%s",
          actionedPlayerId, game.getPlayers())));
  }

  public Cell getTargetCell() {
//    return game.getBoard().getCells()[x][y]; // todo
    return game.getBoard().getCells()[point1.getX()][point1.getY()];
  }

  public Cell findCell(int x, int y) {
    return game.getBoard().getCells()[x][y];
  }

}
