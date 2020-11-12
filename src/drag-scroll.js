export default function dragged(reEnable = false) {
	if (_dragged) {
		_dragged = !reEnable;
		return true;
	}
	return false;
}


const body = document.body;

body.onmousedown = mouseDown;
body.onmouseup = mouseUp;


// DRAG
var _dragXi = 0,
	_dragYi = 0,
	_dragging = false,
	_dragMoving = false,
	_dragged = false;


function mouseDown(ev){
	_dragXi = ev.clientX;
	_dragYi = ev.clientY;
	_dragMoving = false;
	_dragged = false;
	_dragging = true;
	body.onmousemove = mouseMove;
}
function mouseUp(ev){
	_dragging = false;
	_dragMoving = false;
	body.onmousemove = undefined;
}


function mouseMove(ev){
	if (ev.buttons!=1){
		mouseUp(ev);
		return;
	}
	if (_dragging){
		var x = ev.clientX, y = ev.clientY;
		var dx = _dragXi-x, dy = _dragYi-y;
		if (_dragMoving || dx > 10 || dy > 10 || dx < -10 || dy < -10){
			_dragMoving = true;
			_dragged = true;

			window.scrollBy(dx,dy);

			//var left = parseInt(grid.style.left.replace(/px/,''));
			//grid.style.left = (left-dx)+"px";
//
			//var top = parseInt(grid.style.top.replace(/px/,''));
			//grid.style.top = (top-dy)+"px";

			_dragXi = x;
			_dragYi = y;
		}
	}
}
