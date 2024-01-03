package com.demidovn.fruitbounty.gameapi.model;

import lombok.Data;

@Data
public class GameStory {
  private Game gameState;

  public GameStory(Game game) {
    this.gameState = game.deepCopy();
  }
}
