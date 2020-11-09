import React from 'react';

export const TILE_SIZE = 50;

export default class Tile extends React.Component {
	render(){
		let { x,y,status,alt,onClick,onContextMenu,p } = this.props;
		return (
			<span
				className={ `tile ${status} alt${alt}` }
				style={ {left: (x*TILE_SIZE)+"px", top: (y*TILE_SIZE)+"px"} }
				onClick=      { ev => onClick(p, ev) }
				onContextMenu={ ev => onContextMenu(p, ev) }>
				<span></span>
			</span>
		)
	}
}