package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import org.junit.Before;
import org.junit.Test;

public class MoveFeasibilityCheckerTest {
  private static final int BOARD_SIZE = 7;

  private MoveFeasibilityChecker moveFeasibilityChecker = new MoveFeasibilityChecker();

  private int s = 1;

  @Before
  public void before() {
  }

  @Test
  public void whenNoAnyFeasibleMove() {
    Cell[][] cells = createCells();
    Cell[][] copiedCells = Game.copyCells(cells);

    boolean actual = moveFeasibilityChecker.isAnyMovePossible(copiedCells);

    assertTrue(actual);
    assertThat(copiedCells).isEqualTo(cells);
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

}