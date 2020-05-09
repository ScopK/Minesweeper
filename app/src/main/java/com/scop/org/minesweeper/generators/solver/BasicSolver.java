package com.scop.org.minesweeper.generators.solver;

import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.scop.org.minesweeper.elements.Tile.Status.COVERED;

public class BasicSolver extends Solver {

	@Override
	public boolean analyze(){
		boolean changesMade = false;

		List<Tile> sketchTiles = sketch.getTiles();
		List<Integer> sketchTilesUncovered = new ArrayList<>();

		for (int i : sketch.getUncovered()) {
			Tile sTile = sketchTiles.get(i);

			if (sTile.getBombsNear() > 0){
				List<Integer> nearCovered = GridUtils.getNeighborsIdx(sketch.getGrid(), sTile).stream()
						.filter(ix->sketchTiles.get(ix).getStatus() == COVERED)
						.collect(Collectors.toList());

				if (sTile.getBombsNear() == nearCovered.size()){
					changesMade |= nearCovered.size() > 0;
					nearCovered.forEach(this::markBomb);
				}
			} else {
				sketchTilesUncovered.add(i);
			}
		}

		for (int i : sketchTilesUncovered) {
			Tile sTile = sketchTiles.get(i);

			if (sTile.hasCustomFlag('0')) {

				for (int idx : GridUtils.getNeighborsIdx(sketch.getGrid(), sTile) ){
					if (sketchTiles.get(idx).getStatus() == COVERED){
						reveal(idx);
						changesMade = true;
					}
				}
			}
		}


		return changesMade;
	}
}
