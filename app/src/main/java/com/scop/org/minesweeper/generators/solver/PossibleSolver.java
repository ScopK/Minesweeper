package com.scop.org.minesweeper.generators.solver;

import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.scop.org.minesweeper.elements.Tile.Status.*;

public class PossibleSolver extends Solver {

	@Override
	public boolean analyze(){
		boolean changesMade = false;

		List<Tile> sketchTiles = sketch.getTiles();

		for (int i : sketch.getNumberedCopy()){
			Tile sTile = sketchTiles.get(i);

			ArrayList<Integer> coveredIdx = GridUtils.getNeighborsIdx(sketch.getGrid(), sTile).stream()
				.filter(idx->sketchTiles.get(idx).getStatus() == COVERED)
				.collect(Collectors.toCollection(ArrayList::new));

			if (coveredIdx.size() > 0) {
				List<List<Integer>> options = getOptions(coveredIdx, sTile.getBombsNear());

				Iterator<List<Integer>> optIterator = options.iterator();
				while(optIterator.hasNext()){
					List<Integer> option = optIterator.next();

					for (Integer idxTile : option) {
						Tile oTile = sketchTiles.get(idxTile);
						oTile.setStatus(FLAG);
						GridUtils.getNeighbors(sketch.getGrid(), oTile).forEach(Tile::addFlaggedNear);
					}
					if (!isPossible(sTile)) {
						optIterator.remove();
					}
					for (Integer idxTile : option) {
						Tile oTile = sketchTiles.get(idxTile);
						oTile.setStatus(COVERED);
						GridUtils.getNeighbors(sketch.getGrid(), oTile).forEach(Tile::removeFlaggedNear);
					}
				}
				if (options.size() == 1){
					options.get(0).forEach(this::markBomb);
					coveredIdx.stream()
							.filter(idx -> !options.get(0).contains(idx))
							.forEach(this::reveal);
					changesMade = true;
				} else {
					List<Integer> idxCommons = idxInAllLists(options);
					idxCommons.forEach(this::markBomb);

					changesMade |= idxCommons.size()>0;
				}

			}
		}
		return changesMade;
	}

	private boolean isPossible(Tile tile){
		return GridUtils.getNeighbors(sketch.getGrid(), tile).stream()
				.filter(Tile::isNumberVisible)
				.noneMatch(t-> GridUtils.getNeighbors(sketch.getGrid(), t)
						.stream()
						.filter(t2 -> t2.getStatus() == FLAG && !t2.hasCustomFlag('X'))
						.count() > t.getBombsNear());
	}

	private List<Integer> idxInAllLists(List<List<Integer>> options){
		List<Integer> list = new ArrayList<>();

		if (options.size() > 0) {

			List<Integer> flatOptions = options.stream().flatMap(List::stream).collect(Collectors.toList());
			int size = options.size();
			for (Integer i : options.get(0)) {
				int frq = Collections.frequency(flatOptions, i);
				if (frq == size){
					list.add(i);
				}
			}
		}
		return list;
	}


	@SuppressWarnings("unchecked")
	protected List<List<Integer>> getOptions(final ArrayList<Integer> flist, int select){
		ArrayList<Integer> list = (ArrayList<Integer>) flist.clone();

		List<List<Integer>> opts = new ArrayList<>();

		while (!list.isEmpty()){
			ArrayList<Integer> opt = null;

			int thisValue = list.get(0);
			list.remove(0);

			if (select == 1){
				opt = new ArrayList<>();
				opt.add(thisValue);
				opts.add(opt);

			} else {

				ArrayList<Integer> clone = (ArrayList<Integer>) list.clone();

				List<List<Integer>> ret = getOptions(clone, select-1);
				if (ret != null) {
					for (List<Integer> l : ret) {
						opt = new ArrayList<>();
						opt.add(thisValue);
						opt.addAll(l);
						opts.add(opt);
					}
				}
			}
		}

		return opts;
	}
}
