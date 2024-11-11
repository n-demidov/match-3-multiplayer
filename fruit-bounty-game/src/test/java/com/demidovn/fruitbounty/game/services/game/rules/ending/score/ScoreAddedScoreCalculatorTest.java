package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import static org.assertj.core.api.Assertions.assertThat;

import com.demidovn.fruitbounty.gameapi.model.Player;
import junit.framework.TestCase;
import org.junit.Test;

public class ScoreAddedScoreCalculatorTest extends TestCase {

  private ScoreAddedScoreCalculator scoreAddedScoreCalculator = new ScoreAddedScoreCalculator();
  private Player looser = createPlayer(0);

  @Test
  public void test1() {
    Player winner = createPlayer(0);
    int actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(1);

    winner = createPlayer(19);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(1);

    winner = createPlayer(20);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(2);

    winner = createPlayer(39);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(2);

    winner = createPlayer(40);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(3);

    winner = createPlayer(59);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(3);

    winner = createPlayer(60);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(4);
  }

  private Player createPlayer(int pointsWhileGame) {
    Player player = new Player();
    player.setPointsWhileGame(pointsWhileGame);
    return player;
  }

}