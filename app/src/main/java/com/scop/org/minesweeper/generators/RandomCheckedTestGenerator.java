package com.scop.org.minesweeper.generators;

import com.scop.org.minesweeper.control.MainLogic;
import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.generators.solver.BasicSolver;
import com.scop.org.minesweeper.generators.solver.DeepSolver;
import com.scop.org.minesweeper.generators.solver.PossibleSolver;
import com.scop.org.minesweeper.generators.solver.Sketch;
import com.scop.org.minesweeper.generators.solver.Solver;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomCheckedTestGenerator extends RandomCheckedGenerator {

	@Override
	public void generateNewGrid(final Grid grid, int bombs, FinishCallback cb) {
		new Thread(){
			@Override
			public void run(){
				boolean test = false;


				if (test) {
					test1(grid, bombs);
					selectedSafeTile = 24;

					solve(grid);


				} else {
					generateNewRandomGrid(grid, bombs);
					selectedSafeTile = GridUtils.findSafeOpenTileIdx(grid);


					int numBombsLeft = 0;

					while (numBombsLeft == 0) {
						Set<Integer> bombsLeft = solve(grid);
						numBombsLeft = bombsLeft == null ? 0 : bombsLeft.size();

						if (numBombsLeft == 0) {
							generateNewRandomGrid(grid, bombs);
							selectedSafeTile = GridUtils.findSafeOpenTileIdx(grid);
						}
					}

				}


				cb.finished();
			}
		}.start();
	}

	protected void test1(Grid grid, int bombs) {
		int w = grid.getW();
		int h = grid.getH();
		List<Tile> tiles = grid.getGrid();
		tiles.clear();


		for (int j=0; j<h; j++) {
			for (int i=0; i<w; i++) {
				tiles.add( new Tile(i,j,Tile.Status.COVERED) );
			}
		}


		for (int idx : new int[]{8,10,11,12,17,22}) {
			tiles.get(idx).plantBomb();

			GridUtils.getNeighbors(grid, tiles.get(idx)).forEach(Tile::hasBombNear);
		}
	}
}
