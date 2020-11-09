import React from 'react';
import ReactDOM from 'react-dom';

import Grid from './Grid.jsx';


window.loadMS = (w,h,b) => {

	ReactDOM.render(
		<Grid w={w} h={h} bombs={b} firstReveal={true} auto={true} />,
		document.getElementById('main-container')
	);

};