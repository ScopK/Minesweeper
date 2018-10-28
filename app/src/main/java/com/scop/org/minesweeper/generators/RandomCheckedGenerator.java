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
import java.util.Set;
import java.util.stream.Collectors;

public class RandomCheckedGenerator extends RandomGenerator {
	private int selectedSafeTile;


	@Override
	public void generateNewGrid(Grid grid, int bombs, FinishCallback cb) {
		new Thread(){
			@Override
			public void run(){
				generateNewRandomGrid(grid, bombs);
				selectedSafeTile = GridUtils.findSafeOpenTileIdx(grid);

				Grid originalGrid = grid;
				int numBombsLeft = 1;

				while (numBombsLeft != 0) {
					Set<Integer> bombsLeft = solve(originalGrid.clone());
					numBombsLeft = bombsLeft == null? Integer.MAX_VALUE : bombsLeft.size();

					if (numBombsLeft > 0) {
						if (numBombsLeft < bombs/20) {
							swapBombs(grid, bombsLeft);
							originalGrid = grid;

						} else {
							generateNewRandomGrid(grid, bombs);
							originalGrid = grid;
							selectedSafeTile = GridUtils.findSafeOpenTileIdx(grid);
						}

					}
				}

				if (Settings.getInstance().isFirstOpen()) {
					MainLogic ml = new MainLogic(grid);
					Tile t = grid.getGrid().get(selectedSafeTile);
					ml.reveal(t);
				}

				cb.finished();
			}
		}.start();
	}


	public void swapBombs(Grid grid, Set<Integer> toReplace) {
		List<Integer> protectedTiles = GridUtils.getNeighborsIdx(grid, grid.getGrid().get(selectedSafeTile));
		protectedTiles.add(selectedSafeTile);

		for (int rplc : toReplace) {
			int aim;
			do {
				aim = GridUtils.findSafeOpenTileIdx(grid);
			} while (protectedTiles.indexOf(aim) >= 0);

			Tile t0 = grid.getGrid().get(rplc);
			t0.plantBomb(false);
			GridUtils.getNeighbors(grid, t0).forEach(Tile::doesntHaveBombNear);

			Tile tf = grid.getGrid().get(aim);
			tf.plantBomb();
			GridUtils.getNeighbors(grid, tf).forEach(Tile::hasBombNear);
		}
	}

	public Set<Integer> solve(Grid grid){
		MainLogic ml = new MainLogic(grid);

		Tile t = grid.getGrid().get(selectedSafeTile);
		ml.reveal(t);

		Sketch sketch = new Sketch(grid);

		Solver.setSolver(ml,sketch);

		List<Solver> solvers = new ArrayList<>();
		solvers.add(new BasicSolver());
		solvers.add(new PossibleSolver());
		solvers.add(new DeepSolver());

		while(
			solvers.stream().anyMatch(Solver::analyze)
		);
		return Solver.getLeftBombs();
	}
}
