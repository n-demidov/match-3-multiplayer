package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class MatchesFinderTest extends AbstractGameRulesTest {

  private static final int BOARD_SIZE = 7;

  private MatchesFinder matchesFinder = new MatchesFinder();
  private int s = 1;

  private GameAction gameAction;

  @Before
  public void before() {
    gameAction = new GameAction();
    gameAction.setGame(generateGame());

    setCurrentPlayer();
  }

  @Test
  public void noMatches() {
    Cell[][] cells = createCells();

    Set<Cell> actual = matchesFinder.findMatches(cells);

    assertThat(actual.size()).isEqualTo(0);
  }

  @Test
  public void thereMatches() {
    Cell[][] cells = createCellsWithMatches();

    Set<Cell> actual = matchesFinder.findMatches(cells);

    assertThat(actual.size()).isEqualTo(13);
  }

  private Cell[][] createCellsWithMatches() {
    Cell[][] cells = createCells();

    int a = 3;
    int b = 4;

    // Both horizontal and vertical
    cells[1][1].setType(a);
    cells[1][2].setType(a);
    cells[1][3].setType(a);
    cells[1][4].setType(a);
    cells[2][1].setType(a);
    cells[3][1].setType(a);

    // Horizontal
    cells[5][2].setType(a);
    cells[5][3].setType(a);
    cells[5][4].setType(a);

    // Vertical
    cells[3][5].setType(b);
    cells[4][5].setType(b);
    cells[5][5].setType(b);
    cells[6][5].setType(b);

    return cells;
  }

  private Cell[][] createCells() {
    Cell[][] cells = new Cell[BOARD_SIZE][BOARD_SIZE];

    for (int x = 0; x < BOARD_SIZE; x++) {
      for (int y = 0; y < BOARD_SIZE; y++) {
        Cell cell = new Cell(switchS(), x, y);
        cells[x][y] = cell;
      }
    }

    return cells;
  }

  private int switchS() {
    if (s == 1) {
      s++;
      return s;
    } else {
      s = 1;
      return s;
    }
  }

  @Override
  protected List<Player> generatePlayers() {
    List<Player> players = new ArrayList<>();

    Player firstPlayer = new Player();
    Player secondPlayer = new Player();

    firstPlayer.setId(PLAYER_ID);
    secondPlayer.setId(OTHER_PLAYER_ID);

    players.add(firstPlayer);
    players.add(secondPlayer);

    return players;
  }

  private void setCurrentPlayer() {
    gameAction.setActionedPlayerId(PLAYER_ID);
    gameAction.getGame().setCurrentPlayer(getPlayer());
  }

  private Player getPlayer() {
    return gameAction.getGame().getPlayers().get(0);
  }

}