import { STATUS_NEAR, STATUS_BOMB_END, STATUS_FLAGGED, statusRevealed } from './status';

export function fillNear(tiles, w, h) {
	tiles.forEach(tile=>
		tile.near = getNeighbors(tile.p, w, h)
				.map(p=>tiles[p])
				.filter(n=>n.bomb)
				.length
	);
}

export function revealMass(tiles, position, w, h, aggressiveReveal = false) {
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
		const neighbors = getNeighbors(position, w, h);
		return neighbors.map(pos=>revealMass(tiles,pos,w,h)).indexOf(false) < 0;
	}
	return true;
}

export function getNeighbors(position, w, h) {
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


export function findFirstReveal(tiles, w, h) {
	const max = h * w;

	let newArray = [];
	for (let i=0; i<max; i++) {
		newArray.push(i);
	}
	shuffleArray(newArray);

	let pos = -1;
	let tmpPos0 = -1;
	let tmpPos1 = -1;

	while (pos < 0) {

		let rnd = newArray.pop();
		if (rnd == undefined){
			pos = tmpPos1 < 0? tmpPos0 : tmpPos1;
			break;	
		}

		if (!tiles[rnd].bomb) {
			tmpPos0 = rnd;
			let neighbors = getNeighbors(rnd, w, h);
			let hasBombs = neighbors.map(n => tiles[n].bomb).find(_=>_==true);

			if (!hasBombs) {
				tmpPos1 = rnd;

				let hasBombs2 = neighbors.flatMap(n=>getNeighbors(n, w, h)).map(n => tiles[n].bomb).find(_=>_==true);
				if (!hasBombs2) {
					pos = rnd;
					break;
				}
			}
		}
	}
	return pos;
}

function shuffleArray(array) {
	for (let i = array.length - 1; i > 0; i--) {
		let j = Math.floor(Math.random() * (i + 1));
		let temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
}