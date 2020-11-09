import { STATUS_DEFAULT, STATUS_FLAGGED, statusRevealed } from '../../status';

import { getNeighbors, getNeighborsCovered, markBomb, reveal } from '../common-utils';

export default function analyze(sketch) {
	let changesMade = false;

	let numbered = sketch.numbered.slice();

	numbered.forEach(tile => {
		let nearCovered = getNeighborsCovered(sketch, tile);

		if (nearCovered.length) {
			let options = getOptions(nearCovered, tile.near)
				.filter(option => {
					option.forEach(tile => tile.status = STATUS_FLAGGED);
					let keepOption = isPossible(sketch, tile);

			//if (tile.p == 211 && c==38 && keepOption) throw new Error("test");
					option.forEach(tile => tile.status = STATUS_DEFAULT);
					return keepOption;
				});

			if (options.length == 1) {
				options[0].forEach(t => {
					markBomb(sketch, t);
				});
				nearCovered.filter(t => options[0].indexOf(t) < 0).forEach(t => reveal(sketch, t));
				changesMade = true;

			} else {
				let sureBombs = tilesInAllOptions(options);
				sureBombs.forEach(t => markBomb(sketch, t));
				changesMade = changesMade || sureBombs.length > 0;
			}
		}
	});

	return changesMade;
}


function getOptions(candidates, numBombs) {
	candidates = candidates.slice();

	let value;
	let options = [];

	while ((value = candidates.pop()) != undefined) {
		if (numBombs == 1) {
			options.push([value]);

		} else {
			getOptions(candidates, numBombs-1).forEach(opts => options.push([value, ...opts]));
		}
	}
	return options;
}

function isPossible(sketch, tile) {
	return getNeighbors(sketch, tile, t => statusRevealed(t.status) && t.near)
		.find(t => {
			let flagsNear = getNeighbors(sketch, t, t => t.status==STATUS_FLAGGED && t.sFlag!='X').length;
			return flagsNear > t.near;
		}) == undefined;
}

function tilesInAllOptions(options) {
	return options[0]?.filter(tile => options.every(option => option.indexOf(tile) >= 0)) || [];
}