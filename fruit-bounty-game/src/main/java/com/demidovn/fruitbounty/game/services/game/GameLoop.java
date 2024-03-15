package com.demidovn.fruitbounty.game.services.game;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.model.GameProcessingContext;
import com.demidovn.fruitbounty.game.services.DefaultGameEventsSubscriptions;
import com.demidovn.fruitbounty.game.services.FruitBountyGameFacade;
import com.demidovn.fruitbounty.game.services.game.rules.BoardOperations;
import com.demidovn.fruitbounty.game.services.game.rules.CellsDropper;
import com.demidovn.fruitbounty.game.services.game.rules.CellsDropper.DroppedResult;
import com.demidovn.fruitbounty.game.services.game.rules.MatchesFinder;
import com.demidovn.fruitbounty.game.services.game.rules.Swiper;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.gameapi.model.GameStory;
import com.demidovn.fruitbounty.gameapi.model.GameStoryType;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.BotService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameLoop {

  private static final int MIN_GAME_ACTIONS_IN_QUEUE_TO_WARNING = 20;

  @Autowired
  private FruitBountyGameFacade gameFacade;

  @Autowired
  private DefaultGameEventsSubscriptions gameEventsSubscriptions;

  @Autowired
  private BotService botService;

  @Autowired
  private GameStoryCreator gameStoryCreator;

  private static final BoardOperations boardOperations = new BoardOperations();
  private static final GameRules gameRules = new GameRules();
  private static final MatchesFinder matchesFinder = new MatchesFinder();
  private static final Swiper swiper = new Swiper();
  private static final CellsDropper cellsDropper = new CellsDropper();

  @Scheduled(fixedDelayString = GameOptions.GAME_LOOP_SCHEDULE_DELAY)
  public void gameLoop() {
    List<Game> finishedGames = new ArrayList<>();

    for (Game game : gameFacade.getAllGames()) {
      processGame(game);

      if (game.isFinished()) {
        finishedGames.add(game);
      }
    }

    gameFacade.gamesFinished(finishedGames);
  }

  public void processGameAction(GameAction gameAction, GameProcessingContext context) {
    if (gameAction.getGame().isFinished()) {
      return;
    }

    if (gameAction.getType() == GameActionType.Move) {
      processMoveAction(gameAction, context);
//    } else if (gameAction.getType() == GameActionType.Surrender) {
//      processSurrenderAction(gameAction, context);
    } else {
      throw new IllegalArgumentException(String.format(
          "Unknown gameActionType=%s", gameAction.getType()));
    }
  }

  private void processGame(Game game) {
    try {
      doProcessGame(game);
    } catch (Exception e) {
      log.error("error while process game", e);
    }
  }

  private void doProcessGame(Game game) {
    GameProcessingContext processContext = new GameProcessingContext();

    int i = 0;
    GameAction gameAction;
    while ((gameAction = game.getGameActions().poll()) != null) {
      processGameAction(gameAction, processContext);

      i++;
      if (i == MIN_GAME_ACTIONS_IN_QUEUE_TO_WARNING) {
        log.warn("{} iteration of processing game-actions, game={}", i, game);
      }
    }

    if (i > MIN_GAME_ACTIONS_IN_QUEUE_TO_WARNING) {
      log.warn("{} game-actions have been processed, game={}", i, game);
    }

    checkForCurrentMoveExpiration(game, processContext);
    notifyIfGameChanged(game, processContext);
    botService.actionIfBot(game);
  }

  private void processMoveAction(GameAction gameAction, GameProcessingContext context) {
    Game game = gameAction.getGame();
    Player currentPlayer = game.getCurrentPlayer();

    if (gameRules.isMoveValid(gameAction)) {
      currentPlayer.resetConsecutivelyMissedMoves();

      swiper.swipe(game.getBoard().getCells(), gameAction.getPoint1(), gameAction.getPoint2());
      game.getLastStories().add(gameStoryCreator.create(GameStoryType.SWIPE, game.deepCopy()));

      boolean wasExtraMove = false;
      int counter = 0;
      do {
        CleanMatchesResult matchesCountResult = cleanMatches(gameAction);
        currentPlayer.setPointsWhileGame(currentPlayer.getPointsWhileGame() + matchesCountResult.allMatchesCount);
        if (counter == 0 && matchesCountResult.wasExtraMove) {
          wasExtraMove = true;
        }
        game.getLastStories().add(gameStoryCreator.create(GameStoryType.MATCH, game.deepCopy(), wasExtraMove));

        Game copiedState = game.deepCopy();
        DroppedResult droppedResult = cellsDropper.dropCells(game.getBoard().getCells());
        if (droppedResult.droppedCells.size() > 0) {
          game.getLastStories().add(
              gameStoryCreator.create(GameStoryType.DROP_CELLS, copiedState, droppedResult.droppedCells, droppedResult.dropDepthMax));
        }

        List<Cell> createdCells = boardOperations.recreateClearedCells(game.getBoard().getCells());
        int recreationDepth = findRecreationDepth(createdCells);
        game.getLastStories().add(
            gameStoryCreator.create(GameStoryType.CREATED_CELLS, game.deepCopy(), createdCells, recreationDepth));
        counter++;
      } while (!matchesFinder.findMatches(gameAction.getGame().getBoard().getCells()).isEmpty());

      Game copiedState = game.deepCopy();
      boolean updated = boardOperations.recreateCellsIfNoMoves(game.getBoard());
      if (updated) {
        game.getLastStories().add(gameStoryCreator.create(GameStoryType.RECREATE_BOARD, copiedState));
      }

      if (!wasExtraMove) {
        currentPlayer.decreaseMovesInRound();
      }
      gameRules.switchCurrentPlayer(game);

      int totalAnimationTimeMs = sumTotalAnimationTime(game);
      game.setTotalAnimationTimeMs(totalAnimationTimeMs);
      game.setCurrentMoveStarted(game.getCurrentMoveStarted() + totalAnimationTimeMs);

      context.markGameChanged();
    }
  }

  private CleanMatchesResult cleanMatches(GameAction gameAction) {
    Set<Cell> matches = matchesFinder.findMatches(gameAction.getGame().getBoard().getCells());
    for (Cell cell : matches) {
      cell.setCleared(true);
    }

    return new CleanMatchesResult(matches.size(), wasExtraMove(matches));
  }

  private boolean wasExtraMove(Set<Cell> matches) {
    Map<Integer, Long> countsByType = matches.stream()
        .collect(Collectors.groupingBy(Cell::getType, Collectors.counting()));
    boolean wasExtraMove = countsByType.values().stream()
        .anyMatch(v -> v >= GameOptions.GAME_MATCHED_NUM_FOR_ADDITIONAL_MOVE);

    return wasExtraMove;
  }

  private int sumTotalAnimationTime(Game game) {
    return game.getLastStories().stream()
        .mapToInt(g -> g.getStoryIdxCounterMax() * game.getAnimationTimerIntervalMs()).sum();
  }

  private int findRecreationDepth(List<Cell> specialCells) {
    Map<Integer, Long> countsByX = specialCells
        .stream()
        .collect(Collectors.groupingBy(Cell::getX, Collectors.counting()));

    return countsByX.values().stream().max(Long::compareTo).orElse(0L).intValue();
  }

  private void checkForCurrentMoveExpiration(Game game, GameProcessingContext processContext) {
    if (game.isFinished()) {
      return;
    }

    if (isCurrentMoveExpired(game)) {
      Player currentPlayer = game.getCurrentPlayer();

      currentPlayer.setMovesInRound(0);
      currentPlayer.incrementMissedMoves();

      gameRules.switchCurrentPlayer(game);

      processContext.markGameChanged();
    }
  }

  private boolean isCurrentMoveExpired(Game game) {
    return
      game.getCurrentMoveStarted() + game.getTimePerMoveMs() < Instant.now().toEpochMilli();
  }

  private void notifyIfGameChanged(Game game, GameProcessingContext processContext) {
    if (processContext.isGameChanged()) {
      gameEventsSubscriptions.notifyGameChanged(game);
    }
  }

  @AllArgsConstructor
  @Getter
  private static class CleanMatchesResult {
    private int allMatchesCount;
    private boolean wasExtraMove;

  }

}
