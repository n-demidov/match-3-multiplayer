package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Cell {

  private int type;
  private boolean cleared;

  @Deprecated
  private long owner;

  @JsonIgnore
  private final int x, y;

  public Cell(int type, int x, int y) {
    this.type = type;
    this.x = x;
    this.y = y;
  }

  @Deprecated
  public Cell(long owner, int type, int x, int y) {
    this.owner = owner;
    this.type = type;
    this.x = x;
    this.y = y;
  }

}
