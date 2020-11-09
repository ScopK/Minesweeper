import basicSolver from './algorithms/BasicSolver';
import possibleSolver from './algorithms/PossibleSolver';
import deepSolver from './algorithms/DeepSolver';
import advancedSolver from './algorithms/AdvancedSolver';

import Sketch from './Sketch';

export default function solve(tiles, wh) {
	wh = {h:wh.h, w:wh.w};

	let sketch = new Sketch(tiles, wh);

	let solvers = [
		basicSolver,
		possibleSolver,
		deepSolver,
		advancedSolver,
	];

	while (solvers.find(s => s(sketch)));

	//copyObject(scheme.tiles, scheme.originalTiles);

	return true;
}






function copyObject(object, copy = {}) {
	for (let k in object) {
		copy[k] = object[k];
	}
	return copy;
}