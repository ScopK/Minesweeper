package com.scop.org.minesweeper.generators;

import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.utils.GridUtils;

import java.util.List;
import java.util.Random;

public class RandomGenerator implements GridGenerator {

	@Override
	public void generateNewGrid(Grid grid, int bombs, FinishCallback cb) {
		new Thread(){
			@Override
			public void run(){
				generateNewRandomGrid(grid, bombs);
				cb.finished();
			}
		}.start();
	}

	protected void generateNewRandomGrid(Grid grid, int bombs) {
		int w = grid.getW();
		int h = grid.getH();
		List<Tile> tiles = grid.getGrid();
		tiles.clear();


		for (int j=0; j<h; j++) {
			for (int i=0; i<w; i++) {
				tiles.add( new Tile(i,j,Tile.Status.COVERED) );
			}
		}

		Random random = new Random();
		int idx,max = w*h;
		for (int i=0;i<bombs;i++) {
			idx = random.nextInt(max);
			if (tiles.get(idx).hasBomb()){
				i--;
				continue;
			}
			tiles.get(idx).plantBomb();

			GridUtils.getNeighbors(grid, tiles.get(idx)).forEach(Tile::hasBombNear);
		}
	}
}
