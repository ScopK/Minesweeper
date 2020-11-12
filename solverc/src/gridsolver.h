#ifndef GRID_SOLVER_H_
#define GRID_SOLVER_H_

#include <bits/stdint-uintn.h>
#include <list>

#include "tile.h"


class Solver;


class GridSolver
{
public:
	static bool solve(const uint32_t &width, const uint32_t &height, Grid &grid);
	static void printTimes();

private:
	static Solver* solvers[];
	static uint32_t executionNumber;
};


class Solver
{
public:
	Solver() {
		totalClocks=0;
		totalExecutions=0;
	};
	virtual ~Solver() {};

	uint32_t totalClocks;
	uint32_t totalExecutions;

	virtual bool analyze() { return false; };

	bool timedAnalyze();

	static void setData(const uint32_t &width, const uint32_t &height, Grid &grid);
	static TileList getNeighbors(Tile* &tile, bool (*f)(Tile*));
	static TileList getNeighborsCovered(Tile* &tile);
	static void markBomb(Tile* &tile);
	static void reveal(Tile* &tile);

private:
	static void tileUpdate(Tile* &tile);

protected:
	static uint32_t width;
	static uint32_t height;
	static Grid* grid;

	static TileList numbered;
	static TileList revealed;
};


class BasicSolver : public Solver
{
public:
	bool analyze();
};

class PossibleSolver : public Solver
{
public:
	bool analyze();
private:
	static std::list<TileList*> getOptions(TileList candidates, const uint8_t &numBombs);
	static bool isPossible(Tile* &tile);
	static TileList tilesInEveryOption(std::list<TileList*> &options);

};

enum DeepSolverMatchResult_T : uint8_t {
	NO_MATCH,
	CASE_2_1,
	CASE_1_1
};

class DeepSolver : public Solver
{
public:
	bool analyze();
private:
	DeepSolverMatchResult_T matchLogic(const uint8_t &near, const uint8_t &neighNear, const uint8_t &totalCovers, const uint8_t &neighTotalCovers, const uint8_t &commonCovers);
};

class AdvancedSolver : public Solver
{
public:
	bool analyze();
};

#endif /* GRID_SOLVER_H_ */
