package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.utils.ActivityController;
import com.scop.org.minesweeper.utils.GridUtils;
import com.scop.org.minesweeper.control.MainLogic;
import com.scop.org.minesweeper.control.CanvasWrapper;
import com.scop.org.minesweeper.control.GridDrawer;
import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class GamePanel extends View{

	private MainLogic logic = null;
	private Hud hud;
	private ScaleGestureDetector scaleDetector;
	private GestureDetector gestureDetector;
	private Vibrator vibrator;

	private float dragXPos = 0;
	private float dragYPos = 0;

	private boolean isResizing = false;
	private boolean isMoving = false;

	public GamePanel(Context context) {
		super(context);

		//make gamePanel focusable so it can handle events
		setFocusable(true);

		this.vibrator = (Vibrator) GamePanel.this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		this.scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		this.scaleDetector.setQuickScaleEnabled(false);
		this.gestureDetector = new GestureDetector(context, new GestureListener());
		this.gestureDetector.setIsLongpressEnabled(true);
	}

	public void setNewGrid(Grid grid){
		setGrid(new MainLogic(grid), 0);

		if (Settings.getInstance().isFirstOpen() && logic.isAllCovered()) {
			Tile t = GridUtils.findSafeOpenTile(grid);
			logic.reveal(t);
			CanvasWrapper.focus(t);
		} else {
			Optional<Tile> t = grid.getGrid().stream().filter(ti->ti.getStatus() == Tile.Status.A0).findFirst();
			if (t.isPresent()) {
				CanvasWrapper.focus(t.get());
			}
		}
	}

	public void setGrid(MainLogic logic, int seconds){
		this.logic = logic;
		if (hud != null) {
			hud.stopTimer();
		}
		hud = new Hud(logic, this);
		hud.setTime(seconds);
		hud.startTimer();
		logic.setFinishEvent(userWin -> hud.stopTimer());

		CanvasWrapper.setContentDimensions(logic.getGrid().getW()*GridDrawer.getTileSize(),
				logic.getGrid().getH()*GridDrawer.getTileSize());
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		CanvasWrapper xCanvas = new CanvasWrapper(canvas);

		if (logic != null)
			GridDrawer.draw(xCanvas, logic.getGrid());

		xCanvas.end();

		if (hud != null) {
			hud.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		gestureDetector.onTouchEvent(e);
		scaleDetector.onTouchEvent(e);

		if (!scaleDetector.isInProgress()) {
			int action = e.getActionMasked();
			switch (action) {
				case MotionEvent.ACTION_DOWN:
					dragXPos = e.getX();
					dragYPos = e.getY();
					break;

				case MotionEvent.ACTION_MOVE:
					if (this.isResizing)
						return true;

					float X = e.getX();
					float Y = e.getY();

					int dx = Math.round(X - dragXPos);
					int dy = Math.round(Y - dragYPos);

					if (isMoving || Math.abs(dx)+Math.abs(dy)>35){
						dragXPos = X;
						dragYPos = Y;

						CanvasWrapper.translate(dx, dy);

						this.isMoving = true;
						postInvalidate();
					}
					break;

				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_UP:
					this.isMoving = false;
					this.isResizing = false;
					break;
				case MotionEvent.ACTION_CANCEL:
					break;
			}
		}
		return true;
	}

	@Override
	public void onWindowVisibilityChanged(int visibility) {
		switch (visibility){
			case View.VISIBLE:
				if (hud!=null) hud.resumeTimer();
				break;
			case View.GONE:
			case View.INVISIBLE:
				if (hud!=null) hud.pauseTimer();
				break;
		}
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			isResizing = true;

			float ratio = detector.getScaleFactor();
			CanvasWrapper.zoom(ratio);

			postInvalidate();
			return true;
		}
	}
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public void onLongPress(MotionEvent e) {
			if (isMoving) return;

			Tile t = GridUtils.getTileByScreenCoords(logic.getGrid(), e.getX(), e.getY());
			if (t != null){
				boolean succeeded = logic.alternativeAction(t);

				if (succeeded) {
					vibrator.vibrate(VibrationEffect.createOneShot(1, 1));//VibrationEffect.DEFAULT_AMPLITUDE));
				}
			}
			postInvalidate();
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (logic.isGameOver()) {
				// restart:
				ActivityController.loadGrid(logic.getGrid(), (Activity) getContext());
				postInvalidate();
				return true;
			}

			Tile t = GridUtils.getTileByScreenCoords(logic.getGrid(), e.getX(), e.getY());
			if (t != null) {
				logic.mainAction(t);
			}
			postInvalidate();
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			if (e.getAction()!=MotionEvent.ACTION_UP) return false;
			if (isMoving) return false;
			return this.onSingleTapUp(e);
		}
	}


	public void saveState(){
		String saveStatePath = new ContextWrapper(getContext()).getFilesDir().getPath()+"/"+Settings.FILENAME;

		if (logic == null || logic.isGameOver()){
			new File(saveStatePath).delete();
			return;
		}
		try {
			JSONObject obj = GridUtils.getJsonStatus(logic.getGrid(), hud.getTime());

			PrintWriter out = new PrintWriter(saveStatePath);
			out.print(obj.toString());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean loadState(){
		String saveStatePath = new ContextWrapper(getContext()).getFilesDir().getPath()+"/"+Settings.FILENAME;

		if (new File(saveStatePath).exists()) {

			try {
				BufferedReader br = new BufferedReader(new FileReader(saveStatePath));
				String line = br.readLine();
				br.close();

				JSONObject obj = new JSONObject(line);

				Grid bareGrid = Grid.jsonGrid(obj);
				MainLogic logic = GridUtils.calculateLogicFromBareGrid(bareGrid);

				setGrid(logic, obj.getInt("t"));
				CanvasWrapper.set((float)obj.getDouble("x"), (float)obj.getDouble("y"), (float)obj.getDouble("s"));

				return true;

			} catch (JSONException | IOException e){
				System.err.println("Couldn't load \""+saveStatePath+"\"");
			}
		}
		return false;
	}
}
