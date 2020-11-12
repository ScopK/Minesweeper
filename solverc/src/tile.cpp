#include "tile.h"


Tile::Tile(uint32_t _position) :
position(_position),
revealed(false),
bomb(false),
flagged(false),
near(0),

solver_revealed(false),
solver_notes(NONE)
{
	neighbors = new TileList();
}
