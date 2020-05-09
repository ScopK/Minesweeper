package com.scop.org.minesweeper.generators.solver;

import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.scop.org.minesweeper.elements.Tile.Status.COVERED;

public class AdvancedSolver extends Solver {

	@Override
	public boolean analyze(){
		boolean changesMade = false;

		List<Tile> sketchTiles = sketch.getTiles();
		List<Integer> toReveal = new ArrayList<>();

		for (Integer i : sketch.getNumberedCopy()) {
			Tile sTile = sketchTiles.get(i);

			List<Integer> coveredIdx = GridUtils.getNeighborsIdx(sketch.getGrid(), sTile).stream()
					.filter(idx->sketchTiles.get(idx).getStatus() == COVERED)
					.collect(Collectors.toList());

			if (!coveredIdx.isEmpty()) {
				List<Integer> matching = sketch.getNumberedCopy();
				matching.remove(i);

				for (Integer j : coveredIdx) {
					List<Integer> neight = GridUtils.getNeighborsIdx(sketch.getGrid(), sketchTiles.get(j)).stream()
							.filter(idx -> sketchTiles.get(idx).getStatus() == sTile.getStatus())
							.collect(Collectors.toList());

					Iterator<Integer> it = matching.iterator();
					while (it.hasNext()) {
						if (!neight.contains(it.next())) it.remove();
					}
				}

				matching.stream()
						.map(k -> GridUtils.getNeighborsIdx(sketch.getGrid(), sketchTiles.get(k)))
						.flatMap(l -> l.stream())
						.filter(idx -> sketchTiles.get(idx).getStatus() == COVERED)
						.filter(k -> !coveredIdx.contains(k) && !toReveal.contains(k))
						.forEach(toReveal::add);
			}
		}

		for (Integer idx : toReveal) {
			this.reveal(idx);
			changesMade = true;
		}

		return changesMade;
	}
}
