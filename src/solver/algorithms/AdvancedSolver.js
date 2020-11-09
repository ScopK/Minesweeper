import { STATUS_DEFAULT } from '../../status';

import { getNeighbors, getNeighborsCovered, reveal } from '../common-utils';

export default function analyze(sketch) {
	let changesMade = false;

	let numbered = sketch.numbered.slice();

	let toReveal = numbered.flatMap((tile,index) => {

		let nearCovered = getNeighborsCovered(sketch, tile);

		if (nearCovered.length) {
			let matching = numbered.slice();
			matching.splice(index, 1);


			nearCovered.forEach(nearCoveredTile => {
				let sameStatusNeighs = getNeighbors(sketch, nearCoveredTile, t => t.status == tile.status);

				matching = matching.filter(t => sameStatusNeighs.indexOf(t) >= 0);
			});

			return matching.flatMap(t => getNeighbors(sketch, t))
					.filter(t => t.status == STATUS_DEFAULT)
					.filter(t => nearCovered.indexOf(t) < 0);
		}

		return [];
	});

	// Set to Remove duplicates
	new Set(toReveal).forEach(t => {
		reveal(sketch, t);
		changesMade = true;
	});

	return changesMade;
}