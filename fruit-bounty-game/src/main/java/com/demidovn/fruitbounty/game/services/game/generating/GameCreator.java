package com.demidovn.fruitbounty.game.services.game.generating;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.services.game.rules.MatchesFinder;
import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GameCreator {

  private final Random rand = new Random();
  private final MatchesFinder matchesFinder = new MatchesFinder();

  public Game createNewGame(List<Player> players, boolean isTutorial) {
    Game game = new Game();
    game.setTutorial(isTutorial);

    if (!game.isTutorial()) {
      randomlyMixPlayers(players);
    }
    game.setPlayers(players);
    setRandomCurrentPlayer(game);

    game.setBoard(createBoard(game));
    changeBoardIfTutorial(game);

    return game;
  }

  public int createRandomCellType() {
    return rand.nextInt(GameOptions.CELL_TYPES_COUNT) + 1;
  }

  private void randomlyMixPlayers(List<Player> players) {
    ArrayList<Player> copiedPlayersList = new ArrayList<>(players);
    players.clear();

    while (!copiedPlayersList.isEmpty()) {
      int randomIndex = rand.nextInt(copiedPlayersList.size());
      Player randomPlayer = copiedPlayersList.remove(randomIndex);

      players.add(randomPlayer);
    }
  }

  private void setRandomCurrentPlayer(Game game) {
    List<Player> players = game.getPlayers();
    int randomIndex = rand.nextInt(players.size());

    game.setCurrentPlayer(players.get(randomIndex));
  }

  private Board createBoard(Game game) {
    int counter = 0;
    Board board;

    do {
      board = createBoardInner(game);
      if (++counter >= 1_000) {
        log.error("Too many tries to create board, counter={}", counter);
      }
    } while (matchesFinder.findMatches(board.getCells()).size() > 0);

    return board;
  }

  private Board createBoardInner(Game game) {
    int boardWidth = getBoardWidth(game);
    int boardHeight = getBoardHeight(game);
    Cell[][] cells = new Cell[boardWidth][boardHeight];

    for (int x = 0; x < boardWidth; x++) {
      for (int y = 0; y < boardHeight; y++) {
        cells[x][y] = new Cell(createRandomCellType(), x, y);
      }
    }

    return new Board(cells);
  }

  private void changeBoardIfTutorial(Game game) {
    if (!game.isTutorial()) {
      return;
    }

    Cell[][] cells = game.getBoard().getCells();

    int type = cells[1][0].getType();
    cells[1][1].setType(type);

    type = cells[2][1].getType();
    cells[3][1].setType(type);
    cells[2][2].setType(type);
    cells[2][0].setType(type);

    type = cells[3][2].getType();
    cells[4][2].setType(type);
    cells[5][2].setType(type);
    cells[6][2].setType(type);

    type = cells[3][3].getType();
    cells[3][4].setType(type);
    cells[3][5].setType(type);
  }

  private int getBoardWidth(Game game) {
    if (game.isTutorial()) {
      return GameOptions.TUTORIAL_BOARD_WIDTH;
    } else {
      return GameOptions.BOARD_WIDTH;
    }
  }

  private int getBoardHeight(Game game) {
    if (game.isTutorial()) {
      return GameOptions.TUTORIAL_BOARD_HEIGHT;
    } else {
      return GameOptions.BOARD_HEIGHT;
    }
  }

}
