package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // todo: remove it later?
@AllArgsConstructor
@Data
public class Game {
  private Board board;
  private int roundsNum;
  private int currentRound;
  private int turnsCount;
  private boolean isTutorial;
  private List<Player> players;
  private Player currentPlayer;

  private long timePerMoveMs;
  @JsonIgnore
  private long currentMoveStarted;
  private long clientCurrentMoveTimeLeft;  // Only for client

  private boolean isFinished;
  private Player winner;
  private List<GameStory> lastStories = new ArrayList<>();
  @JsonIgnore
  private int totalAnimationTimeMs;
  private int animationTimerIntervalMs;

  @JsonIgnore
  private final Queue<GameAction> gameActions = new ConcurrentLinkedQueue<>();

  public static Game copy(Game fromGame) {
    Cell[][] copiedCells = copyCells(fromGame.getBoard().getCells());

    return new Game(
        new Board(copiedCells),
        fromGame.roundsNum,
        fromGame.currentRound,
        fromGame.turnsCount,
        fromGame.isTutorial,
        fromGame.players.stream().map(Player::copyPlayer).collect(Collectors.toList()),
        Player.copyPlayer(fromGame.currentPlayer),
        fromGame.timePerMoveMs,
        fromGame.currentMoveStarted,
        fromGame.clientCurrentMoveTimeLeft,
        fromGame.isFinished,
        Player.copyPlayer(fromGame.winner),
        Collections.emptyList(),
        fromGame.totalAnimationTimeMs,
        fromGame.animationTimerIntervalMs
    );
  }

  public static Cell[][] copyCells(Cell[][] sourceCells) {
    Cell[][] copiedCells = new Cell[sourceCells.length][sourceCells[0].length];

    for (int x = 0; x < sourceCells.length; x++) {
      for (int y = 0; y < sourceCells[0].length; y++) {
        Cell cell = sourceCells[x][y];
        copiedCells[x][y] = new Cell(cell.getType(), cell.isCleared(), cell.getOwner(),
            cell.getX(), cell.getY());
      }
    }

    return copiedCells;
  }

  public void setCurrentPlayer(Player player) {
    this.currentPlayer = player;
    this.currentMoveStarted = Instant.now().toEpochMilli();
    turnsCount++;
  }

  public Player findPlayer(long playerId) {
    for (Player player : players) {
      if (player.getId() == playerId) {
        return player;
      }
    }

    throw new IllegalStateException(String.format(
      "Can't find player by id '%s' in game=%s",
      playerId,
      this
    ));
  }

  public Game deepCopy() {
    return copy(this);
  }

  @Override
  public String toString() {
    return "Game{" +
            "players=" + players +
            ", currentPlayer=" + currentPlayer +
            ", isTutorial=" + isTutorial +
            ", timePerMoveMs=" + timePerMoveMs +
            ", currentMoveStarted=" + currentMoveStarted +
            ", clientCurrentMoveTimeLeft=" + clientCurrentMoveTimeLeft +
            ", isFinished=" + isFinished +
            ", winner=" + winner +
            '}';
  }

  public String toFullString() {
    return "Game{" +
            "board=" + board +
            ", turnsCount=" + turnsCount +
            ", isTutorial=" + isTutorial +
            ", players=" + players +
            ", currentPlayer=" + currentPlayer +
            ", timePerMoveMs=" + timePerMoveMs +
            ", currentMoveStarted=" + currentMoveStarted +
            ", clientCurrentMoveTimeLeft=" + clientCurrentMoveTimeLeft +
            ", isFinished=" + isFinished +
            ", winner=" + winner +
            '}';
  }
}
