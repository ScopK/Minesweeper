package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.scop.org.minesweeper.control.ScreenProperties;

import java.util.function.Consumer;

/**
 * Button used on menus
 */
public class MenuOption {
	private String text;
	private int index;
	private Procedure procedure;
	private boolean hover = false;

	private int color = 0xff000000, hoverColor = 0xff666666;

	private static final float FACTOR_FONT_SIZE = 11f;
	private static final float FACTOR_MARGIN_IN_SIZE = 16f;
	public  static final float FACTOR_MARGIN_OUT_SIZE = 20f;
	private static final float FACTOR_MARGIN_BTW_SIZE = 10f;
	private static final float FACTOR_BORDER_SIZE = 1f;

	/**
	 * Constructor. Sets the parameters of the button
	 * @param text String to display
	 * @param procedure Action that will be performed
	 * @param index Position
	 * @param color Color of the button
	 * @param colorh Color of the button when it is being touched
	 */
	public MenuOption(String text, Procedure procedure, int index, int color, int colorh) {
		this.text = text;
		this.index = index;
		this.procedure = procedure;
		this.color = color;
		this.hoverColor = colorh;
	}
	/**
	 * Constructor. Sets the parameters of the button. Using default colors.
	 * @param text String to display
	 * @param procedure Action that will be performed
	 * @param index Position
	 */
	public MenuOption(String text, Procedure procedure, int index) {
		this.text = text;
		this.index = index;
		this.procedure = procedure;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Method to run the action attached to the button
	 */
	public void run(){
		this.procedure.invoke();
	}

	/**
	 * Check if the user is touching the button.
	 * @param x x position of the screen
	 * @param y y position of the screen
	 * @param scrollPosition scroll position
	 * @return
	 */
	public boolean touchIsIn(int x, int y, float scrollPosition){
		Rect r = getRect(scrollPosition);
		return r.contains(x,y);
	}

	/**
	 * Sets the button status to hover
	 * @param hover boolean
	 */
	public void setHover(boolean hover){
		this.hover = hover;
	}

	/**
	 * Checks if the button is hover
	 * @return boolean
	 */
	public boolean isHover(){
		return this.hover;
	}

	private float fontSize,marginInSize,marginOutSize,marginBtwSize,borderSize,textHeight,baseHeight,initHPoint;
	private int w;
	/**
	 * Calculates the main variables used to position the button. They are calculated here so we don't
	 * have to calculate it everytime in draw()
	 * @param w screen width
	 * @param h screen height
	 * @param dpiW screen width density
	 * @param dpiH screen height density
	 */
	public void setWindowValues(int w, int h, float dpiW, float dpiH){
		this.w = w;
		this.fontSize = ScreenProperties.fontSizeAdapted(FACTOR_FONT_SIZE);
		this.marginInSize = ScreenProperties.dpiValue(FACTOR_MARGIN_IN_SIZE);
		this.marginOutSize = ScreenProperties.dpiValue(FACTOR_MARGIN_OUT_SIZE);
		this.marginBtwSize = ScreenProperties.dpiValue(FACTOR_MARGIN_BTW_SIZE);
		this.borderSize = ScreenProperties.dpiValue(FACTOR_BORDER_SIZE);

		Paint textPaint = new Paint();
		textPaint.setTextSize(fontSize);

		Paint.FontMetrics fm = textPaint.getFontMetrics();
		this.textHeight = fm.descent - fm.ascent;

		this.baseHeight = textHeight + 2*marginInSize;
		this.initHPoint = index*(baseHeight+marginBtwSize);
	}

	/**
	 * Returns the bounds of the button, depending on the scroll position
	 * @param scrollPosition scroll position
	 * @return Rect bounds
	 */
	public Rect getRect(float scrollPosition){
		Rect r = new Rect();

		r.left = Math.round(marginOutSize);
		r.top = Math.round(scrollPosition+initHPoint+marginOutSize);
		r.right = Math.round(w - marginOutSize);
		r.bottom = Math.round(scrollPosition+initHPoint+baseHeight+marginOutSize);

		return r;
	}

	/**
	 * Method to draw
	 * @param canvas canvas
	 * @param scrollPosition scroll Position
	 */
	public void draw(Canvas canvas, float scrollPosition){
		Rect r = getRect(scrollPosition);

		Paint myPaint = new Paint();
		myPaint.setColor(this.hover? this.hoverColor : this.color);
		canvas.drawRect(r, myPaint);

		myPaint.setColor(0xff000000);
		myPaint.setStrokeWidth(borderSize);
		myPaint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(r, myPaint);

		Paint textPaint = new Paint();
		textPaint.setColor(0xffffffff);
		textPaint.setTextSize(fontSize);
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text, w / 2, scrollPosition+initHPoint + marginInSize + marginOutSize + fontSize, textPaint);
	}


	@FunctionalInterface
	public interface Procedure {
		void invoke();
	}
}
