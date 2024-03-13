package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.services.game.rules.ending.GameEndingByAllRoundsCompleted;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrentPlayerSwitch extends AbstractGameRules {

  private static final PlayersFinder playersFinder = new PlayersFinder();
  private static final GameEndingByAllRoundsCompleted gameEndingByAllRoundsCompleted =
      new GameEndingByAllRoundsCompleted();

  public void switchCurrentPlayer(Game game) {
    if (game.isFinished()) {
      return;
    }

    Player currentPlayer = game.getCurrentPlayer();
    if (currentPlayerShouldMove(currentPlayer)) {
      return;
    }

    Player nextPlayer = playersFinder.getNextPlayer(game, currentPlayer.getId());

    if (currentRoundCompleted(nextPlayer)) {
      if (allRoundsCompleted(game)) {
        gameEndingByAllRoundsCompleted.finishGameByAllRoundsCompleted(game);
        return;
      }

      createNewRound(game);
    }

    game.setCurrentPlayer(nextPlayer);
  }

  private boolean currentPlayerShouldMove(Player currentPlayer) {
    return currentPlayer.getMovesInRound() > 0;
  }

  private boolean currentRoundCompleted(Player nextPlayer) {
    return nextPlayer.getMovesInRound() == 0;
  }

  private boolean allRoundsCompleted(Game game) {
    return game.getCurrentRound() == GameOptions.GAME_ROUNDS_NUM;
  }

  private void createNewRound(Game game) {
    game.setCurrentRound(game.getCurrentRound() + 1);
    game.getPlayers().forEach(p -> p.setMovesInRound(GameOptions.PLAYER_MOVES_PER_ROUND));
  }

}
