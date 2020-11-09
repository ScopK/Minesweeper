import { STATUS_NEAR, STATUS_BOMB_END, STATUS_FLAGGED, statusRevealed } from './status';


export function fillNear(tiles, wh) {
	tiles.forEach(tile=>
		tile.near = getNeighbors(tile.p, wh)
				.map(p=>tiles[p])
				.filter(n=>n.bomb)
				.length
	);
}

export function revealMass(tiles, position, wh, aggressiveReveal = false) {
	const tile = tiles[position];

	if (tile.status == STATUS_FLAGGED) {
		return true;
	}
	if (!aggressiveReveal && statusRevealed(tile.status)) {
		return true;
	}
	if (tile.bomb) {
		tile.status = STATUS_BOMB_END;
		return false;
	}

	tile.status = STATUS_NEAR(tile.near);

	if (!tile.near || aggressiveReveal) {
		const neighbors = getNeighbors(position, wh);
		return neighbors.map(pos=>revealMass(tiles,pos,wh)).indexOf(false) < 0;
	}
	return true;
}

export function getNeighbors(position, { h, w }) {
	let top = position     % h == 0,
		bot = (position+1) % h == 0,
		left = position-h < 0,
		right = position+h >= w*h;

	return [
			top?   -1 : position-1,
			bot?   -1 : position+1,
			left?  -1 : position-h,
			right? -1 : position+h,
			top || left?  -1 : position-h-1,
			bot || left?  -1 : position-h+1,
			top || right? -1 : position+h-1,
			bot || right? -1 : position+h+1]
		.filter(v => v >= 0);
}