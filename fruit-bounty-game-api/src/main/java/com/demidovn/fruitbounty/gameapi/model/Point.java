package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Point {

  @JsonIgnore
  private final int x, y;

}
