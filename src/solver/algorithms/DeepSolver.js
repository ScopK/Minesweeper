import { statusRevealed } from '../../status';

import { getNeighbors, getNeighborsCovered, markBomb, reveal } from '../common-utils';

const NONE = 0;
const CASE_1_1 = 1;
const CASE_2_1 = 2;

export default function analyze(sketch) {
	let changesMade = false;

	let numbered = sketch.numbered.slice();

	numbered.forEach(tile => {
		if (/*statusRevealed(tile.status) &&*/ tile.near) { // REDUNDANT???

			let nearCovered = getNeighborsCovered(sketch, tile);

			if (nearCovered.length) {

				getNeighbors(sketch, tile, t => statusRevealed(t.status) && t.near) // numberedNeighbors
					.forEach(neighTile => {

						let neighNearCovered = getNeighborsCovered(sketch, neighTile);

						let common = neighNearCovered.filter(t => nearCovered.indexOf(t) >= 0).length;

						let thisCase = matchLogic(tile.near, neighTile.near, nearCovered.length, neighNearCovered.length, common);

						switch (thisCase) {
							case CASE_2_1:
								nearCovered.filter(t => neighNearCovered.indexOf(t) < 0).forEach(t => markBomb(sketch, t));
								// no breaking case:

							case CASE_1_1:
								neighNearCovered.filter(t => nearCovered.indexOf(t) < 0).forEach(t => reveal(sketch, t));
								changesMade = true;
								break;
						}
				});
			}
		}
	});

	return changesMade;
}

function matchLogic(near, neighNear, totalCovers, neighTotalCovers, commonCovers) {
	if (commonCovers) {
		if (near > neighNear ) {

			///////////// Detecting this case
			//// Xxx //// [near 2, neighNear 1, totalcovers 3]
			////  21 //// diff is 1
			///////////// totalcovers - diff == commonCovers (3-1==2)
			///////////// we know for sure X is a bomb

			let diff = near - neighNear;
			if (totalCovers - diff == commonCovers) return CASE_2_1;

		} else {

			///////////// Detecting this case
			//// XXx //// [near 1, neighNear 1, totalcovers 2, commonCovers 2, neighTotalCovers 3]
			//// 11  //// 
			///////////// we know for sure x is not a bomb

			if (near == neighNear && totalCovers == commonCovers && totalCovers != neighTotalCovers)
				return CASE_1_1;
		}
	}
	return NONE;
}