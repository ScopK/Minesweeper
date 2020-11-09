import { statusRevealed } from '../../status';

import { getNeighborsCovered, markBomb, reveal } from '../common-utils';

export default function analyze(sketch) {
	let changesMade = false;

	let cleanUncovered = [];

	sketch.uncovered.forEach((tile,i) => {
		if (tile.near > 0) {
			let nearCovered = getNeighborsCovered(sketch, tile);

			if (tile.near == nearCovered.length) {
				changesMade = true;
				nearCovered.forEach(t=>markBomb(sketch, t));
			}

		} else if (tile.sFlag == '0') {
			delete tile.sFlag;

			getNeighborsCovered(sketch, tile)
					.forEach(t => {
						reveal(sketch, t);
						changesMade = true;
					});
		} else {
			cleanUncovered.unshift(i);
		}
	});

	cleanUncovered.forEach(t=>sketch.uncovered.splice(t,1));

	return changesMade;
}



