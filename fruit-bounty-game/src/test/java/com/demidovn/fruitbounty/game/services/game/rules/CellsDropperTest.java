package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import org.junit.Test;

public class CellsDropperTest {

  private static final int BOARD_SIZE = 7;

  private CellsDropper cellsDropper = new CellsDropper();
  private int s = 1;

  @Test
  public void noCleared() {
    Cell[][] cells = createCells();

    Cell[][] actual = Game.copyCells(cells);
    cellsDropper.dropCells(actual);

    assertThat(actual).isEqualTo(cells);
  }

  @Test
  public void thereCleared() {
    Cell[][] cells = createCellsWithCleared();

    Cell[][] actual = Game.copyCells(cells);
    cellsDropper.dropCells(actual);

    assertThat(actual).isNotEqualTo(cells);

    // 'Cleared' changed correctly
    assertTrue(actual[1][0].isCleared());
    assertTrue(actual[1][1].isCleared());
    assertTrue(actual[1][2].isCleared());
    assertFalse(actual[1][3].isCleared());
    assertFalse(actual[1][4].isCleared());
    assertFalse(actual[1][5].isCleared());
    assertFalse(actual[1][6].isCleared());

    // 'Types' changed correctly
    assertThat(actual[1][4].getType()).isEqualTo(cells[1][1].getType());
    assertThat(actual[1][3].getType()).isEqualTo(cells[1][0].getType());
  }

  private Cell[][] createCellsWithCleared() {
    Cell[][] cells = createCells();

    cells[1][2].setCleared(true);
    cells[1][3].setCleared(true);
    cells[1][4].setCleared(true);

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

}