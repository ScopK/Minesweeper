import React from 'react';

import Tile from './Tile.jsx';

const STATUS = {
	DEFAULT: 'cov def',
	NEAR: ['n0','n1','n2','n3','n4','n5','n6','n7','n8'],
	BOMB: 'cov b',
	BOMB_END: 'cov X',
	FLAGGED: 'cov f',
	FLAGGED_FAILED: 'cov F'
};
const statusRevealed = status => STATUS.NEAR.indexOf(status) >= 0;

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
					status: STATUS.DEFAULT,
				});
			}
		}

		if (props.firstReveal) {
			this.revealMass(
					this.firstOpen = this.findFirstReveal()
				);
		}
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

	getNeighbors(pos) {
		let { h, w } = this.props;

		let top = pos     % h == 0,
			bot = (pos+1) % h == 0,
			left = pos-h < 0,
			right = pos+h >= w*h;

		return [
				top?   -1 : pos-1,
				bot?   -1 : pos+1,
				left?  -1 : pos-h,
				right? -1 : pos+h,
				top || left?  -1 : pos-h-1,
				bot || left?  -1 : pos-h+1,
				top || right? -1 : pos+h-1,
				bot || right? -1 : pos+h+1]
			.filter(v => v >= 0);
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


		if (status == STATUS.FLAGGED) {
			tile.status = STATUS.DEFAULT;

		} else if (!statusRevealed(tile.status)) {
			tile.status = STATUS.FLAGGED;

			if (this.props.auto) {
				this.autoStack = this.getNeighbors(position);
				if (!this.revealAuto()){
					this.gameOver();
				}
			}

		} else {
			let neighbors = this.getNeighbors(position);
			let flagged = neighbors.map(n => tiles[n]).reduce((acc,t) => t.status==STATUS.FLAGGED? acc+1 : acc, 0);

			if (flagged >= tile.bombs) {
				if (!this.revealMass(position, true)){
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

		if (status == STATUS.FLAGGED || this.gameStatus != GAME_STATUS_PLAYING) {
			return;
		}

		if (!statusRevealed(status)) {
			if (!this.revealMass(position)) {
				this.gameOver();
			}

			this.setState({ tiles: tiles });
		}
	}

	revealMass(position, aggressiveReveal = false) {
		const tiles = this.state.tiles;
		const tile = tiles[position];

		if (tile.status == STATUS.FLAGGED) {
			return true;
		}
		if (!aggressiveReveal && statusRevealed(tile.status)) {
			return true;
		}

		if (tile.bomb) {
			tile.status = STATUS.BOMB_END;
			return false;
		}

		const neighbors = this.getNeighbors(position);

		const near = neighbors.map(p=>tiles[p]).filter(n=>n.bomb).length;
		tile.bombs = near;
		tile.status = STATUS.NEAR[near];

		if (!near || aggressiveReveal) {
			return neighbors.map(pos=>this.revealMass(pos)).indexOf(false) < 0;
		}
		return true;
	}

	revealAuto() {
		let tiles = this.state.tiles;

		let revealId = Math.random();
		let lost = 0;
		let position;

		while ( (position = this.autoStack.pop()) != undefined) {
			let tile = tiles[position];


			if (tile.bombs && tile.revealId != revealId) {

				let flagged = 0

				let coveredNeighbors = this.getNeighbors(position).filter(n => {
					let tile = tiles[n];
					if (tile.status==STATUS.FLAGGED) flagged++;
					else if (tile.status==STATUS.DEFAULT) return true;
				});


				if (flagged >= tile.bombs) {
					tile.revealId = revealId;

					if (!this.revealMass(position, true)) {
						this.autoStack = [];
						return false;
					}

					this.autoStack = this.autoStack.concat(coveredNeighbors.filter(n=> {
						let tile = tiles[n];
						return tile.bombs && tile.revealId != revealId;
					}));
				}
			}
		}

		return true;
	}

	gameOver() {
		this.gameStatus = GAME_STATUS_LOSE;
		this.state.tiles.filter(t => t.status != STATUS.BOMB_END)
			.forEach(t => {
				if (t.bomb) {
					if (t.status==STATUS.DEFAULT) t.status = STATUS.BOMB;
				} else {
					if (t.status==STATUS.FLAGGED) t.status = STATUS.FLAGGED_FAILED;
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
				let neighbors = this.getNeighbors(rnd); 
				let hasBombs = neighbors.map(n => tiles[n].bomb).find(_=>_==true);

				if (!hasBombs) {
					tmpPos1 = rnd;

					let hasBombs2 = neighbors.flatMap(n=>this.getNeighbors(n)).map(n => tiles[n].bomb).find(_=>_==true);
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
			tile.status = STATUS.DEFAULT;
			delete tile.bombs;
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