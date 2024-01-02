package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Point;
import java.util.List;

public class MoveFeasibilityChecker extends AbstractGameRules {
  private static final Swiper swiper = new Swiper();
  private static final MatchesFinder matchesFinder = new MatchesFinder();

  public boolean isAnyMovePossible(Cell[][] cells) {
    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        List<Cell> neighborCells = getNeighborCells(cells, x, y);

        for (Cell neighbor : neighborCells) {
          Cell[][] copiedCells = Game.copyCells(cells);
          swiper.swipe(copiedCells, new Point(x, y), new Point(neighbor.getX(), neighbor.getY()));
          if (!matchesFinder.findMatches(copiedCells).isEmpty()) {
            return true;
          }
        }
      }
    }

    return false;
  }

}
