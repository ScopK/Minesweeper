#include "util.h"

#include <algorithm>
#include <cstdint>
#include <iostream>
#include <list>
#include <map>
#include <stdexcept>

void Util::findNeighborTiles(const uint32_t &width, const uint32_t &height, const uint32_t &position, Grid &grid, Tile* &tile)
{

	TileList* neighbors = tile->getNeighbors();

	uint32_t sum = position+height;
	uint32_t sub = position-height;
	uint32_t mod = position%height;

	bool top = mod == 0;
	//bool bot = (position+1) % height == 0;
	bool bot = mod == (height-1);
	bool left  = sub > INT32_MAX; // if negative
	bool right = sum >= width*height;

	if (!top) {
		neighbors->push_back(grid.at(position-1));
		if (!left)  neighbors->push_back(grid.at(sub-1));
		if (!right) neighbors->push_back(grid.at(sum-1));
	}

	if (!bot) {
		neighbors->push_back(grid.at(position+1));
		if (!left)  neighbors->push_back(grid.at(sub+1));
		if (!right) neighbors->push_back(grid.at(sum+1));
	}

	if (!left)  neighbors->push_back(grid.at(sub));
	if (!right) neighbors->push_back(grid.at(sum));
}


void Util::fillNearBombs(const uint32_t &width, const uint32_t &height, Grid &grid)
{
	for (Grid::iterator it = grid.begin(); it != grid.end(); it++) {
		findNeighborTiles(width, height, it->first, grid, it->second);

		uint8_t near = 0;
		for (Tile* neighbor : *it->second->getNeighbors()){
			if (neighbor->hasBomb())
				near++;
		}
		it->second->setNear(near);
	}
}

uint32_t Util::findStartingTile(const uint32_t &width, const uint32_t &height, Grid &grid)
{
	uint32_t max = width*height;

	uint32_t candidates[max];

	for (uint32_t i=0; i<max; i++) {
		candidates[i] = i;
	}

	std::random_shuffle(&candidates[0], &candidates[max]);

	uint32_t tmpPos0 = UINT32_MAX;
	uint32_t tmpPos1 = UINT32_MAX;
	uint32_t tmpPos2 = UINT32_MAX;
	for (uint32_t i=0; i<max; i++) {
		uint32_t position = candidates[i];
		Tile* t = grid.at(position);

		if (!t->hasBomb()) {
			tmpPos0 = position;

			TileList* neighbors = t->getNeighbors();
			bool anyHasBomb = false;
			for (Tile* neighbor : *neighbors){
				if (neighbor->hasBomb()) {
					anyHasBomb = true;
					break;
				}
			}

			if (!anyHasBomb) {
				tmpPos1 = position;

				for (Tile* neighbor : *neighbors){
					TileList* neighbors2 = neighbor->getNeighbors();
					for (Tile* neighbor2 : *neighbors2){
						if (neighbor2->hasBomb()) {
							anyHasBomb = true;
							break;
						}
					}
					if (anyHasBomb) break;
				}

				if (!anyHasBomb) {
					tmpPos2 = position;
					break;
				}
			}
		}
	}

	if (tmpPos2 == UINT32_MAX){
		tmpPos2 = tmpPos1==UINT32_MAX? tmpPos0 : tmpPos1;
	}
	return tmpPos2;
}


void Util::revealMass(const uint32_t &width, const uint32_t &height, const uint32_t &position, Grid &grid)
{
	Tile* tile = grid.at(position);

	if (tile->isFlagged() || tile->isRevealed()) {
		return;
	}

	if (tile->hasBomb()) {
		std::cerr << "Revealing a tile had a bomb" << std::endl;
		throw new std::runtime_error("Revealing a tile had a bomb");
	}

	tile->setRevealed(true);

	if (tile->getNearBombs() == 0) {
		for (Tile* neighbor : *tile->getNeighbors()) {
			revealMass(width, height, neighbor->getPosition(), grid);
		}
	}
}


void Util::printGrid(const uint32_t &width, const uint32_t &height, Grid &grid)
{
	for (uint32_t j=0; j<height; j++) {
		for (uint32_t i=0; i<width; i++) {
			uint32_t pos(i*height+j);
			if (i==0) std::cout << std::endl;

			Tile* tile = grid.at(pos);

			if (!tile->isRevealed()) {
				std::cout << (tile->isFlagged()? "X" : "O");

			} else if (tile->getNearBombs()==0) {
				std::cout << "_";
			} else {
				std::cout << (int)tile->getNearBombs();
			}
		}
	}
	std::cout << std::endl << std::endl;
}
