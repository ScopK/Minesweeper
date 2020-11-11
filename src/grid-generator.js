import solve from './solver/grid-solver';

import { fillNear, revealMass, findFirstReveal } from './control';
import { STATUS_DEFAULT } from './status';

export const RANDOM_GENERATOR = 0;
export const SOLVABLE_GENERATOR = 1;

export function generateGrid(generator = RANDOM_GENERATOR, ...args) {
	return [
		randomGenerator,
		solvableGenerator
	][generator](...args);
}


function randomGenerator(w, h, bombs, findStartPoint = false) {
	const tiles = [];

	let max = w * h;

	let bombsIndexes = [];

	while (bombs > 0) {
		let pos = Math.floor(Math.random() * max)
		if (bombsIndexes.indexOf(pos) < 0){
			bombsIndexes.push(pos);
			bombs--;
		}
	}

	for (let i=0; i<w; i++) {
		for (let j=0; j<h; j++) {

			let pos = tiles.length;
			tiles.push({
				x: i,
				y: j,
				p: pos,
				bomb: bombsIndexes.indexOf(pos) >= 0,
				alt: Math.floor(Math.random()*4)+1,
				status: STATUS_DEFAULT,
			});
		}
	}

	fillNear(tiles, w, h);

	if (findStartPoint) {
		let startPoint = findFirstReveal(tiles, w, h);
		revealMass(tiles, startPoint, w, h);
		return [tiles, startPoint];
	}
	return [tiles];
}


function solvableGenerator(w, h, bombs, findStartPoint = true) {
	let solved = false;

	let tiles, startPoint;
	while (!solved) {
		[tiles, startPoint] = randomGenerator(w, h, bombs, true);
		solved = solve(tiles, w, h);
	}

	if (findStartPoint) {
		revealMass(tiles, startPoint, w, h);
		return [tiles, startPoint];
	}
	return [tiles];
}




