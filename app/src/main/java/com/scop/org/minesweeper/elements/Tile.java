package com.scop.org.minesweeper.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
	private List<Character> customFlag = null;

	private Tile(){}

	public Tile(int x, int y, Status status){
		this.x = x;
		this.y = y;
		this.status = status;
	}

	public Tile(int x, int y) {
		this(x,y,Status.COVERED);
	}

	public void plantBomb(){
		plantBomb(true);
	}
	public void plantBomb(boolean value){
		hasBomb = value;
	}

	public void defuseBomb(){
		hasBomb = false;
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

	public void doesntHaveBombNear() {
		bombsNear--;
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

	public boolean hasCustomFlag(char customFlag) {
		if (this.customFlag == null)
			return false;
		return this.customFlag.contains(customFlag);
	}

	public void removeCustomFlag(char customFlag) {
		if (this.customFlag != null) {
			this.customFlag.remove((Character)customFlag);
		}
	}

	public void setCustomFlag(char customFlag) {
		if (this.customFlag == null)
			this.customFlag = new ArrayList<>();
		this.customFlag.add(customFlag);
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

	public Tile clone(){
		Tile tile = new Tile();

		tile.x = x;
		tile.y = y;
		tile.status = status;
		tile.hasBomb = hasBomb;
		tile.flaggedNear = flaggedNear;
		tile.bombsNear = bombsNear;
		tile.customFlag = customFlag;

		return tile;
	}

	public boolean isCovered(){
		return status == Status.COVERED || status == Status.FLAG;
	}

	public boolean isUncovered(){
		return !isCovered();
	}

	public boolean isNumberVisible(){
		return !isCovered() && status != Status.A0;
	}
}
