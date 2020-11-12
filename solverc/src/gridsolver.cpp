//#define __CALCULATE_TIME__

#include "gridsolver.h"

#include <algorithm>
#include <iostream>
#include <map>
#include <set>
#include <stdexcept>
#include <utility>

#ifdef __CALCULATE_TIME__
#include <chrono>
#endif

#include "util.h"

Solver* GridSolver::solvers[] = {
		new BasicSolver(),
		new PossibleSolver(),
		new DeepSolver(),
		new AdvancedSolver()
};

uint32_t GridSolver::executionNumber(0);


bool GridSolver::solve(const uint32_t &width, const uint32_t &height, Grid &grid)
{
	Solver::setData(width, height, grid);
	executionNumber++;

	uint32_t count = 0;
	bool changesMade;
	do {
		if (count > 100){
			changesMade = false;
		}
		changesMade = false;

		for (Solver* solver : solvers) {
			#ifdef __CALCULATE_TIME__
			changesMade = solver->timedAnalyze();
			#else
			changesMade = solver->analyze();
			#endif

			if (changesMade) break;
		}
		/*
		for (int i=0; !changesMade && i<NUM_SOLVERS; i++) {
			#ifdef __CALCULATE_TIME__
			changesMade = solvers[i]->timedAnalyze();
			#else
			changesMade = solvers[i]->analyze();
			#endif
		}
		*/

		//std::cout << "Iteration #" << executionNumber << "." << ++count << std::endl;
	} while (changesMade);


	bool solved = true;

	for (Grid::iterator it = grid.begin(); it != grid.end(); it++) {
		if (!it->second->isRevealed() && !it->second->isFlagged()) {
			solved = false;
			break;
		}
	}

	//Util::printGrid(width, height, grid);

	return solved;
}

#ifdef __CALCULATE_TIME__
void GridSolver::printTimes()
{
	for (Solver* s : solvers) {
		float seconds = ((float) s->totalClocks)/ 1000000;
		std::cout << "Milliseconds: " << seconds << " Executions: " << s->totalExecutions << " Average: " << (seconds/s->totalExecutions) << std::endl;
	}
}
#endif

// ########################################
// ############## SOLVER GENERAL FUNCTIONS
// ######################################

TileList Solver::revealed;
TileList Solver::numbered;

uint32_t Solver::width(0);
uint32_t Solver::height(0);
Grid* Solver::grid;

#ifdef __CALCULATE_TIME__
bool Solver::timedAnalyze()
{
	auto start_time = std::chrono::high_resolution_clock::now();

	bool result = analyze();

	totalExecutions++;
	auto end_time = std::chrono::high_resolution_clock::now();
	auto time = end_time - start_time;

	totalClocks = totalClocks + time/std::chrono::nanoseconds(1);


	return result;
}
#endif

void Solver::setData(const uint32_t &width, const uint32_t &height, Grid &grid)
{
	Solver::width = width;
	Solver::height = height;
	Solver::grid = &grid;

	revealed.clear();
	numbered.clear();

	for (Grid::iterator it = grid.begin(); it != grid.end(); it++) {
		Tile* tile = it->second;
		if (tile->isRevealed()) {
			tile->setSolverRevealed(true);
			revealed.push_back(tile);

			if (tile->getNearBombs() > 0) {
				numbered.push_back(tile);
			}
		}
	}
}

TileList Solver::getNeighbors(Tile* &tile, bool (*f)(Tile*))
{
	TileList neighbors = *tile->getNeighbors();
	neighbors.remove_if(f);
	return neighbors;
}

TileList Solver::getNeighborsCovered(Tile* &tile)
{
	return getNeighbors(tile, [](Tile* t){
		return t->isRevealed() || t->isFlagged();
	});
}

void Solver::markBomb(Tile* &tile)
{
	if (tile->isFlagged()) return;

	TileList neighbors = *tile->getNeighbors();

	if (!tile->hasBomb()) {
		std::cerr << "Flagging a tile that has no bomb..." << std::endl;
		throw new std::runtime_error("Flagging a tile that has no bomb...");
	}

	tile->setFlagged(true);

	tile->setBomb(false);
	tile->setSolverNotes(DEFUSED);

	for (Tile* neighTile : neighbors) {
		if (neighTile->getSolverNotes() != DEFUSED) {
			neighTile->decreaseNearCount();

			if (neighTile->getNearBombs() == 0) {
				if (neighTile->isRevealed()) {
					numbered.remove(neighTile);
				}

				neighTile->setSolverNotes(EMPTY);
			}
		}
	}
}

void Solver::reveal(Tile* &tile)
{
	uint32_t position(tile->getPosition());

	Util::revealMass(width, height, position, *grid);
	tileUpdate(tile);
}

void Solver::tileUpdate(Tile* &tile)
{
	if (!tile->isSolverRevealed() && tile->isRevealed()) {
		tile->setSolverRevealed(true);

		revealed.push_back(tile);

		if (tile->getNearBombs() > 0) {
			numbered.push_back(tile);
		}

		TileList neighbor = *tile->getNeighbors();

		for (Tile* neighTile : neighbor) {
			tileUpdate(neighTile);
		}
	}
}

// ########################################
// ########################## BASIC SOLVER
// ######################################

bool BasicSolver::analyze()
{
	bool changesMade = false;

	revealed.remove_if([&changesMade](Tile* tile){
		if (tile->getNearBombs() > 0) {
			TileList nearCovered = getNeighborsCovered(tile);

			if (nearCovered.size() == tile->getNearBombs()) {
				for (Tile* nearTile : nearCovered) {
					markBomb(nearTile);
				}
				changesMade = true;
			}
		} else if (tile->getSolverNotes() == EMPTY) {
			tile->setSolverNotes(NONE);

			TileList nearCovered = *tile->getNeighbors();
			for (Tile* nearTile : nearCovered) {
				if (!nearTile->isRevealed() && !nearTile->isFlagged()) {
					reveal(nearTile);
					changesMade = true;
				}
			}

		} else {
			return true;
		}
		return false;
	});

	return changesMade;
}

// ########################################
// ####################### POSSIBLE SOLVER
// ######################################


bool PossibleSolver::analyze()
{
	bool changesMade = false;

	TileList numberedCopy(numbered);

	for (Tile* tile : numberedCopy) {
		TileList nearCovered = getNeighborsCovered(tile);

		if (!nearCovered.empty()) {
			std::list<TileList*> options = getOptions(nearCovered, tile->getNearBombs());

			options.remove_if([&tile](TileList* option){
				for (Tile* optionTile : *option) optionTile->setFlagged(true);
				bool remove = !isPossible(tile);
				for (Tile* optionTile : *option) optionTile->setFlagged(false);
				return remove;
			});


			if (options.size() == 1) {
				TileList* option = options.front();
				for (Tile* optionTile : *option) {
					markBomb(optionTile);
				}
				for (Tile* nearTile : nearCovered) {
					bool found = std::find(option->begin(), option->end(), nearTile) != option->end();
					if (!found) {
						reveal(nearTile);
					}
				}
				changesMade = true;

			} else {
				TileList sureBombs = tilesInEveryOption(options);
				for (Tile* sureBombTile : sureBombs) {
					markBomb(sureBombTile);
				}
				if (!changesMade) changesMade = !sureBombs.empty();
			}
		}
	}

	return changesMade;
}

std::list<TileList*> PossibleSolver::getOptions(TileList candidates, const uint8_t &numBombs) {
	std::list<TileList*> options;

	while (!candidates.empty()) {
		Tile* tile = candidates.back();
		candidates.pop_back();

		if (numBombs == 1) {
			TileList* opts = new TileList();
			opts->push_back(tile);
			options.push_back(opts);

		} else {
			std::list<TileList*> opts = getOptions(candidates, numBombs-1);
			for (TileList* o : opts) {
				o->push_back(tile);
				options.push_back(o);
			}
		}
	}
	return options;
}

bool PossibleSolver::isPossible(Tile* &tile) {
	for (Tile* neighTile : *tile->getNeighbors()) {

		if (neighTile->isRevealed() && neighTile->getNearBombs() > 0) {

			uint8_t flags = 0;
			for (Tile* neighTile2 : *neighTile->getNeighbors()) {
				if (neighTile2->isFlagged() && neighTile2->getSolverNotes() != DEFUSED) {
					flags++;
				}
			}
			if (flags > neighTile->getNearBombs())
				return false;
		}
	}
	return true;
}


TileList PossibleSolver::tilesInEveryOption(std::list<TileList*> &options)
{
	if (options.size() == 1) {
		return *options.front();
	}

	TileList tiles;
	if (!options.empty()) {
		TileList* firstOption = options.front();
		options.pop_front();

		for (Tile* tile : *firstOption) {
			bool foundInEveryOption = true;

			for (TileList* option : options) {
				bool found = std::find(option->begin(), option->end(), tile) != option->end();
				if (!found) {
					foundInEveryOption = false;
					break;
				}
			}

			if (foundInEveryOption) {
				tiles.push_back(tile);
			}
		}
	}

	return tiles;
}


// ########################################
// ########################### DEEP SOLVER
// ######################################

bool DeepSolver::analyze()
{
	bool changesMade = false;

	//TileList numberedCopy(numbered);

	for (Tile* tile : numbered) {
		if (tile->getNearBombs() > 0) { // REDUNDANT???
			TileList nearCovered = getNeighborsCovered(tile);

			if (!nearCovered.empty()) {

				TileList neighbors = *tile->getNeighbors();
				for (Tile* neighTile : neighbors) {
					if (neighTile->isRevealed() && neighTile->getNearBombs() > 0) { // numberedNeighbors
						TileList neighNearCovered = getNeighborsCovered(neighTile);

						uint8_t common = 0;
						for (Tile* t : neighNearCovered) {
							bool found = std::find(nearCovered.begin(), nearCovered.end(), t) != nearCovered.end();
							if (found) common++;
						}

						DeepSolverMatchResult_T result = matchLogic(
								tile->getNearBombs(),
								neighTile->getNearBombs(),
								nearCovered.size(),
								neighNearCovered.size(),
								common);

						switch(result) {
							case CASE_2_1:

								for (Tile* t : nearCovered) {
									bool found = std::find(neighNearCovered.begin(), neighNearCovered.end(), t) != neighNearCovered.end();

									if (!found) markBomb(t);
								}
								// no breaking case:

							case CASE_1_1:

								for (Tile* t : neighNearCovered) {
									bool found = std::find(nearCovered.begin(), nearCovered.end(), t) != nearCovered.end();

									if (!found) reveal(t);
								}
								changesMade = true;
								break;

							case NO_MATCH:
								break;
						}
					}
				}
			}
		}
	}

	return changesMade;
}

DeepSolverMatchResult_T DeepSolver::matchLogic(const uint8_t &near, const uint8_t &neighNear, const uint8_t &totalCovers, const uint8_t &neighTotalCovers, const uint8_t &commonCovers)
{
	if (commonCovers > 0) {

		if (near > neighNear) {
			///////////// Detecting this case
			//// Xxx //// [near 2, neighNear 1, totalcovers 3]
			////  21 //// diff is 1
			///////////// totalcovers - diff == commonCovers (3-1==2)
			///////////// we know for sure X is a bomb

			if (totalCovers - (near-neighNear) == commonCovers)
				return CASE_2_1;

		} else {

			///////////// Detecting this case
			//// XXx //// [near 1, neighNear 1, totalcovers 2, commonCovers 2, neighTotalCovers 3]
			//// 11  ////
			///////////// we know for sure x is not a bomb

			if (near == neighNear && totalCovers == commonCovers && totalCovers != neighTotalCovers)
				return CASE_1_1;
		}

	}
	return NO_MATCH;
}

// ########################################
// ####################### ADVANCED SOLVER
// ######################################

bool AdvancedSolver::analyze()
{
	bool changesMade = false;

	std::set<Tile*> toReveal;

	for (Tile* tile : numbered) {
		TileList nearCovered = getNeighborsCovered(tile);

		if (!nearCovered.empty()) {
			TileList matching(numbered);
			matching.remove(tile);

			for (Tile* nearCoveredTile : nearCovered) {
				TileList sameStatusNeighs = *nearCoveredTile->getNeighbors();

				sameStatusNeighs.remove_if([tile](Tile* t){
					return tile->isRevealed() != t->isRevealed() ||
							tile->getNearBombs() != t->getNearBombs() ||
							tile->isFlagged() != t->isFlagged();
				});

				matching.remove_if([sameStatusNeighs](Tile* t){
					bool found = std::find(sameStatusNeighs.begin(), sameStatusNeighs.end(), t) != sameStatusNeighs.end();
					return !found;
				});
			}

			for (Tile* numberedTile : matching) {
				TileList sameStatusNeighs = getNeighborsCovered(numberedTile);

				for (Tile* sameStatusNeighTile : sameStatusNeighs) {
					bool found = std::find(nearCovered.begin(), nearCovered.end(), sameStatusNeighTile) != nearCovered.end();

					if (!found) {
						toReveal.insert(sameStatusNeighTile);
					}
				}
			}
		}
	}

	for (Tile* tile : toReveal) {
		reveal(tile);
		changesMade = true;
	}

	return changesMade;
}
