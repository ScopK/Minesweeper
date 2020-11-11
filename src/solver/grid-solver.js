import { STATUS_DEFAULT } from '../status';

import basicSolver from './algorithms/BasicSolver';
import possibleSolver from './algorithms/PossibleSolver';
import deepSolver from './algorithms/DeepSolver';
import advancedSolver from './algorithms/AdvancedSolver';

import Sketch from './Sketch';

export default function solve(tiles, w, h) {
	let sketch = new Sketch(tiles, {h:h, w:w});

	let solvers = [
		basicSolver,
		possibleSolver,
		deepSolver,
		advancedSolver,
	];

	while (solvers.find(s => s(sketch)));

	//copyObject(scheme.tiles, scheme.originalTiles);

	let solved = sketch.tiles.filter(t => t.status == STATUS_DEFAULT).length == 0;

	if (solved) tiles.forEach(tile => tile.status = STATUS_DEFAULT);

	return solved;
}






function copyObject(object, copy = {}) {
	for (let k in object) {
		copy[k] = object[k];
	}
	return copy;
}