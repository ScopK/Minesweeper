import { getNeighbors as controlGetNeighbors, revealMass } from '../control';
import { STATUS_DEFAULT, STATUS_NEAR, STATUS_FLAGGED, statusRevealed } from '../status';


// SUPPORT FUNCTIONS
export function getNeighbors(sketch, tile, filter = false) {
	let neighbors = controlGetNeighbors(tile.p, sketch.wh)
			.map(n => sketch.tiles[n]);
	return filter? neighbors.filter(t => filter(t)) : neighbors;
}



export function getNeighborsCovered(sketch, tile) {
	return getNeighbors(sketch, tile, t => t.status == STATUS_DEFAULT)
}


export function markBomb(sketch, tile){
	let oTile = sketch.originalTiles[tile.p];

	if (oTile.status == STATUS_FLAGGED) return;
	oTile.status = tile.status = STATUS_FLAGGED;

	tile.sFlag = 'X';
	tile.bomb = false;

	getNeighbors(sketch, tile, t => t.sFlag != 'X').forEach(neighTile => {
		let near = --neighTile.near;

		let isRevealed = statusRevealed(neighTile.status);

		if (isRevealed) neighTile.status = STATUS_NEAR(near);

		if (near == 0) {
			if (isRevealed) {
				sketch.removeNumbered(neighTile);
			}
			neighTile.sFlag = '0';
		}
	});
}


export function reveal(sketch, tile){
	revealMass(sketch.originalTiles, tile.p, sketch.wh);
	tileUpdate(sketch, tile);
}

function tileUpdate(sketch, tile){
	if (!statusRevealed(tile.status)) {

		let oTile = sketch.originalTiles[tile.p];

		if (statusRevealed(oTile.status) && oTile.near == 0) {

			tile.status = STATUS_NEAR(tile.near = 0);
			sketch.uncovered.push(tile);

			getNeighbors(sketch, tile).forEach(t => tileUpdate(sketch, t));

		} else {
			sketch.uncovered.push(tile);
			sketch.numbered.push(tile);
			tile.status = STATUS_NEAR(tile.near);
		}
	}
}