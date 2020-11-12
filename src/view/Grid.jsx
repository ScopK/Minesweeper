import React from 'react';

import solve from '../solver/grid-solver';

import Tile from './Tile.jsx';
import { TILE_SIZE } from './Tile.jsx';
import { getNeighbors, revealMass } from '../control';
import dragged from '../drag-scroll';
import { STATUS_DEFAULT, STATUS_BOMB, STATUS_BOMB_END, STATUS_FLAGGED, STATUS_FLAGGED_FAILED, statusRevealed } from '../status';
import { generateGrid } from '../grid-generator';

const EVENT_END_GAME = 0;
const EVENT_REVEALED_TILE = 1;
const EVENT_GET_NEAR_FLAG = 2;

const GAME_STATUS_PLAYING = 0;
const GAME_STATUS_LOSE = 1;
const GAME_STATUS_WIN = 2;

export default class Grid extends React.Component {
	constructor(props) {
		super(...arguments);

		let [tiles, firstOpen] = generateGrid(this.props.generator, props.w, props.h, props.bombs, this.props.firstReveal);

		this.gameStatus = GAME_STATUS_PLAYING;
		this.state = { tiles: tiles	};
	}

	render(){
		return (
			<div id="grid-container" className={this.props.skin || 'default'} style={{width: `${TILE_SIZE*this.props.w}px`, height: `${TILE_SIZE*this.props.h}px`}}>
				<div id="grid-scroll">
					{
						this.state.tiles.map(tile=>
							<Tile 
								x={ tile.x }
								y={ tile.y }
								p={ tile.p }
								status={ tile.status }
								bomb={ tile.bomb }
								alt={ tile.alt }
								onClick=      { this.handleClick.bind(this) }
								onContextMenu={ this.handleContextMenu.bind(this) }
								/>
							)
					}
				</div>
			</div>
		)
	}

	handleClick(position, event) {
		if (dragged(true)) {
			return;
		}

		if (this.gameStatus != GAME_STATUS_PLAYING) {
			window.location.reload();
			return;
		}

		const props = this.props;
		const tiles = this.state.tiles;
		const tile = tiles[position];
		const status = tile.status;


		if (status == STATUS_FLAGGED) {
			tile.status = STATUS_DEFAULT;

		} else if (!statusRevealed(tile.status)) {
			tile.status = STATUS_FLAGGED;

			if (props.auto) {
				this.autoStack = getNeighbors(position, props.w, props.h);
				if (!this.revealAuto()){
					this.gameOver();
				}
			}

		} else {
			let neighbors = getNeighbors(position, props.w, props.h);
			let flagged = neighbors.map(n => tiles[n]).filter(t => t.status==STATUS_FLAGGED).length;

			if (flagged >= tile.near) {
				if (!revealMass(tiles, position, props.w, props.h, true)){
					this.gameOver();
				}
			}
		}

		this.setState(this.state);
	}

	handleContextMenu(position, event) {
		event.preventDefault();

		const props = this.props;
		const tiles = this.state.tiles;
		const tile = tiles[position];
		let status = tile.status;

		if (status == STATUS_FLAGGED || this.gameStatus != GAME_STATUS_PLAYING) {

			if (props.auto) {
				this.autoStack = getNeighbors(position, props.w, props.h);
				if (!this.revealAuto()){
					this.gameOver();
				}
			}

			return;
		}

		if (!statusRevealed(status)) {
			if (!revealMass(tiles, position, this.props.w, this.props.h)) {
				this.gameOver();
			}

			this.setState({ tiles: tiles });
		}
	}

	revealAuto() {
		let props = this.props;
		let tiles = this.state.tiles;

		let revealId = Math.random();
		let lost = 0;
		let position;

		while ( (position = this.autoStack.shift()) != undefined) {
			let tile = tiles[position];


			if (tile.near && tile.revealId != revealId) {

				let flagged = 0

				let coveredNeighbors = getNeighbors(position, props.w, props.h).filter(n => {
					let tile = tiles[n];
					if (tile.status==STATUS_FLAGGED) flagged++;
					else if (tile.status==STATUS_DEFAULT) return true;
				});

				if (statusRevealed(tile.status) && flagged >= tile.near) {
					tile.revealId = revealId;

					if (!revealMass(tiles, position, props.w, props.h, true)) {
						this.autoStack = [];
						return false;
					}

					this.autoStack = this.autoStack.concat(coveredNeighbors.filter(n=> {
						let tile = tiles[n];
						return tile.near && tile.revealId != revealId;
					}));
				}
			}
		}

		return true;
	}

	gameOver() {
		this.gameStatus = GAME_STATUS_LOSE;
		this.state.tiles.filter(t => t.status != STATUS_BOMB_END)
			.forEach(t => {
				if (t.bomb) {
					if (t.status==STATUS_DEFAULT) t.status = STATUS_BOMB;
				} else {
					if (t.status==STATUS_FLAGGED) t.status = STATUS_FLAGGED_FAILED;
				}
			});
	}

	findFirstReveal() {
		const props = this.props;
		const max = props.h * props.w;
		const tiles = this.state.tiles;

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
				let neighbors = getNeighbors(rnd, props.w, props.h);
				let hasBombs = neighbors.map(n => tiles[n].bomb).find(_=>_==true);

				if (!hasBombs) {
					tmpPos1 = rnd;

					let hasBombs2 = neighbors.flatMap(n=>getNeighbors(n, props.w, props.h)).map(n => tiles[n].bomb).find(_=>_==true);
					if (!hasBombs2) {
						pos = rnd;
						break;
					}
				}
			}
		}
		return pos;
	}
}




function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        let j = Math.floor(Math.random() * (i + 1));
        let temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}