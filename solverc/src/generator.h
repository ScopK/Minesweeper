#ifndef GENERATOR_H_
#define GENERATOR_H_

#include <bits/stdint-uintn.h>

#include "tile.h"

class Generator
{
public:
	static uint32_t generateSolvableGrid(const uint32_t &height, const uint32_t &width, const uint32_t &bombs, Grid &grid);

protected:
	static void generateRandomGrid(const uint32_t &height, const uint32_t &width, const uint32_t &bombs, Grid &grid);
};

#endif /* GENERATOR_H_ */
