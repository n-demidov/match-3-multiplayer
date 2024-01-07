package com.demidovn.fruitbounty.game.services.game.rules;

import static com.demidovn.fruitbounty.game.GameOptions.MIN_CELLS_TO_MATCH;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MatchesFinder {

  public Set<Cell> findMatches(Cell[][] cells) {
    Set<Cell> result = new HashSet<>();

    result.addAll(innerFindMatches(cells, true));
    result.addAll(innerFindMatches(cells, false));

    return result;
  }

  private Set<Cell> innerFindMatches(Cell[][] cells, boolean type) {
    Set<Cell> result = new HashSet<>();

    for (int x = 0; x < cells.length; x++) {
      Stack<Cell> stack = new Stack<>();
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = type ? cells[x][y] : cells[y][x];

        if (y == 0 || cell.getType() != stack.peek().getType()) {
          if (stack.size() >= MIN_CELLS_TO_MATCH) {
            result.addAll(stack);
          }
          stack.clear();
        }
        stack.push(cell);
      }

      if (stack.size() >= MIN_CELLS_TO_MATCH) {
        result.addAll(stack);
      }
    }

    return result;
  }

}
