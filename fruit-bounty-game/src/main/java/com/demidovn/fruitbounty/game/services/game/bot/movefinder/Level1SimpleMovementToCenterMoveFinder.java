package com.demidovn.fruitbounty.game.services.game.bot.movefinder;

import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.Randomizer;
import com.demidovn.fruitbounty.game.services.game.rules.CellsFinder;
import com.demidovn.fruitbounty.game.services.game.rules.MatchesFinder;
import com.demidovn.fruitbounty.game.services.game.rules.Swiper;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Point;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Level1SimpleMovementToCenterMoveFinder {

  private static final String CAN_NOT_FIND_ANY_MOVE_FOR_BOT_GAME = "Can't find any move for bot, game=%s";

  private static final CellsFinder cellsFinder = new CellsFinder();
  private static final Randomizer randomizer = new Randomizer();

  private static final MatchesFinder matchesFinder = new MatchesFinder();
  private static final Swiper swiper = new Swiper();

  public Pair<Point, Point> findMove(Game game) {
    Cell[][] cells = game.getBoard().getCells();

    List<Pair<Point, Point>> foundMoves = new ArrayList<>();
    for (int x = 0; x < cells.length; x ++) {
      for (int y = 0; y < cells[x].length; y ++) {
        Cell cell = cells[x][y];
        Point point1 = new Point(cell.getX(), cell.getY());
        List<Cell> neighborCells = cellsFinder.getNeighborCells(cells, x, y);

        for (Cell neighborCell : neighborCells) {
          Point point2 = new Point(neighborCell.getX(), neighborCell.getY());

          Cell[][] copiedCells = Game.copyCells(cells);
          swiper.swipe(copiedCells, point1, point2);

          if (!matchesFinder.findMatches(copiedCells).isEmpty()) {
            foundMoves.add(new Pair<>(point1, point2));
          }
        }
      }
    }

    // Returns random move
    if (!foundMoves.isEmpty()) {
      int randomIdx = randomizer.generateFromRange(0, foundMoves.size() - 1);
      return foundMoves.get(randomIdx);
    }

    String errMsg = String.format(CAN_NOT_FIND_ANY_MOVE_FOR_BOT_GAME, game.toFullString());
    log.error(errMsg);
    throw new IllegalStateException(errMsg);
  }

}
