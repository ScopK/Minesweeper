package com.scop.org.minesweeper.generators;

import com.scop.org.minesweeper.elements.Grid;

public interface GridGenerator {
	void generateNewGrid(Grid grid, int bombs, FinishCallback cb);

	@FunctionalInterface
	interface FinishCallback {
		void finished();
	}
}

