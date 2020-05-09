package com.scop.org.minesweeper.generators.solver;

import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.scop.org.minesweeper.elements.Tile.Status.*;

public class DeepSolver extends Solver {
	private static final int NONE = -1;
	private static final int CASE_1_1 = 1;
	private static final int CASE_2_1 = 2;

	@Override
	public boolean analyze(){
		boolean changesMade = false;

		List<Tile> sketchTiles = sketch.getTiles();

		for (Integer i : sketch.getNumberedCopy()) {
			Tile sTile = sketchTiles.get(i);

			if (sTile.isNumberVisible()) {
				List<Integer> coveredIdx = GridUtils.getNeighborsIdx(sketch.getGrid(), sTile).stream()
					.filter(idx->sketchTiles.get(idx).getStatus() == COVERED)
					.collect(Collectors.toList());

				if (coveredIdx.size() > 0) {
					int bNear = sTile.getBombsNear();
					int totalCovers = coveredIdx.size();
					coveredIdx.stream()
							.map(sketchTiles::get)
							.forEach(t->t.setCustomFlag('?'));

					List<Tile> numberedNeighbors =  GridUtils.getNeighbors(sketch.getGrid(),sTile).stream()
							.filter(Tile::isNumberVisible)
							.collect(Collectors.toList());

					for (Tile sTile2 : numberedNeighbors) {
						List<Integer> coveredIdx2 = GridUtils.getNeighborsIdx(sketch.getGrid(), sTile2).stream()
								.filter(idx->sketchTiles.get(idx).getStatus() == COVERED)
								.collect(Collectors.toList());

						int bNear2 = sTile2.getBombsNear();
						int totalCovers2 = coveredIdx2.size();
						int count = (int) coveredIdx2.stream()
								.filter(idx->sketchTiles.get(idx).hasCustomFlag('?'))
								.count();
						if (count > 0) {
							int mCase = matchLogic(bNear, bNear2, totalCovers, totalCovers2, count);
							switch (mCase) {
								case CASE_2_1:
									for (int idx : coveredIdx){
										if (!coveredIdx2.contains(idx)){
											this.markBomb(idx);
										}
									}

								case CASE_1_1:
									for (int idx : coveredIdx2){
										if (!coveredIdx.contains(idx)){
											this.reveal(idx);
										}
									}

									changesMade = true;
									break;
							}
						}
					}
					coveredIdx.stream()
							.map(sketchTiles::get)
							.forEach(t->t.removeCustomFlag('?'));
				}
			}
		}

		return changesMade;
	}

	private int matchLogic(int bNear, int bNear2, int totalCovers, int totalCovers2, int count) {
		if (bNear > bNear2) {
			int diff = bNear-bNear2;
			return totalCovers-diff == count? CASE_2_1:NONE;
		}
		if (bNear == bNear2){
			return count == totalCovers? (totalCovers == totalCovers2? NONE:CASE_1_1):NONE;
		}
		return NONE;
	}
}
