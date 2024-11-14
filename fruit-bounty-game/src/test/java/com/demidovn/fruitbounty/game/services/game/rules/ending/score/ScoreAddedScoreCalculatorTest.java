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

    winner = createPlayer(17);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(1);

    winner = createPlayer(27);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(1);

    winner = createPlayer(28);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(2);

    winner = createPlayer(37);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(2);

    winner = createPlayer(38);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(3);

    winner = createPlayer(47);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(3);

    winner = createPlayer(48);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(4);

    winner = createPlayer(57);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(4);

    winner = createPlayer(58);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(5);

    winner = createPlayer(158);
    actual = scoreAddedScoreCalculator.findWinnerAddedScore(winner, looser);
    assertThat(actual).isEqualTo(15);
  }

  private Player createPlayer(int pointsWhileGame) {
    Player player = new Player();
    player.setPointsWhileGame(pointsWhileGame);
    return player;
  }

}