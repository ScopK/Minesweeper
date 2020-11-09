import { statusRevealed } from '../status';

export default class Sketch {

	constructor(tiles, wh) {
		this.originalTiles = tiles;
		this.wh = wh;
		
		this.tiles = tiles.map(t=>copyObject(t));
		this.uncovered = this.tiles.filter(tile => statusRevealed(tile.status));
		this.numbered = this.uncovered.filter(tile => tile.near);
	}

	removeNumbered(tile) {
		let index = this.numbered.indexOf(tile);
		if (index >= 0) {
			this.numbered.splice(index, 1);
		}
	}

	removeUncovered(tile) {
		let index = this.uncovered.indexOf(tile);
		if (index >= 0) {
			this.uncovered.splice(index, 1);
		}
	}
}



function copyObject(object, copy = {}) {
	for (let k in object) {
		copy[k] = object[k];
	}
	return copy;
}