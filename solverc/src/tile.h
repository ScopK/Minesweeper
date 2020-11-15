#ifndef TILE_H_
#define TILE_H_

#include <bits/stdint-uintn.h>
#include <list>
#include <map>

enum TileNotes_T { 
	NONE,
	DEFUSED,
	EMPTY
};


class Tile;
typedef std::map<uint32_t, Tile*> Grid;
typedef std::list<Tile*> TileList;


class Tile
{
public:
	Tile(uint32_t _position);
	virtual ~Tile(){
		delete neighbors;
	};

	const uint32_t getPosition() const { return position; }
	bool isRevealed() const { return revealed; }
	bool isSolverRevealed() const { return solver_revealed; }
	bool hasBomb() const {
		bool a = this->bomb;
		return a;
	}
	bool isFlagged() const { return flagged; }
	uint8_t getNearBombs() const { return near; }
	TileNotes_T getSolverNotes() const { return solver_notes; }
	TileList* getNeighbors() const { return neighbors; }

	void setBomb(bool bomb = true) { this->bomb = bomb; }
	void setRevealed(bool revealed = true) { this->revealed = revealed; }
	void setSolverRevealed(bool solver_revealed = true) { this->solver_revealed = solver_revealed; }
	void setFlagged(bool flagged = true) { this->flagged = flagged; }
	void setNear(uint8_t near) { this->near = near; }
	void decreaseNearCount() { this->near--; }
	void setSolverNotes(TileNotes_T solver_notes) { this->solver_notes = solver_notes; }

protected:
	const uint32_t position;
	bool revealed;
	bool bomb;
	bool flagged;
	uint8_t near;

	bool solver_revealed;
	TileNotes_T solver_notes;
	TileList* neighbors;
};

#endif /* TILE_H_ */
