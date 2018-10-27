package com.scop.org.minesweeper.elements;

import java.io.Serializable;
import java.util.Objects;

public class Tile implements Serializable {
	public enum Status {
		COVERED, A0, A1, A2, A3, A4, A5, A6, A7, A8, FLAG, FLAG_FAIL, BOMB, BOMB_FINAL
	}

	private int x,y;
	private Status status;
	private boolean hasBomb = false;
	private int flaggedNear = 0;
	private int bombsNear = 0;

	public Tile(int x, int y, Status status){
		this.x = x;
		this.y = y;
		this.status = status;
	}

	public Tile(int x, int y) {
		this(x,y,Status.COVERED);
	}

	public void plantBomb(){
		hasBomb = true;
	}

	public void addFlaggedNear(){
		flaggedNear++;
	}

	public void removeFlaggedNear(){
		flaggedNear--;
	}

	public int getFlaggedNear() {
		return flaggedNear;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean hasBomb() {
		return hasBomb;
	}

	public void hasBombNear() {
		bombsNear++;
	}

	public int getBombsNear() {
		return bombsNear;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		String str;
		switch(status){
			case COVERED:    str = "#"; break;
			case A0:         str = "·"; break;
			case A1:         str = "1"; break;
			case A2:         str = "2"; break;
			case A3:         str = "3"; break;
			case A4:         str = "4"; break;
			case A5:         str = "5"; break;
			case A6:         str = "6"; break;
			case A7:         str = "7"; break;
			case A8:         str = "8"; break;
			case FLAG:       str = "F"; break;
			case BOMB:
			case BOMB_FINAL: str = "X"; break;
			default:         str = " "; break;
		}
		return str;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, hasBomb, bombsNear, super.hashCode());
	}
}
