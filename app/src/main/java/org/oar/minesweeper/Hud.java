package org.oar.minesweeper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import org.oar.minesweeper.control.MainLogic;
import org.oar.minesweeper.control.ScreenProperties;
import org.oar.minesweeper.control.Settings;

public class Hud {
	private static final float FONT_SIZE = 9;
	private static final float MARGIN = 10;

	private float fontHeight;
	private float margin;

	private MainLogic logic;
	private View view;

	private Paint bg,textStyle;

	private Timer timer;
	private int startingTime=0;
	protected String timeStr = "";
	protected boolean showTime = false;

	public Hud(MainLogic logic, View view) {
		this.logic = logic;
		this.view = view;
		showTime = Settings.INSTANCE.getShowTime();

		bg = new Paint();

		textStyle = new Paint(Paint.FILTER_BITMAP_FLAG);
		textStyle.setColor(Color.WHITE);
		textStyle.setTextSize(ScreenProperties.fontSizeAdapted(FONT_SIZE));

		Rect r = new Rect();
		textStyle.getTextBounds("W", 0, 1, r);

		fontHeight = r.height();
		margin = ScreenProperties.dpiValue(MARGIN);
	}

	public void draw(Canvas canvas) {
		boolean showTime = this.showTime;
		switch(logic.getStatus()){
			case PLAYING: bg.setColor(0xA0000000); break;
			case LOSE:
				bg.setColor(0xA0FF0000);
				showTime = true;
				break;
			case WIN:
				bg.setColor(0xA000FF00);
				showTime = true;
				break;
		}

		canvas.drawRect(0, ScreenProperties.INSTANCE.getHEIGHT()-margin*2-fontHeight,
				ScreenProperties.INSTANCE.getWIDTH(), ScreenProperties.INSTANCE.getHEIGHT(), bg);

		textStyle.setTextAlign(Paint.Align.LEFT);
		canvas.drawText("Open: "+(logic.getRevisedTiles())+"/"+logic.getTotalTiles(),
				margin, ScreenProperties.INSTANCE.getHEIGHT()-margin, textStyle);

		textStyle.setTextAlign(Paint.Align.RIGHT);
		String rightText = "";
		if (showTime) {
			rightText = timeStr + "   ";
		}
		rightText += logic.getFlaggedBombs() + "/" + logic.getGrid().getBombs();

		canvas.drawText(rightText, ScreenProperties.INSTANCE.getWIDTH()-margin,
				ScreenProperties.INSTANCE.getHEIGHT()-margin, textStyle);
	}

	public void startTimer(){
		if (timer!=null){
			timer.timerClose();
		}
		if (startingTime==0) {
			timer = new Timer();
		} else {
			timer = new Timer(startingTime);
		}
		timer.start();
	}

	public void resumeTimer(){
		showTime = Settings.INSTANCE.getShowTime();
		if (timer==null) return;
		timer.timeResume();
	}
	public void pauseTimer(){
		if (timer==null) return;
		timer.timePause();
	}
	public void restartTimer() {
		if (timer==null){
			startingTime=0;
			startTimer();
		} else {
			timer.restart();
		}
	}
	public void stopTimer() {
		if (timer==null) return;
		timer.timerClose();
		timer = null;
	}
	public int getTime(){
		return timer.getTime();
	}
	public void setTime(int seconds){
		if (timer==null)
			startingTime = seconds;
		else
			timer.setTime(seconds);
	}
	public void timerNotify(int time){
		int s = time / 10;
		int m = s / 60;
		int h = m / 60;
		s %= 60;
		m %= 60;
		timeStr = (h == 0 ? "" : (h + ":")) + m + ":" + (s < 10 ? "0" + s : s);
		if (showTime) {
			view.postInvalidate(0, (int) (ScreenProperties.INSTANCE.getHEIGHT()-margin-fontHeight),
					ScreenProperties.INSTANCE.getWIDTH(), ScreenProperties.INSTANCE.getHEIGHT());
		}
	}

	private class Timer extends Thread{
		long initTime=0,pausedTime=0;
		int initDSecs=0;
		boolean running = true;
		public Timer(){}
		public Timer(int dseconds){
			this.initDSecs = dseconds;
		}
		public int getTime(){
			return (int)((System.nanoTime()-initTime)/100000000);
		}
		public void setTime(int dseconds){
			initTime = System.nanoTime()-dseconds*100000000L;
			pausedTime=0;
		}
		public void restart(){
			running = true;
			initTime = System.nanoTime();
			initDSecs = 0;
			pausedTime = 0;
		}
		public void timePause(){
			if (pausedTime==0){
				pausedTime=System.nanoTime();
			}
		}
		public void timeResume(){
			if (pausedTime!=0) {
				initTime += (System.nanoTime() - pausedTime);
			}
			pausedTime=0;
		}
		public void timerClose(){
			running = false;
		}
		@Override
		public void run() {
			super.run();
			running = true;
			initTime = System.nanoTime()-(initDSecs*100000000L);
			pausedTime=0;
			while (running){
				if (pausedTime==0){
					Hud.this.timerNotify(getTime());
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
