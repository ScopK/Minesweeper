import React from 'react';
import ReactDOM from 'react-dom';

import Grid from './Grid.jsx';

import { RANDOM_GENERATOR, SOLVABLE_GENERATOR } from '../grid-generator';


window.loadMS = (w,h,b) => {

	ReactDOM.render(
		<Grid w={w} h={h} bombs={b} firstReveal={true} auto={true} generator={SOLVABLE_GENERATOR} />,
		document.getElementById('main-container')
	);

};