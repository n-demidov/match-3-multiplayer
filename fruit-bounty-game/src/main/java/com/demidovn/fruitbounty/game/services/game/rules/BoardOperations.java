package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BoardOperations {

  private final Random rand = new Random();
  private final MoveFeasibilityChecker moveFeasibilityChecker = new MoveFeasibilityChecker();
  private final MatchesFinder matchesFinder = new MatchesFinder();

  public void recreateClearedCells(Cell[][] cells) {
    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];
        if (cell.isCleared()) {
          cell.setCleared(false);
          cell.setType(createRandomCellType());
        }
      }
    }
  }

  public void recreateCellsIfNoMoves(Board board) {
    Cell[][] cells = board.getCells();
    if (moveFeasibilityChecker.isAnyMovePossible(cells)) {
      return;
    }

    int boardWidth = cells.length;
    int boardHeight = cells[0].length;

    board.setCells(createBoard(boardWidth, boardHeight));
  }

  public Cell[][] createBoard(int boardWidth, int boardHeight) {
    Cell[][] cells = createBoardInner(boardWidth, boardHeight);
    int counter = 0;
    while (matchesFinder.findMatches(cells).size() > 0) {
      cells = createBoardInner(boardWidth, boardHeight);
      if (++counter >= 1_000) {
        log.error("Too many tries to create board, counter={}", counter);
      }
    }

    return cells;
  }

  public Cell[][] createBoardInner(int boardWidth, int boardHeight) {
    Cell[][] cells = new Cell[boardWidth][boardHeight];

    for (int x = 0; x < boardWidth; x++) {
      for (int y = 0; y < boardHeight; y++) {
        cells[x][y] = new Cell(createRandomCellType(), x, y);
      }
    }
    return cells;
  }

  public int createRandomCellType() {
    return rand.nextInt(GameOptions.CELL_TYPES_COUNT) + 1;
  }

}
