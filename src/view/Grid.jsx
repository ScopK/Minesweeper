import React from 'react';

import solve from '../solver/grid-solver';

import Tile from './Tile.jsx';
import { fillNear, getNeighbors, revealMass } from '../control';
import { STATUS_DEFAULT, STATUS_BOMB, STATUS_BOMB_END, STATUS_FLAGGED, STATUS_FLAGGED_FAILED, statusRevealed } from '../status';

const EVENT_END_GAME = 0;
const EVENT_REVEALED_TILE = 1;
const EVENT_GET_NEAR_FLAG = 2;

const GAME_STATUS_PLAYING = 0;
const GAME_STATUS_LOSE = 1;
const GAME_STATUS_WIN = 2;

export default class Grid extends React.Component {
	constructor(props) {
		super(...arguments);

		props.bombsIdx = [];

		this.gameStatus = GAME_STATUS_PLAYING;

		this.state = {
			tiles: []
		};

		let { w, h, bombsIdx, bombs } = props;
		let { tiles } = this.state;
		let max = w * h;

		do {
			bombsIdx = [];
			bombs = props.bombs;

			while (bombs > 0) {
				let pos = Math.floor(Math.random() * max)
				if (bombsIdx.indexOf(pos) < 0){
					bombsIdx.push(pos);
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
						bomb: bombsIdx.indexOf(pos) >= 0,
						alt: Math.floor(Math.random()*4)+1,
						status: STATUS_DEFAULT,
					});
				}
			}

			fillNear(tiles, props);

			if (props.firstReveal) {
				revealMass(
						tiles,
						this.firstOpen = this.findFirstReveal(),
						props
					);
			}

		} while (!solve(tiles, props));
	}

	render(){
		return (
			<div id="grid-container" className={this.props.skin || 'default'} style={{width: "1500px", height: "800px"}}>
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
		if (this.gameStatus != GAME_STATUS_PLAYING) {
			//this.reset();
			window.location.reload();
			return;
		}

		const tiles = this.state.tiles;
		const tile = tiles[position];
		const status = tile.status;


		if (status == STATUS_FLAGGED) {
			tile.status = STATUS_DEFAULT;

		} else if (!statusRevealed(tile.status)) {
			tile.status = STATUS_FLAGGED;

			if (this.props.auto) {
				this.autoStack = getNeighbors(position, this.props);
				if (!this.revealAuto()){
					this.gameOver();
				}
			}

		} else {
			let neighbors = getNeighbors(position, this.props);
			//let flagged = neighbors.map(n => tiles[n]).reduce((acc,t) => t.status==STATUS_FLAGGED? acc+1 : acc, 0);
			let flagged = neighbors.map(n => tiles[n]).filter(t => t.status==STATUS_FLAGGED).length;

			if (flagged >= tile.near) {
				if (!revealMass(tiles, position, this.props, true)){
					this.gameOver();
				}
			}
		}

		this.setState(this.state);
	}

	handleContextMenu(position, event) {
		event.preventDefault();

		const tiles = this.state.tiles;
		const tile = tiles[position];
		let status = tile.status;

		if (status == STATUS_FLAGGED || this.gameStatus != GAME_STATUS_PLAYING) {
			return;
		}

		if (!statusRevealed(status)) {
			if (!revealMass(tiles, position, this.props)) {
				this.gameOver();
			}

			this.setState({ tiles: tiles });
		}
	}

	revealAuto() {
		let tiles = this.state.tiles;

		let revealId = Math.random();
		let lost = 0;
		let position;

		while ( (position = this.autoStack.pop()) != undefined) {
			let tile = tiles[position];


			if (tile.near && tile.revealId != revealId) {

				let flagged = 0

				let coveredNeighbors = getNeighbors(position, this.props).filter(n => {
					let tile = tiles[n];
					if (tile.status==STATUS_FLAGGED) flagged++;
					else if (tile.status==STATUS_DEFAULT) return true;
				});

				if (statusRevealed(tile.status) && flagged >= tile.near) {
					tile.revealId = revealId;

					if (!revealMass(tiles, position, this.props, true)) {
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
		const max = this.props.h * this.props.w;
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
				let neighbors = getNeighbors(rnd, this.props);
				let hasBombs = neighbors.map(n => tiles[n].bomb).find(_=>_==true);

				if (!hasBombs) {
					tmpPos1 = rnd;

					let hasBombs2 = neighbors.flatMap(n=>getNeighbors(n, this.props)).map(n => tiles[n].bomb).find(_=>_==true);
					if (!hasBombs2) {
						pos = rnd;
						break;
					}
				}
			}
		}
		return pos;
	}

	reset() {
		this.state.tiles.forEach(tile => {
			tile.status = STATUS_DEFAULT;
		});


		if (this.props.firstReveal) {
			this.revealMass(this.firstOpen);
		}

		this.gameStatus = GAME_STATUS_PLAYING;

		this.setState({ tiles: this.state.tiles });
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