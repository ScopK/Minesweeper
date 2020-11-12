#include <bits/stdint-uintn.h>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <map>

#include "generator.h"
#include "tile.h"

using namespace std;

int main(int argc, char** argv) {
	if (argc != 4) {
		cout << "Wrong parameter number!" << endl;
		return 1;
	}

	srand(time(NULL)); // Random seed for random tool

	uint32_t w = strtol(argv[1], NULL, 10);
	uint32_t h = strtol(argv[2], NULL, 10);
	uint32_t b = strtol(argv[3], NULL, 10);

	Grid grid;

	uint32_t startPoint = Generator::generateSolvableGrid(w, h, b, grid);

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

	return 0;
}


