package com.scop.org.minesweeper.generators.solver;

import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sketch implements Iterable<Tile>{
	private Grid grid;
	private ArrayList<Integer> numbered;
	private ArrayList<Integer> uncovered;

	public Sketch(Grid grid) {
		this.grid = grid.clone();
		this.numbered = new ArrayList<>();
		this.uncovered = new ArrayList<>();

		for (int i=0; i<getTiles().size(); i++){
			Tile t = getTile(i);
			if (t.isUncovered()){
				uncovered.add(i);
				if (t.getStatus() != Tile.Status.A0) {
					numbered.add(i);
				}
			}
		}
	}

	@Override
	public Iterator<Tile> iterator() {
		return grid.getGrid().iterator();
	}

	public List<Tile> getTiles() {
		return grid.getGrid();
	}

	public Tile getTile(int idx) {
		return getTiles().get(idx);
	}

	public Grid getGrid() {
		return grid;
	}

	public List<Integer> getNumbered(){
		return numbered;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getNumberedCopy(){
		return (List<Integer>) numbered.clone();
	}

	public List<Integer> getUncovered(){
		return uncovered;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getUncoveredCopy(){
		return (List<Integer>) uncovered.clone();
	}

	public void addUncovered(Integer i){
		uncovered.add(i);
	}

	public void addNumbered(Integer i){
		numbered.add(i);
		uncovered.add(i);
	}

	public void removeNumbered(Integer i){
		numbered.remove(i);
	}
}
