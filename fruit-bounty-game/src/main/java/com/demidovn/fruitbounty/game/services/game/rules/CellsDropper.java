package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Point;
import java.util.ArrayList;
import java.util.List;

public class CellsDropper {
  private static final Swiper swiper = new Swiper();

  public DroppedResult dropCells(Cell[][] cells) {
    DroppedResult droppedResult = new DroppedResult();
    for (int x = 0; x <cells.length; x++) {
      for (int y = cells[x].length - 2; y >= 0; y--) {
        Cell cell = cells[x][y];
        if (cell.isCleared()) {
          continue;
        }
        int dropDepth = dropCell(cell, cells);
        if (dropDepth > 0) {
          droppedResult.dropDepthMax = Math.max(dropDepth, droppedResult.dropDepthMax);
          droppedResult.droppedCells.add(cell);
        }
      }
    }
    return droppedResult;
  }

  private int dropCell(Cell cell, Cell[][] cells) {
    int x = cell.getX();
    int y = cell.getY();

    int dY = getDY(y, x, cells);

    while (dY > 0 && cells[x][y + dY].isCleared()) {
      y += dY;

      dY = getDY(y, x, cells);
    }

    if (y != cell.getY()) {
      swiper.swipe(cells, new Point(cell.getX(), cell.getY()), new Point(x, y));
    }

    return y - cell.getY();
  }

  private int getDY(int y, int x, Cell[][] cells) {
    if (y + 1 >= cells[x].length) {
      return 0;
    }

    return 1;
  }

  public static class DroppedResult {
    public int dropDepthMax;
    public List<Cell> droppedCells = new ArrayList<>();
  }

}
