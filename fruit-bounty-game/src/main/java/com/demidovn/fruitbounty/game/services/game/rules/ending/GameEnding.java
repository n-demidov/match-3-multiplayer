package com.demidovn.fruitbounty.game.services.game.rules.ending;

import com.demidovn.fruitbounty.game.services.game.rules.ending.score.ScoreAddedScoreCalculator;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.List;

public abstract class GameEnding {

  private final ScoreAddedScoreCalculator scoreAddedScoreCalculator = new ScoreAddedScoreCalculator();

  protected void finishGame(Game game, Player winner) {
    List<Player> players = game.getPlayers();
    if (players.size() > 2) {
      throw new UnsupportedOperationException("Now there is supporting only of 2 players!");
    }

    game.setFinished(true);
    game.setWinner(winner);
    game.setCurrentPlayer(null);

    if (winner != null) {
      Player looser = findLooser(players, winner);

      winner.setAddedScore(scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser));
      looser.setAddedScore(0);
    }
  }

  private Player findLooser(List<Player> players, Player winner) {
    return players
      .stream()
      .filter(player -> player.getId() != winner.getId())
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Can't find looser in players=" + players));
  }

}
