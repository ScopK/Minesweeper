var TILE_SIZE = 50,
	STATE_UNDISCOVERED = -1,
	STATE_NEAR1 = 0,
	STATE_NEAR2 = 1,
	STATE_NEAR3 = 2,
	STATE_NEAR4 = 3,
	STATE_NEAR5 = 4,
	STATE_NEAR6 = 5,
	STATE_NEAR7 = 6,
	STATE_NEAR8 = 7,
	STATE_BOMB = 8,
	STATE_BOMB_END = 9,
	STATE_FLAGGED = 10,
	STATE_FLAGGED_FAILED = 11,
	STATE_EMPTY = 12;

function getConfig(state){
	var covered = "covered";
	var noCovered = "";
	switch (state){
		case STATE_UNDISCOVERED:
			return [covered,"none"]; break;
		case STATE_NEAR1:
			return [noCovered,"n1"]; break;
		case STATE_NEAR2:
			return [noCovered,"n2"]; break;
		case STATE_NEAR3:
			return [noCovered,"n3"]; break;
		case STATE_NEAR4:
			return [noCovered,"n4"]; break;
		case STATE_NEAR5:
			return [noCovered,"n5"]; break;
		case STATE_NEAR6:
			return [noCovered,"n6"]; break;
		case STATE_NEAR7:
			return [noCovered,"n7"]; break;
		case STATE_NEAR8:
			return [noCovered,"n8"]; break;
		case STATE_BOMB:
			return [noCovered,"mine"]; break;
		case STATE_BOMB_END:
			return [noCovered,"mine-explode"]; break;
		case STATE_FLAGGED:
			return [covered,"flag"]; break;
		case STATE_FLAGGED_FAILED:
			return [covered,"flag-wrong"]; break;
		case STATE_EMPTY:
			return ["","empty"]; break;
	}
}

var body = document.body,
	GRID_DATA = [],
	GRID_WIDTH = 0,
	GRID_HEIGHT = 0,
	GRID_MINES = 0,
	GRID_GAMEOVER = false;

var main_container = document.createElement("div");
main_container.id = "main-container";
body.appendChild(main_container);

var grid_container = document.createElement("div");
grid_container.id = "grid-container";
body.onmousedown = mouseDown;
body.onmouseup = mouseUp;
body.onkeyup = function(ev){
	if (ev.keyCode == 82){ //R
		resetMS();
		closeDialogs();
	}
}
main_container.appendChild(grid_container);

var grid = document.createElement("div");
grid.id = "grid-scroll";
grid_container.appendChild(grid);

function createTile(x,y,state){
	var id = Math.floor(Math.random()*4)+1;

	var classes = getConfig(state);
	var tile = document.createElement("span");
	tile.className = "alt"+id +" tile "+ classes[0];
	//tile.setAttribute("x-pos",x);
	tile.style.left = (x*TILE_SIZE)+"px";

	//tile.setAttribute("y-pos",y);
	tile.style.top = (y*TILE_SIZE)+"px";

	tile.setAttribute("state",state);
	grid.appendChild(tile);

	var textra = document.createElement("span");
	textra.className = classes[1];
	tile.appendChild(textra);

	return tile;
}

function editTile(tile,state){
	tile.state = state;

	var classes = getConfig(state);
	var e = tile.element;
	var ee = e.children[0];
	e.className = e.className.substr(0,10)+classes[0];
	ee.className = classes[1];
}

// DRAG
var dragXi = 0,
	dragYi = 0,
	dragging = false,
	dragMoving = false,
	dragged = false;
function mouseDown(ev){
	dragXi = ev.clientX;
	dragYi = ev.clientY;
	dragMoving = false;
	dragged = false;
	dragging = true;
	body.onmousemove = mouseMove;
}
function mouseUp(ev){
	dragging = false;
	dragMoving = false;
	body.onmousemove = undefined;
}
function mouseMove(ev){
	if (ev.buttons!=1){
		mouseUp(ev);
		return;
	}
	if (dragging){
		var x = ev.clientX, y = ev.clientY;
		var dx = dragXi-x, dy = dragYi-y;
		if (dragMoving || dx > 10 || dy > 10 || dx < -10 || dy < -10){
			dragMoving = true;
			dragged = true;

			window.scrollBy(dx,dy);

			//var left = parseInt(grid.style.left.replace(/px/,''));
			//grid.style.left = (left-dx)+"px";
//
			//var top = parseInt(grid.style.top.replace(/px/,''));
			//grid.style.top = (top-dy)+"px";

			dragXi = x;
			dragYi = y;
		}
	}
}

function clickTile(leftClick,ev){
	if (dragged){
		dragged = false;
		return false;
	}
	if (!GRID_GAMEOVER){
		switch (this.state){
			case STATE_UNDISCOVERED:
				if (leftClick)
					reveal(this);
				else {
					editTile(this,STATE_FLAGGED);
					this.flag = true;
				}
				break;
			case STATE_FLAGGED:
				if (!leftClick){
					editTile(this,STATE_UNDISCOVERED);
					this.flag = false;
				}
				break;
			case STATE_NEAR1:
			case STATE_NEAR2:
			case STATE_NEAR3:
			case STATE_NEAR4:
			case STATE_NEAR5:
			case STATE_NEAR6:
			case STATE_NEAR7:
				areaReveal(this);
				break;
		}
	}
	return false;
}

function loadMS(width,height,mines){
	GRID_DATA = [];
	window.GRID_DATA = GRID_DATA;
	grid_container.style.width = (width*TILE_SIZE)+"px";
	grid_container.style.height = (height*TILE_SIZE)+"px";
	GRID_WIDTH = width;
	GRID_HEIGHT = height;
	GRID_MINES = mines;
	GRID_GAMEOVER = false;
	for (var i = 0; i < width; i++) {
		GRID_DATA[i] = [];
		for (var j = 0; j < height; j++) {
			var e = createTile(i,j,STATE_UNDISCOVERED);
			GRID_DATA[i][j] = {
				x: i,
				y: j,
				element: e,
				mine: false,
				flag: false,
				state: STATE_UNDISCOVERED,
				c: 0,
			}
			e.onclick = clickTile.bind(GRID_DATA[i][j],true);
			e.oncontextmenu = clickTile.bind(GRID_DATA[i][j],false);
		}
	}

	for (var i = 0; i < mines; i++) {
		var x = Math.floor(Math.random()*width);
		var y = Math.floor(Math.random()*height);
		if (GRID_DATA[x][y].mine){
			i--;
		} else {
			GRID_DATA[x][y].mine = true;
		}
	}

	for (var i = 0; i < width; i++) {
		for (var j = 0; j < height; j++) {
			var tile = GRID_DATA[i][j];
			if (!tile.mine){
				var ns = getNeighbors(i,j);
				for (var k = 0; k < ns.length; k++) {
					if (ns[k].mine)
						tile.c++;
				}
			}
		}
	}
}

function blur(add){
	grid_container.style.filter = add? "blur(2px)":"none";
}

// MINESWEEPER LOGIC

function getNeighbors(x,y){
	var neighbors = [];
	var ll = x > 0,
		lr = x < GRID_WIDTH-1,
		lt = y > 0,
		lb = y < GRID_HEIGHT-1;

	if (ll)	{
					neighbors.push(GRID_DATA[x-1][y]);
		if (lt) 	neighbors.push(GRID_DATA[x-1][y-1]);
		if (lb) 	neighbors.push(GRID_DATA[x-1][y+1]);
	}
	if (lr){
					neighbors.push(GRID_DATA[x+1][y]);
		if (lt) 	neighbors.push(GRID_DATA[x+1][y-1]);
		if (lb) 	neighbors.push(GRID_DATA[x+1][y+1]);
	}
	if (lt) 	neighbors.push(GRID_DATA[x][y-1]);
	if (lb) 	neighbors.push(GRID_DATA[x][y+1]);
	return neighbors;
}

function reveal(tile){
	if (tile.state == STATE_UNDISCOVERED){
		if (tile.mine){
			editTile(tile, STATE_BOMB_END);
			GRID_GAMEOVER = true;
			blur(true);
			showDialog("gameover");

			for (var i = 0; i < GRID_WIDTH; i++) {
				for (var j = 0; j < GRID_HEIGHT; j++) {
					var t = GRID_DATA[i][j];
					if (t.mine){
						if (t != tile) editTile(t, STATE_BOMB);
					} else if(t.state == STATE_FLAGGED) {
						editTile(t, STATE_FLAGGED_FAILED);
					}
				}
			}

		} else {
			if (tile.c==0){
				editTile(tile, STATE_EMPTY);
				//MassReveal:
				var ns = getNeighbors(tile.x,tile.y);
				for (var i = 0; i < ns.length; i++) {
					reveal(ns[i]);
				}
			} else {
				editTile(tile, tile.c-1);
			}
		}
	}
}

function areaReveal(tile){
	var c=0,count = tile.state+1;

	var ns = getNeighbors(tile.x,tile.y);
	for (var i = 0; i < ns.length; i++) {
		if (ns[i].flag){
			c++;
		}
	}
	if (c == count){
		for (var i = 0; i < ns.length; i++) {
			reveal(ns[i]);
		}
	}
}

function addActions(selector,event,funct){
	var s = document.querySelectorAll(selector);
	for (var i=0;i<s.length;){
		s[i++][event] = funct;
	}
}

// To window
window.loadMS = loadMS;
window.resetMS = function(){
	loadMS(GRID_WIDTH,GRID_HEIGHT,GRID_MINES);
}