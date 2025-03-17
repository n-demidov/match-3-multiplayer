"use strict";

var CANVAS_ID = "game-canvas";
var CANVAS_CONTEXT = "2d";
var LEFT_TEXT_ALIGN = "left";

var FIRST_PLAYER_CELLS_COLOR = "green";
var SECOND_PLAYER_CELLS_COLOR = "blue";
var BOARD_GRID_COLOR = "black";

var MIN_CELLS_TO_MATCH = 3;

var BOUNCE_VAL = 2;

var ANIMATED_TEXT_ANIMATION_DURATION_MS = 1000;

var MAX_BUSY_TYPE_ANIMATION_OPACITY = 0.6;
var DELTA_BUSY_TYPE_OPACITY = 0.15;
var busyTypeAnimationTempVar;
var busyTypeAnimationTempVarDirect;

var CAPTURING_CELL_ANIMATION_DELTA = 0.2;
var CAPTURING_CELL_ANIMATION_MIN = CAPTURING_CELL_ANIMATION_DELTA;

var WIN_OR_DRAW_ADDS_CHANCE = 50;

var point1, point2;
var screenTouchWorking = false;

var fontsByLocale = {
  "en": '"Showcard Gothic"',
  "ru": '"Showcard gothic cyrillic"'
}
var FRUIT_IMG_SIZE = 50;
var FRUIT_IMG_MARGIN = 5;
var FRUIT_IMG_NUM = 9;
var CAPTURED_OPACITY_CELL = 0.25;
var cellSize;

var BOARD_X = 0;
var BOARD_Y = 0;
var boardWidth;
var boardHeight;

var imgGameScreen = new Image();
var fruitsImage = [];
var handImage = new Image();
var arrowHelperImage = new Image();
var imgWarning = new Image();
var imgDefeat = new Image();
var imgVictory = new Image();
var imgDraw = new Image();
var imgBtnNext = new Image();
var imgSurrender = new Image();
var imgUnknownUser = new Image();

var canvas;
var ctx;
var timerId;
var animation = {};
var story = {};
var swipeCellAnimation = {};
var game;
var capturedCellsAnimation;
var movesCounter;
var maxTimerProgressWidth;
var gameWindowMayBeClosed;

var isDefeat = false;

function initGameUi() {
  maxTimerProgressWidth = $('#time-progress').width();
  canvas = document.getElementById(CANVAS_ID);
  ctx = canvas.getContext(CANVAS_CONTEXT);

  $("#" + CANVAS_ID).on("touchstart ", canvasTouchStarted);
  $("#" + CANVAS_ID).on("touchend ", canvasTouchEnded);
  $("#" + CANVAS_ID).on("mousedown", canvasClicked);

  $('#player-surrender').on("click", surrenderClicked);
  $('#subwindow-close').on("click", onSubwindowClose);
  $('#subwindow-btn-next').on("click", onSubwindowClose);

  preloadGameSecondImages();
  setImagesOnTags();
}

function processGameStartedOperation(newGame) {
  window.game = newGame;
  $(".background").css("background-image", "url(" + imgGameScreen.src + ")");

  resetStoryIdx();

  canvas = document.getElementById(CANVAS_ID);
  cellSize = getCanvasWidth() / getCellsCount();
  boardWidth = cellSize * getCellsCount();
  boardHeight = boardWidth;

  canvas.width = getCanvasWidth();
  canvas.height = getCanvasHeight();

  resetGameInfo();
  if (isCurrentTurn(game)) {
    resetAnimatedText();
  }

  resetGameRequestUi();
  switchToGameWindow();
  resetProps(newGame);
  processGameChangedOperation(newGame);
}

function resetProps(game) {
  capturedCellsAnimation = {};
  for (var i = 0; i < game.players.length; i++) {
    capturedCellsAnimation[game.players[i].id] = {};
    capturedCellsAnimation[game.players[i].id].cells = [];
    capturedCellsAnimation[game.players[i].id].oldCells = [];
    capturedCellsAnimation[game.players[i].id].alpha = CAPTURING_CELL_ANIMATION_MIN;
    capturedCellsAnimation[game.players[i].id].deltaAlpha = CAPTURING_CELL_ANIMATION_DELTA;
    capturedCellsAnimation[game.players[i].id].started = Date.now();
  }

  movesCounter = 0;
  animation.busyCellsStart = 0;
  animation.validMovesStart = 0;
  resetPossibleCellsAnimation();
  resetBusyTypeAnimation();
}

function resetPossibleCellsAnimation() {
  animation.possibleCellsEnabled = false;
  animation.possibleCellsLastMs = Date.now();
}

function resetBusyTypeAnimation() {
  busyTypeAnimationTempVar = MAX_BUSY_TYPE_ANIMATION_OPACITY;
  busyTypeAnimationTempVarDirect = -1 * DELTA_BUSY_TYPE_OPACITY;
}

function resetGameInfo() {
  gameWindowMayBeClosed = false;
  $('#subwindow-background').hide();
  hideConfirmWindow();
}

function preloadGameSecondImages() {
  for (var i = 1; i <= FRUIT_IMG_NUM; i++) {
    fruitsImage[i] = new Image();
    fruitsImage[i].src = IMG_PREFIX + "/img/fruits/" + i + ".png";
  }

  handImage.src = IMG_PREFIX + "/img/hand.png";
  arrowHelperImage.src = IMG_PREFIX + "/img/arrow_helper.png";
  imgGameScreen.src = IMG_PREFIX + '/img/components/game-background.png';
  imgWarning.src = IMG_PREFIX + '/img/components/window_warning.' + browserLocale + '.png';
  imgDefeat.src = IMG_PREFIX + '/img/components/window_defeat.' + browserLocale + '.png';
  imgVictory.src = IMG_PREFIX + '/img/components/window_victory.' + browserLocale + '.png';
  imgDraw.src = IMG_PREFIX + '/img/components/window_draw.' + browserLocale + '.png';
  imgBtnNext.src = IMG_PREFIX + '/img/components/button_next.' + browserLocale + '.png';
  imgSurrender.src = IMG_PREFIX + '/img/components/surrender.' + browserLocale + '.png';
  imgUnknownUser.src = IMG_PREFIX + '/img/components/unknown_user.png';
}

function getFruitImageIdx(cell) {
  return cell.type;
}

function setImagesOnTags() {
  $('#player-surrender-img').attr('src', imgSurrender.src);
  $('#subwindow-btn-next').css("background-image", "url(" + imgBtnNext.src + ")");
  $('#warnwindow-container').css("background-image", "url(" + imgWarning.src + ")");
}

function processGameChangedOperation(newGame) {
  window.game = newGame;
  killGameTimer();
  movesCounter += 1;

  newGame.incomingTime = Date.now();

  resetStoryIdx();
  resetSelectedCells();
  resetSwipeCellAnimation();
  resetPossibleCellsAnimation();
  paintGame(newGame);

  timerId = setInterval(
    function() {
      // preparePossibleCellsAnimation(newGame);
      paintGame(newGame);
      incrementStoryIdx(newGame);
      incrementSwipeCellAnimation();
    },
    newGame.animationTimerIntervalMs);
}

function switchToGameWindow() {
  $("#lobby-window").hide();
  $("#game-window").show();
}

function showConfirmWindow(yesFunction, noFunction, text, showInput, initInputText) {
  $("#warnwindow-text").text(text);

  $("#warnwindow-yes").on("click", yesFunction);
  $("#warnwindow-no").on("click", noFunction);

  if (showInput) {
    $('#warnwindow-input').val(initInputText);
    $('#warnwindow-text').css('top', '304px');
    $("#warnwindow-input").show();
  } else {
    $('#warnwindow-text').css('top', '324px');
    $("#warnwindow-input").hide();
  }

  $("#warnwindow-background").show();
}

function hideConfirmWindow() {
  $("#warnwindow-background").hide();
}

function onSubwindowClose(e) {
  if (isDefeat) {
    showAdds();
  } else {
    showAdds(WIN_OR_DRAW_ADDS_CHANCE);
  }

  $(".background").css("background-image", "url(" + imgLobbyScreen.src + ")");
  $("#game-window").hide();
  $("#lobby-window").show();
}

function surrenderClicked() {
  showConfirmWindow(onSurrender, hideConfirmWindow, localize('concede-confirmation'), false, null);
}

function onSurrender() {
  var surrenderPayload = {
    type: SURRENDER_GAME_ACTION
  };
  sendGameAction(surrenderPayload);

  hideConfirmWindow();
}

function killGameTimer() {
  if (timerId != null) {
    clearInterval(timerId);
  }
}

function canvasClicked(e) {
  if (screenTouchWorking) {
    return;
  }

  var x = e.offsetX;
  var y = e.offsetY;

  gameBoardClicked(x, y);
}

function canvasTouchStarted(e) {
  screenTouchWorking = true;

  var x = e.originalEvent.changedTouches[0].clientX - $("#" + CANVAS_ID).offset().left;
  var y = e.originalEvent.changedTouches[0].clientY - $("#" + CANVAS_ID).offset().top;

  gameBoardClicked(x, y);
}

function canvasTouchEnded(e) {
  screenTouchWorking = true;

  var x = e.originalEvent.changedTouches[0].clientX - $("#" + CANVAS_ID).offset().left;
  var y = e.originalEvent.changedTouches[0].clientY - $("#" + CANVAS_ID).offset().top;

  gameBoardClicked(x, y);
}

function gameBoardClicked(x, y) {
  if (!(x >= BOARD_X && x < BOARD_X + boardWidth &&
      y >= BOARD_Y && y < BOARD_Y + boardHeight)) {
    return;
  }

  var game = getActualGame();
  if (game.finished) {
    return;
  }

  if (!isCurrentTurn(game)) {
    startAnimatedText(localize('opponentTurn'));
    return;
  } else {
    resetAnimatedText();
  }

  var xCellIndex = Math.floor(x / cellSize);
  var yCellIndex = Math.floor(y / cellSize);

  if (point1 === undefined) {
    point1 = {};
    point1.x = xCellIndex;
    point1.y = yCellIndex;
    return;
  } else if (point2 === undefined) {
    point2 = {};
    point2.x = xCellIndex;
    point2.y = yCellIndex;
  }

  if (!areCellsNeighbors(point1, point2)) {
    point1 = point2;
    resetPoint2();
    return;
  }

  var cells = game.board.cells;
  if (isMatchAfterMove(point1, point2, cells)) {
    startSwipeCellAnimation(true, point1, point2);
  } else {
    startSwipeCellAnimation(false, point1, point2);
  }

  var movePayload = {
    type: MOVE_GAME_ACTION,
    point1: point1,
    point2: point2
  };
  sendGameAction(movePayload);
  resetSelectedCells();
}

function resetSelectedCells() {
  resetPoint1();
  resetPoint2();
}

function resetPoint1() {
  point1 = undefined;
}

function resetPoint2() {
  point2 = undefined;
}

function startAnimatedText(text) {
  animation.animatedTextValue = text;
  animation.animatedTextStartedMs = Date.now();
}

function resetAnimatedText() {
  animation.animatedTextStartedMs = undefined;
  animation.animatedTextValue = undefined;
}

function sendGameAction(movePayload) {
  sendOperation(SEND_GAME_ACTION, movePayload);
}



function paintGame(game) {
  if (game == null) {
    return;
  }

  game = getActualGame();
  if (game.finished) {
    killGameTimer();
    gameWindowMayBeClosed = true;
    hideConfirmWindow();
  }

  canvas.width = getCanvasWidth();

  paintUi(game);
  paintPlayers(game);
  paintBoardBackground();
  paintBoardGrid(game);
  paintBoard(game);
  // paintPossibleCellsAnimation(game);
  if (!isCurrentTurn(game)) {
    hideBoard();
    resetSelectedCells();
  }
  paintWinner(game);

  paintPlayerChangedText();
  paintShuffleText();
  paintExtraMoveText();
  paintAnimatedText();
}

function paintUi(game) {
  $('#numCurrentRound').text(game.currentRound);
}

function paintPlayers(game) {
  var leftPlayerIndex;
  var rightPlayerIndex;
  for (var i = 0; i < game.players.length; i++) {
    var player = game.players[i];
    if (userInfo.id === player.id) {
      leftPlayerIndex = i;
    } else {
      rightPlayerIndex = i;
    }
  }

  paintPlayer(game.players[leftPlayerIndex], game, "left");
  paintPlayer(game.players[rightPlayerIndex], game, "right");
}

function paintPlayer(player, game, playerSide) {
  // Player's image
  var playerImage = $('#' + playerSide + '-pl-img');
  if (playerImage.attr('src') !== prepareServerImg(player.img)) {
    playerImage.attr('src', prepareServerImg(player.img));
  }

  // Player's name
  var playerName = $('#' + playerSide + '-pl-name');
  if (playerName.text() !== player.publicName) {
    playerName.text(player.publicName);
  }

  // Other player's params
  $('#' + playerSide + '-pl-score').text(player.pointsWhileGame);

  var movesLeft = '';
  if (player.movesInRound === 2) {
    movesLeft = '| |';
  } else if (player.movesInRound === 1) {
    movesLeft = '|';
  }

  $('#' + playerSide + '-pl-moves').text(movesLeft);
  $('#' + playerSide + '-pl-info').attr("data-original-title", concatGameStats(player));

  // If game is going on
  if (!game.finished) {
    // Active player
    if (player.id === game.currentPlayer.id) {
      $('#' + playerSide + '-pl-shadow').show();
    } else {
      $('#' + playerSide + '-pl-shadow').hide();
    }

    // Timer
    var moveTimeLeft = game.clientCurrentMoveTimeLeft - (Date.now() - game.incomingTime);
    var timerProgressWidth = maxTimerProgressWidth * moveTimeLeft / game.timePerMoveMs;
    $('#time-progress').width(timerProgressWidth);
  }

  if (game.finished) {
    $('#' + playerSide + '-pl-info').removeClass("player-active");
  }
}

function resetStoryIdx() {
  story.storyIdx = 0;
  story.storyIdxCounter = 0;
}

function startSwipeCellAnimation(success, point1, point2) {
  resetSwipeCellAnimation(success, point1, point2)
  swipeCellAnimation.enabled = true;
}

function resetSwipeCellAnimation(success, point1, point2) {
  swipeCellAnimation.enabled = false;
  swipeCellAnimation.idxCounter = 0;
  swipeCellAnimation.idxCounterMax = game.standardAnimationIter;

  swipeCellAnimation.success = success;
  swipeCellAnimation.point1 = point1;
  swipeCellAnimation.point2 = point2;
}

function incrementStoryIdx(game) {
  var gameStory = getActualGameStory();

  story.storyIdxCounter++;
  if (story.storyIdxCounter >= gameStory.storyIdxCounterMax) {
    story.storyIdxCounter = 0;

    if (story.storyIdx + 1 <= game.lastStories.length) {
      story.storyIdx++;
    }
  }
}

function incrementSwipeCellAnimation() {
  swipeCellAnimation.idxCounter++;

  if (swipeCellAnimation.idxCounter >= swipeCellAnimation.idxCounterMax) {
    resetSwipeCellAnimation();
  }
}


function getActualGameStory() {
  if (game.lastStories.length === 0 ||
      story.storyIdx >= game.lastStories.length) {
    return {};  //todo: make empty?
  }

  return game.lastStories[story.storyIdx];
}

function getActualGame() {
  var gameStory = getActualGameStory();
  if (isObjectEmpty(gameStory)) {
    return game;
  }

  return game.lastStories[story.storyIdx].gameState;
}

function getActualBoard(game) {
  var gameStory = getActualGameStory();

  if (isObjectEmpty(gameStory)) {
    return game.board.cells;
  }
  return gameStory.gameState.board.cells;
}


function paintBoardBackground() {
  ctx.fillStyle = "white";
  ctx.globalAlpha = 1;
  ctx.fillRect(BOARD_X, BOARD_Y, boardWidth, boardHeight);
}

function paintBoard(game) {
  var cells = getActualBoard(game);

  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (cell.cleared) {
        continue;
      }

      drawFruit(cell, game);

      // paint selected cells
      if (point1 !== undefined && cell.x === point1.x && cell.y === point1.y) {
        ctx.fillStyle = FIRST_PLAYER_CELLS_COLOR;

        ctx.globalAlpha = CAPTURED_OPACITY_CELL;
        ctx.fillRect(x * cellSize, y * cellSize + BOARD_Y, cellSize, cellSize);
        ctx.globalAlpha = 1;
      }
      if (point2 !== undefined && cell.x === point2.x && cell.y === point2.y) {
        ctx.fillStyle = SECOND_PLAYER_CELLS_COLOR;

        ctx.globalAlpha = CAPTURED_OPACITY_CELL;
        ctx.fillRect(x * cellSize, y * cellSize + BOARD_Y, cellSize, cellSize);
        ctx.globalAlpha = 1;
      }
    }
  }
}

function foundCell(cell, cells) {
  for(var i = 0; i < cells.length; i++) {
    if (cells[i].x === cell.x && cells[i].y === cell.y) {
      return true;
    }
  }
  return false;
}

function drawFruit(cell, game) {
  var initY = cell.y * cellSize + BOARD_Y;
  var x = cell.x * cellSize;
  var y = initY;

  var gameStory = getActualGameStory();
  if (swipeCellAnimation.enabled &&
      anyCoordsSame(cell, [swipeCellAnimation.point1, swipeCellAnimation.point2])) {
    if (swipeCellAnimation.success) {
      // NOOP
    } else {
      if (swipeCellAnimation.idxCounter <= 1) {
        var swipedPoint;
        if (anyCoordsSame(cell, [swipeCellAnimation.point1])) {
          swipedPoint = swipeCellAnimation.point2;
        } else {
          swipedPoint = swipeCellAnimation.point1;
        }

        initY = swipedPoint.y * cellSize + BOARD_Y;
        x = swipedPoint.x * cellSize;
        y = initY;
      } else if (swipeCellAnimation.idxCounter === swipeCellAnimation.idxCounterMax - 2) {
        // NOOP
      } else if (swipeCellAnimation.idxCounter === swipeCellAnimation.idxCounterMax - 1) {
        initY -= BOUNCE_VAL;
        y -= BOUNCE_VAL;
      }
    }
  } else if (gameStory.type === 'DROP_CELLS' &&
      foundCell(cell, gameStory.specialCells)) {
    y += story.storyIdxCounter * 0.25 * cellSize;

    var targetY = initY + findDepthBelow(cell, gameStory.gameState.board.cells) * cellSize;
    if (y > targetY) {
      y = targetY;
      cell.bounceIdx = updateBounceIdx(cell.bounceIdx);
    }

    if (cell.bounceIdx === 0) {
      initY -= BOUNCE_VAL;
      y -= BOUNCE_VAL;
    }
  } else if (gameStory.type === 'CREATED_CELLS' &&
      foundCell(cell, gameStory.specialCells)) {
    y += story.storyIdxCounter * 0.25 * cellSize;
    y += -findColumnDepth(cell, gameStory.specialCells) * cellSize;

    if (y > initY) {
      y = initY;
      cell.bounceIdx = updateBounceIdx(cell.bounceIdx);
    }

    if (cell.bounceIdx === 0) {
      initY -= BOUNCE_VAL;
      y -= BOUNCE_VAL;
    }
  }

  ctx.drawImage(
    fruitsImage[getFruitImageIdx(cell)],
    0, 0, FRUIT_IMG_SIZE, FRUIT_IMG_SIZE,
    x + FRUIT_IMG_MARGIN, y + FRUIT_IMG_MARGIN, cellSize - FRUIT_IMG_MARGIN * 2, cellSize - FRUIT_IMG_MARGIN * 2);
}

function updateBounceIdx(bounceIdx) {
  if (bounceIdx === undefined) {
    return 0;
  }
  return ++bounceIdx;
}

// function paintPossibleCellsAnimation(game) {
//   if (game.finished || !isCurrentTurn(game) || !animation.possibleCellsEnabled) {
//     return;
//   }
//
//   var validMoveCells = findValidMoveCells(userInfo.id, game);
//   var cells = getActualBoard(game);
//   for (var x = 0; x < cells.length; x++) {
//     var row = cells[x];
//
//     for (var y = 0; y < row.length; y++) {
//       var cell = row[y];
//       if (!isMoveValid(x, y, validMoveCells)) {
//         continue;
//       }
//
//       // Fill background
//       fillBackgroundCell(cell);
//
//       // Paint zoomed fruit
//       var fruitImgCoords = getImageCoordinates(cell);
//       ctx.drawImage(
//         fruitsImage,
//         fruitImgCoords.x, fruitImgCoords.y, FRUIT_IMG_SIZE, FRUIT_IMG_SIZE,
//         x * cellSize - animation.possibleCellsValue,
//         y * cellSize + BOARD_Y - animation.possibleCellsValue,
//         cellSize + animation.possibleCellsValue * 2,
//         cellSize + animation.possibleCellsValue * 2);
//     }
//   }
//
//   // Change animation values
//   if (animation.possibleCellsValue <= POSSIBLE_CELLS_ANIMATION_VALUE_MIN) {
//     animation.possibleCellsSpeed = Math.abs(animation.possibleCellsSpeed);
//   } else if (animation.possibleCellsValue >= POSSIBLE_CELLS_ANIMATION_VALUE_MAX) {
//     animation.possibleCellsSpeed = -Math.abs(animation.possibleCellsSpeed);
//   }
//   animation.possibleCellsValue += animation.possibleCellsSpeed;
// }

function paintBoardGrid(game) {
  ctx.fillStyle = BOARD_GRID_COLOR;

  var cells = getActualBoard(game);
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];

    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      // Right cell line
      ctx.beginPath();
      ctx.moveTo(x * cellSize + cellSize, y * cellSize + BOARD_Y);
      ctx.lineTo(x * cellSize + cellSize, y * cellSize + BOARD_Y + cellSize);
      ctx.stroke();

      // Bottom cell line
      ctx.beginPath();
      ctx.moveTo(x * cellSize, y * cellSize + BOARD_Y + cellSize);
      ctx.lineTo(x * cellSize + cellSize, y * cellSize + BOARD_Y + cellSize);
      ctx.stroke();
    }
  }

  // Top border
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.lineTo(BOARD_X + boardWidth, 0);
  ctx.stroke();

  // Left border
  ctx.beginPath();
  ctx.moveTo(0, 0);
  ctx.lineTo(0, BOARD_Y + boardHeight);
  ctx.stroke();
}

function hideBoard() {
  darkenCells([], false, 0.2);
}

function paintWinner(game) {
  if (!game.finished) {
    return;
  }

  var gameResult;
  if (game.winner) {
    if (game.winner.id === userInfo.id) {
      gameResult = 'win';
    } else {
      gameResult = 'defeat';
    }
  } else {
    gameResult = "draw";
  }

  // Points while game
  var selfPlayer = findSelfPlayer();
  var opponentPlayer = findOpponentPlayer();
  var text = selfPlayer.pointsWhileGame + "/" + opponentPlayer.pointsWhileGame;
  $('#subwindow-cells').text(text);

  // Game points
  var addedScore = selfPlayer.addedScore;
  if (addedScore > -1) {
    addedScore = "+" + addedScore;
  }
  text = localize('score') + ": " + addedScore;
  $('#subwindow-points').text(text);

  // Set player images
  $('#subwindow-left-pl-img').attr('src', $('#left-pl-img').attr('src'));
  $('#subwindow-right-pl-img').attr('src', $('#right-pl-img').attr('src'));

  // Set player names
  $('#subwindow-left-pl-name').text($('#left-pl-name').text());
  $('#subwindow-right-pl-name').text($('#right-pl-name').text());

  // Set background image
  if (gameResult === "win") {
    $('#subwindow-container').css("background-image", "url(" + imgVictory.src + ")");
    isDefeat = false;
  } else if (gameResult === "defeat") {
    $('#subwindow-container').css("background-image", "url(" + imgDefeat.src + ")");
    isDefeat = true;
  } else {
    $('#subwindow-container').css("background-image", "url(" + imgDraw.src + ")");
    isDefeat = true;
  }

  $('#subwindow-background').show();
}


function paintPlayerChangedText() {
  var gameStory = getActualGameStory();
  if (gameStory.type !== 'PLAYER_CHANGED') {
    return;
  }

  var s1;
  if (gameStory.newRound) {
    if (game.currentRound === game.roundsNum) {
      s1 = localize('finalRound');
    } else {
      s1 = localize('round') + ' ' + game.currentRound;
    }
  }

  var s2;
  if (isCurrentTurn(game)) {
    s2 = localize('yourTurn');
  } else {
    s2 = localize('opponentTurn');
  }

  if (s1 === undefined) {
    paintShortText(s2);
  } else {
    paintShortText(s1, s2);
  }

  resetAnimatedText();
}

function paintShuffleText() {
  var gameStory = getActualGameStory();
  if (gameStory.type !== 'RECREATE_BOARD') {
    return;
  }

  paintShortText(localize('noMoves'));
  resetAnimatedText();
}

function paintExtraMoveText() {
  var gameStory = getActualGameStory();
  if (!gameStory.extraMove) {
    return;
  }

  if (!story || story.storyIdxCounter !== 0) {
    return;
  }

  resetAnimatedText();
  startAnimatedText(localize('extraMove'));
}

function paintAnimatedText() {
  if (animation.animatedTextStartedMs === undefined) {
    return;
  }

  if (Date.now() - animation.animatedTextStartedMs > ANIMATED_TEXT_ANIMATION_DURATION_MS) {
    resetAnimatedText();
    return;
  }

  paintShortText(animation.animatedTextValue);
}

function paintShortText(text, text2) {
  var twoLines = text2 !== undefined;
  var yMargin = cellSize;

  var cells = game.board.cells;
  var centerX = cells[0].length * cellSize / 2;
  var centerY = cells.length * cellSize / 2 - cellSize / 2;

  if (twoLines) {
    paintTextFrame(centerY, yMargin, 2);
  } else {
    paintTextFrame(centerY, yMargin, 1);
  }

  if (twoLines) {
    paintStrokedText(text, centerX, centerY - yMargin / 2);
    paintStrokedText(text2, centerX, centerY + yMargin / 2);
  } else {
    paintStrokedText(text, centerX, centerY);
  }
}

function paintTextFrame(y, yMargin, linesNum) {
  var opacity = 0.6;
  var rowHeight = yMargin;

  ctx.fillStyle = "rgba(0, 0, 0, " + opacity + ")";
  var y2LineMargin = yMargin * (linesNum - 1) / 2;
  ctx.fillRect(0, y - rowHeight - y2LineMargin, getCanvasWidth(), rowHeight + yMargin * 2/3 + y2LineMargin * 2);
}

function paintStrokedText(text, x, y) {
  ctx.textAlign = "center";
  ctx.font = getCanvasTipsParams().fontSize + ' ' + fontsByLocale[browserLocale];
  ctx.strokeStyle = 'black';
  ctx.lineWidth = getCanvasTipsParams().lineWidth;
  ctx.strokeText(text, x, y);

  ctx.fillStyle = 'white';
  ctx.fillText(text, x, y);
}

function darkenCells(exceptCells, excludeCapturedCells, opacity) {
  var cells = game.board.cells;
  for (var x = 0; x < cells.length; x++) {
    var row = cells[x];
    for (var y = 0; y < row.length; y++) {
      var cell = row[y];

      if (exceptCells.includes(cell)) {
        continue;
      }

      if (excludeCapturedCells && cell.owner === userInfo.id) {
        continue;
      }

      ctx.fillStyle = "rgba(0, 0, 0, " + opacity + ")";
      ctx.fillRect(cell.x * cellSize, cell.y * cellSize, cellSize, cellSize);
    }
  }
}

function findCenterRange(x, y, cells) {
  return Math.abs(cells.length / 2 - x) +
    Math.abs(cells.length / 2 - y);
}

function areCellsNeighbors(point1, point2) {
  if (Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y) !== 1) {
    return false;
  }

  return true;
}

function anyCoordsSame(point1, points) {
  for (var i = 0; i < points.length; i++) {
    if (point1.x === points[i].x && point1.y === points[i].y) {
      return true;
    }
  }

  return false;
}

function findDepthBelow(cell, cells) {
  var initY = cell.y;
  var x = cell.x;

  var result = 0;
  for (var y = initY + 1; y < cells[x].length; y++) {
    if (cells[x][y].cleared) {
      result++;
    }
  }
  return result;
}

function findColumnDepth(cell, specialCells) {
  var x = cell.x;
  var result = 0;

  for (var i = 0; i < specialCells.length; i++) {
    var specialCell = specialCells[i];
    if (specialCell.x === x) {
      result = Math.max(specialCell.y, result);
    }
  }
  return result + 1;
}

function isCurrentTurn(game) {
  return game.currentPlayer && userInfo.id === game.currentPlayer.id;
}

function findOpponentId() {
  // Works only for 2 players in game.
  if (game.players[0].id === userInfo.id) {
    return game.players[1].id;
  } else {
    return game.players[0].id;
  }
}

function findSelfPlayer() {
  // Works only for 2 players in game.
  if (game.players[0].id === userInfo.id) {
    return game.players[0];
  } else {
    return game.players[1];
  }
}

function findOpponentPlayer() {
  // Works only for 2 players in game.
  if (game.players[0].id === findOpponentId()) {
    return game.players[0];
  } else {
    return game.players[1];
  }
}

// Match Logic
function isMatchAfterMove(point1, point2, cells) {
  var copiedCells = JSON.parse(JSON.stringify(cells));

  swipeCells(point1, point2, copiedCells);

  return findMatches(copiedCells).size > 0;
}

function swipeCells(point1, point2, cells) {
  var cell1 = cells[point1.x][point1.y];
  var cell2 = cells[point2.x][point2.y];

  var cell1Type = cell1.type;
  cell1.type = cell2.type;
  cell2.type = cell1Type;

  var cell1Cleared = cell1.cleared;
  cell1.cleared = cell2.cleared;
  cell2.cleared = cell1Cleared;
}

function findMatches(cells) {
  var result = new Set();

  innerFindMatches(cells, true).forEach(result.add, result);
  innerFindMatches(cells, false).forEach(result.add, result);

  return result;
}

function innerFindMatches(cells, type) {
  var result = new Set();

  for (var x = 0; x < cells.length; x++) {
    var stack = [];
    for (var y = 0; y < cells[x].length; y++) {
      var cell = type ? cells[x][y] : cells[y][x];

      var top = stack[stack.length - 1];
      if (y === 0 || cell.type !== top.type) {
        if (stack.length >= MIN_CELLS_TO_MATCH) {
          // result.addAll(stack);
          stack.forEach(result.add, result);
        }
        stack = [];
      }
      stack.push(cell);
    }

    if (stack.length >= MIN_CELLS_TO_MATCH) {
      // result.addAll(stack);
      stack.forEach(result.add, result);
    }
  }

  return result;
}


function getCanvasWidth() {
  var chatOutputScrolling = document.getElementById("chat-output-scrolling");
  return chatOutputScrolling.offsetWidth;
}

function getCanvasHeight() {
  return getCanvasWidth();
}

function getCellsCount() {
  return game.board.cells.length;
}

function isObjectEmpty(value) {
  return Object.keys(value).length === 0;
}
