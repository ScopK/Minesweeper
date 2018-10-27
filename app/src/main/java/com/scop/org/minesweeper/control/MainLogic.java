package com.scop.org.minesweeper.control;

import com.scop.org.minesweeper.utils.GridUtils;
import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;

import java.util.function.Consumer;

import static com.scop.org.minesweeper.elements.Tile.Status.*;

public class MainLogic {
	public enum GameStatus {
		PLAYING, WIN, LOSE
	}

	private Grid grid;
	private GameStatus status = GameStatus.PLAYING;
	private int flaggedBombs = 0;
	private int correctFlaggedBombs = 0;
	private int revealedTiles;
	private Consumer<Boolean> finishEvent = null;

	public MainLogic(Grid grid){
		this.grid = grid;
		this.revealedTiles = 0;
	}

	public void mainAction(Tile tile){
		if (isGameOver()) return;

		switch (tile.getStatus()){
			case COVERED:
				tile.setStatus(FLAG);

				GridUtils.getNeighbors(grid, tile).forEach(Tile::addFlaggedNear);

				flaggedBombs++;
				if (tile.hasBomb()){
					correctFlaggedBombs++;
				}
				checkWin();
				break;
			case FLAG:
				tile.setStatus(COVERED);

				GridUtils.getNeighbors(grid, tile).forEach(Tile::removeFlaggedNear);

				flaggedBombs--;
				if (tile.hasBomb()){
					correctFlaggedBombs--;
				}
				checkWin();
				break;

			case A0:
				break;

			default:
				boolean executeMassReveal = false;

				switch (Settings.getInstance().getDiscoveryMode()) {
					case Settings.EASY:
						executeMassReveal = tile.getFlaggedNear() == tile.getBombsNear();
						break;
					case Settings.NORMAL:
						executeMassReveal = tile.getFlaggedNear() >= tile.getBombsNear();
						break;
					case Settings.HARD:
						executeMassReveal = true;
						break;
					case Settings.DISABLED:
						break;
				}
				if (executeMassReveal) {
					GridUtils.getNeighbors(grid, tile).stream()
							.filter(t-> t.getStatus() == COVERED)
							.forEach(this::reveal);
				}
				break;
		}

	}

	public void alternativeAction(Tile tile){
		if (isGameOver()) return;

		switch (tile.getStatus()){
			case FLAG:
				GridUtils.getNeighbors(grid, tile).forEach(Tile::removeFlaggedNear);

				flaggedBombs--;
				if (tile.hasBomb()){
					correctFlaggedBombs--;
				}
				checkWin();

			case COVERED:
				reveal(tile);
				break;
		}
	}

	public void reveal(Tile tile){
		revealedTiles++;
		if (tile.hasBomb()){
			tile.setStatus(BOMB_FINAL);
			gameOver();
		} else {
			switch (tile.getBombsNear()) {
				case 0:
					tile.setStatus(A0);
					GridUtils.getNeighbors(grid, tile).stream()
							.filter(t-> t.getStatus() == COVERED)
							.forEach(this::reveal);
					break;
				default:
					tile.setStatus(
							GridUtils.getTileStatus(tile.getBombsNear())
					);
					break;
			}
		}

	}

	public void checkWin(){
		if (correctFlaggedBombs==grid.getBombs() && correctFlaggedBombs==flaggedBombs){
			gameWin();
		}
	}

	public void gameOver(){
		if (status == GameStatus.LOSE) return;
		status = GameStatus.LOSE;

		grid.getGrid().stream()
				.filter(t -> t.hasBomb())
				.forEach(t-> {
					if (t.getStatus() == FLAG) {
						t.setStatus(FLAG_FAIL);
					} else if (t.getStatus() != BOMB_FINAL){
						t.setStatus(BOMB);
					}
				});
		if (finishEvent!=null) finishEvent.accept(false);
	}

	public void gameWin(){
		status = GameStatus.WIN;
		if (finishEvent!=null) finishEvent.accept(true);
	}

	public boolean isGameOver(){
		return status == GameStatus.WIN || status == GameStatus.LOSE;
	}

	public GameStatus getStatus() {
		return status;
	}

	public int getTotalTiles(){
		return grid.getGrid().size();
	}

	public int getRevisedTiles(){
		return revealedTiles + flaggedBombs;
	}

	public int getFlaggedBombs() {
		return flaggedBombs;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setFinishEvent(Consumer<Boolean> finishEvent) {
		this.finishEvent = finishEvent;
	}

	public void addRevealedTiles(){
		revealedTiles++;
	}

	public void addFlaggedBombs(){
		flaggedBombs++;
	}

	public void addCorrectFlaggedBombs(){
		correctFlaggedBombs++;
	}
}
