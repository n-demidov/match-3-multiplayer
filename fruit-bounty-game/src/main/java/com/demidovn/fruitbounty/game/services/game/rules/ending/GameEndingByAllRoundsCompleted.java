package com.demidovn.fruitbounty.game.services.game.rules.ending;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.List;

public class GameEndingByAllRoundsCompleted extends GameEnding {

  public void finishGameByAllRoundsCompleted(Game game) {
    Player winner = findWinner(game.getPlayers());

    finishGame(game, winner);
  }

//  @Nullable
  private Player findWinner(List<Player> players) {
    if (players.size() > 2) {
      throw new UnsupportedOperationException("Now there is supporting only of 2 players!");
    }

    Player firstPlayer = players.get(0);
    Player secondPlayer = players.get(1);

    if (firstPlayer.getPointsWhileGame() == secondPlayer.getPointsWhileGame()) {
      return null;
    } else if (firstPlayer.getPointsWhileGame() > secondPlayer.getPointsWhileGame()) {
      return firstPlayer;
    } else {
      return secondPlayer;
    }
  }

}
