package com.scop.org.minesweeper;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;


import com.scop.org.minesweeper.control.ScreenProperties;
import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.MenuOption;
import com.scop.org.minesweeper.generators.RandomCheckedGenerator;
import com.scop.org.minesweeper.generators.RandomGenerator;

import java.util.ArrayList;
import java.util.List;


public class MenuPanel extends View {
	List<MenuOption> options;
	MenuActivity activity;

	private float scrollPosition = 0;
	private float maxScroll;


	public MenuPanel(MenuActivity context) {
		super(context);
		activity = context;

		addOptions();
		updateWinValues();
	}

	public void updateWinValues(){
		float maxHeight=0;
		for (MenuOption option : options){
			option.setWindowValues(ScreenProperties.WIDTH, ScreenProperties.HEIGHT_BAR_EXCLUDED, ScreenProperties.DPI_W, ScreenProperties.DPI_H);
			Rect r = option.getRect(0);
			if (r.bottom>maxHeight){
				maxHeight = r.bottom;
			}
		}
		maxHeight += MenuOption.FACTOR_MARGIN_OUT_SIZE*ScreenProperties.DPI_H;
		maxScroll = ScreenProperties.HEIGHT_BAR_EXCLUDED -maxHeight;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		ScreenProperties.load(getContext());
		updateWinValues();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private MenuOption optionSelected=null;
	private int lastY;
	private boolean hasMoved = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x,y;
		switch(event.getActionMasked()){
			case MotionEvent.ACTION_DOWN:
				x = Math.round(event.getX());
				lastY = Math.round(event.getY());

				optionSelected = null;
				hasMoved = false;
				for (MenuOption option : options) {
					if (option.touchIsIn(x, lastY, scrollPosition)) {
						option.setHover(true);
						this.postInvalidate();
						optionSelected = option;
						break;
					}
				}
				return true;
			case MotionEvent.ACTION_UP:
				if (!hasMoved) {
					x = Math.round(event.getX());
					y = Math.round(event.getY());

					if (optionSelected != null && optionSelected.touchIsIn(x, y, scrollPosition)) {
						optionSelected.setHover(false);
						optionSelected.run();
					}
				} else {
					if (optionSelected != null) {
						optionSelected.setHover(false);
					}
				}
				optionSelected = null;
				return true;
			case MotionEvent.ACTION_MOVE:
				x = Math.round(event.getX());
				y = Math.round(event.getY());

				float scrollPositionInitial = scrollPosition;
				if (maxScroll<0) {
					scrollPosition -= lastY - y;
					lastY = y;

					if (scrollPosition > 0) scrollPosition = 0;
					else if (scrollPosition < maxScroll) scrollPosition = maxScroll;
				}

				if (scrollPositionInitial!=scrollPosition){
					hasMoved = true;
				}

				if (optionSelected!=null){
					optionSelected.setHover(
							optionSelected.touchIsIn(x, y, scrollPosition)
					);
				}
				this.postInvalidate();
				return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		canvas.drawColor(0xFF242433);

		for (MenuOption option : options){
			option.draw(canvas,scrollPosition);
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == VISIBLE){
			//int startString = State.getLastLoadedJSON()==null? R.string.menu_newgame : R.string.menu_continue;
			//options.get(0).setText(activity.getString(startString));
		}
	}

	public void addOptions(){
		int counter = 0;
		MenuActivity activity = (MenuActivity) getContext();

		options = new ArrayList();

		options.add(new MenuOption(activity.getString(R.string.menu_load), activity::loadGrid,
				counter++,0xff579f41,0xff61a94b));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_1),()-> activity.startGrid(4, 6, 5),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_2),()-> activity.startGrid(10, 10, 12),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_3),()-> activity.startGrid(10, 10, 25),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_4),()-> activity.startGrid(15, 15, 50),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_5),()-> activity.startGrid(25, 25, 100),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_6),()-> activity.startGrid(50, 50, 503),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_7),()-> activity.startGrid(6, 20, 40),
				counter++,0xff41769f,0xff4a7fa8));

		options.add(new MenuOption(activity.getString(R.string.menu_settings), activity::openSettings,
				counter++, 0xff828282,0xff949494));

		options.add(new MenuOption(activity.getString(R.string.menu_game_name_6)+" AI",()-> activity.startGrid(50, 50, 603, RandomCheckedGenerator.class),
				counter++,0xff579f41,0xff61a94b));

	}
}
