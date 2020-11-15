#include "generator.h"

#include <cstdlib>
#include <map>
#include <utility>

#include "gridsolver.h"
#include "util.h"

uint32_t Generator::generateSolvableGrid(const uint32_t &height, const uint32_t &width, const uint32_t &bombs, Grid &grid)
{
	bool solved = false;

	uint32_t startPoint;
	while (!solved) {
		generateRandomGrid(width, height, bombs, grid);
		startPoint = Util::findStartingTile(width, height, grid);

		Util::revealMass(width, height, startPoint, grid);
		solved = GridSolver::solve(width, height, grid);
	}
	//GridSolver::printTimes();

	return startPoint;
}

void Generator::generateRandomGrid(const uint32_t &width, const uint32_t &height, const uint32_t &bombs, Grid &grid)
{
	for (Grid::iterator it = grid.begin(); it != grid.end(); it++) {
		delete it->second;
	}
	grid.clear();

	uint32_t max = width*height;

	for(uint32_t i=0; i<max; i++) {
		grid.insert( std::pair<uint32_t, Tile*>(i, new Tile(i)));
	}

	for(uint32_t i=0; i<bombs; i++) {
		uint32_t index = ((float)(std::rand())/RAND_MAX)*max;
		Tile* t = grid.at(index);
		if (t->hasBomb()) {
			i--;
		} else {
			t->setBomb(true);
		}
	}

	Util::fillNearBombs(width, height, grid);
}

