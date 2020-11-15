#include <bits/stdint-uintn.h>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <map>
#include <vector>
#include <thread>

#include "generator.h"
#include "tile.h"

using namespace std;

void execution(const uint32_t &width, const uint32_t &height, const uint32_t &bombs);

int main(int argc, char** argv) {
	if (argc != 5) {
		cerr << "Execution requires 4 parameters: GRID_WIDTH GRID_HEIGHT NUM_BOMBS NUM_THREADS" << endl;
		return 1;
	}

	srand(time(NULL)); // Random seed for random tool

	const uint32_t w = strtol(argv[1], NULL, 10);
	const uint32_t h = strtol(argv[2], NULL, 10);
	const uint32_t b = strtol(argv[3], NULL, 10);
	const uint8_t t = strtol(argv[4], NULL, 10);

	thread threads[t];
	for (uint8_t i = 0; i < t ; i++) {
		threads[i] = thread(execution, w, h, b);
	}

	for (uint8_t i = 0; i < t ; i++) {
		threads[i].join();
	}

	return 0;
}


void execution(const uint32_t &width, const uint32_t &height, const uint32_t &bombs)
{
	Grid grid;

	uint32_t startPoint = Generator::generateSolvableGrid(width, height, bombs, grid);

	if (startPoint > INT32_MAX) return; // Negative

	cout << startPoint << ":";

	uint8_t byte(0);
	uint8_t bytePosCount(0);

	for (Grid::iterator it = grid.begin(); it != grid.end(); it++) {

		byte = byte << 1;
		if (it->second->hasBomb() || it->second->getSolverNotes() == DEFUSED) {
			byte += 1;
		}

		bytePosCount++;
		if (bytePosCount == 8) {
			std::cout << byte;

			byte=0;
			bytePosCount=0;
		}
		//cout << (it->second->hasBomb() || it->second->getSolverNotes() == DEFUSED);
	}
	if (bytePosCount != 0) {
		byte = byte << (8-bytePosCount);
		std::cout << byte;
	}

	cout << endl;
}
