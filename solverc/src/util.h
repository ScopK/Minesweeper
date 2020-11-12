#ifndef UTIL_H_
#define UTIL_H_

#include <bits/stdint-uintn.h>

#include "tile.h"

class Util
{
public:
	static void fillNearBombs(const uint32_t &width, const uint32_t &height, Grid &grid);
	static uint32_t findStartingTile(const uint32_t &width, const uint32_t &height, Grid &grid);
	static void findNeighborTiles(const uint32_t &width, const uint32_t &height, const uint32_t &position, Grid &grid, Tile* &tile);
	static void revealMass(const uint32_t &width, const uint32_t &height, const uint32_t &position, Grid &grid);
	static void printGrid(const uint32_t &width, const uint32_t &height, Grid &grid);
};


#endif /* UTIL_H_ */
