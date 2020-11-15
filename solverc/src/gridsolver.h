#ifndef GRID_SOLVER_H_
#define GRID_SOLVER_H_

#include <bits/stdint-uintn.h>
#include <list>

#include "tile.h"


class Solver;

enum SolverResult_T : uint8_t {
	RESOLVED,
	FAILED,
	SKIP
};

class GridSolver
{
public:
	static SolverResult_T solve(const uint32_t &width, const uint32_t &height, Grid &grid);
	static void printTimes();

private:
	static Solver* solvers[];
	static uint32_t executionNumber;
	static uint32_t resultFound;
};


struct SolverData
{
	const uint32_t width;
	const uint32_t height;
	Grid* grid;

	TileList numbered;
	TileList revealed;
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

	virtual bool analyze(SolverData &sd) { return false; };

	bool timedAnalyze(SolverData &sd);

	static SolverData setData(const uint32_t &width, const uint32_t &height, Grid &grid);
	static TileList getNeighbors(Tile* &tile, bool (*f)(Tile*));
	static TileList getNeighborsCovered(Tile* &tile);
	static void markBomb(SolverData &sd, Tile* &tile);
	static void reveal(SolverData &sd, Tile* &tile);

private:
	static void tileUpdate(SolverData &sd, Tile* &tile);

protected:
};


class BasicSolver : public Solver
{
public:
	bool analyze(SolverData &sd);
};

class PossibleSolver : public Solver
{
public:
	bool analyze(SolverData &sd);
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
	bool analyze(SolverData &sd);
private:
	DeepSolverMatchResult_T matchLogic(const uint8_t &near, const uint8_t &neighNear, const uint8_t &totalCovers, const uint8_t &neighTotalCovers, const uint8_t &commonCovers);
};

class AdvancedSolver : public Solver
{
public:
	bool analyze(SolverData &sd);
};

#endif /* GRID_SOLVER_H_ */
