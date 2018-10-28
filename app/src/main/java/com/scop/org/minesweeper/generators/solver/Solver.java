package com.scop.org.minesweeper.generators.solver;

import com.scop.org.minesweeper.control.MainLogic;
import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.scop.org.minesweeper.elements.Tile.Status.*;

public abstract class Solver {
	private static MainLogic logic;
	protected static List<Tile> logicTiles;
	protected static Sketch sketch;

	public static void setSolver(MainLogic logic, Sketch sketch) {
		Solver.logic = logic;
		Solver.logicTiles = logic.getGrid().getGrid();
		Solver.sketch = sketch;
	}

	public abstract boolean analyze();

	public static void copyRevealed(){
		List<Tile> sketchTiles = sketch.getTiles();

		for (int i=0; i<logicTiles.size(); i++) {
			Tile tile = logicTiles.get(i);
			Tile sTile = sketchTiles.get(i);

			if (tile.getStatus() != COVERED && sTile.getStatus() == COVERED ) {
				int bombsNear = sTile.getBombsNear();
				sTile.setStatus(GridUtils.getTileStatus(bombsNear));

				if (bombsNear>0) sketch.addNumbered(i);
				else sketch.addUncovered(i);
			}
		}
	}

	protected void markBomb(int idx){
		if (logicTiles.get(idx).getStatus() == FLAG) return;
		logicTiles.get(idx).setStatus(FLAG);

		List<Tile> sketchTiles = sketch.getTiles();

		Tile tile = sketchTiles.get(idx);
		tile.setStatus(FLAG);
		tile.setCustomFlag('X');
		tile.defuseBomb();

		GridUtils.getNeighborsIdx(sketch.getGrid(), tile).stream()
				.peek(i-> {
					Tile t = sketchTiles.get(i);
					t.doesntHaveBombNear();
					if (t.isNumberVisible()) {
						int bombs = t.getBombsNear();
						t.setStatus(GridUtils.getTileStatus(bombs));
						if (bombs == 0) sketch.removeNumbered(i);
					}

				})
				.map(sketchTiles::get)
				.filter(t -> t.getBombsNear() == 0)
				.forEach(t -> t.setCustomFlag('0'));
	}

	protected void reveal(int idx){
		Tile t = logicTiles.get(idx);
		logic.reveal(t);

		tileUpdate(idx);
	}

	private void tileUpdate(int idx){
		Tile sTile = sketch.getTile(idx);
		if (sTile.isCovered()) {

			Tile tile = logicTiles.get(idx);

			if (tile.getStatus()==A0) {
				sketch.addUncovered(idx);
				sTile.setStatus(A0);
				for (int idxChild : GridUtils.getNeighborsIdx(sketch.getGrid(), sTile)){
					tileUpdate(idxChild);
				}

			} else {
				sketch.addNumbered(idx);
				sTile.setStatus(GridUtils.getTileStatus(sTile.getBombsNear()));
			}
		}
	}

	public static Set<Integer> getLeftBombs(){
		Set<Integer> li = new HashSet<>();

		for (int i : sketch.getNumbered()) {
			Tile sTile = sketch.getTiles().get(i);

			List<Integer> neigh = GridUtils.getNeighborsIdx(sketch.getGrid(), sTile);
			for (int n : neigh) {
				Tile t = logicTiles.get(n);
				if (t.getStatus() == COVERED && t.hasBomb())
					li.add(n);
			}
		}
		if (li.size() == 0) {
			for (int i = 0; i < logicTiles.size(); i++) {
				Tile t = logicTiles.get(i);
				if (t.getStatus() == COVERED && t.hasBomb())
					li.add(i);
			}
			if (li.size() > 0) {
				return null;
			}
		}
		return li;
	}
}
