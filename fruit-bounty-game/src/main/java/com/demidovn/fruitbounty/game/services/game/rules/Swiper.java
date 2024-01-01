package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Point;

public class Swiper {

  public void swipe(Cell[][] cells, Point point1, Point point2) {
    Cell cell1 = cells[point1.getX()][point1.getY()];
    Cell cell2 = cells[point2.getX()][point2.getY()];

    int cell1Type = cell1.getType();

    cell1.setType(cell2.getType());
    cell2.setType(cell1Type);
  }

}
