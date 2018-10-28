package com.scop.org.minesweeper.elements;

import com.scop.org.minesweeper.generators.GridGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Grid implements Serializable {

	private List<Tile> grid;
	private int w;
	private int h;
	private int bombs;
	private Class<? extends GridGenerator> generatorClass;

	private Grid(){}

	public Grid(int w, int h, int bombs, Class<? extends GridGenerator> generatorClass){
		this.h = h;
		this.w = w;
		this.bombs = bombs;
		this.generatorClass = generatorClass;
	}

	public void generate(GridGenerator.FinishCallback callback){
		grid = new ArrayList<>();
		try {
			GridGenerator gen = generatorClass.newInstance();
			gen.generateNewGrid(this, bombs, callback);

		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
	}

	// GETTERS-SETTERS

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public List<Tile> getGrid() {
		return grid;
	}

	public int getBombs() {
		return bombs;
	}

	public String getGeneratorClass(){
		return generatorClass.getName();
	}

	public Grid startNewGame(GridGenerator.FinishCallback callback){
		Grid g = new Grid(w, h, bombs, generatorClass);
		g.generate(callback);
		return g;
	}


	@SuppressWarnings("unchecked")
	public static Grid jsonGrid(JSONObject obj) throws JSONException {
		try {
			Grid grid = new Grid();

			grid.h = obj.getInt("h");
			grid.w = obj.getInt("w");
			grid.generatorClass = (Class<? extends GridGenerator>) Class.forName(obj.getString("g"));

			List<Tile> tiles = new ArrayList<>();
			int bombs = 0;
			String stringGrid = obj.getString("ts");
			for (int i=0; i<stringGrid.length(); i++){
				Tile t = null;

				switch (stringGrid.charAt(i)){
					case 'B': t = new Tile(i%grid.w, i/grid.w, Tile.Status.BOMB); break;
					case ' ': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A0); break;
					case '1': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A1); break;
					case '2': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A2); break;
					case '3': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A3); break;
					case '4': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A4); break;
					case '5': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A5); break;
					case '6': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A6); break;
					case '7': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A7); break;
					case '8': t = new Tile(i%grid.w, i/grid.w, Tile.Status.A8); break;
					case 'f': t = new Tile(i%grid.w, i/grid.w, Tile.Status.FLAG); break;
					case 'c': t = new Tile(i%grid.w, i/grid.w, Tile.Status.COVERED); break;
					case 'F':
						t = new Tile(i%grid.w, i/grid.w, Tile.Status.FLAG);
						t.plantBomb();
						bombs++;
						break;
					case 'C':
						t = new Tile(i%grid.w, i/grid.w, Tile.Status.COVERED);
						t.plantBomb();
						bombs++;
						break;
				}
				if (t != null) tiles.add(t);
			}
			grid.bombs = bombs;
			grid.grid = tiles;
			return grid;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Grid clone(){
		Grid grid = new Grid();

		grid.w = w;
		grid.h = h;
		grid.bombs = bombs;
		grid.generatorClass = generatorClass;
		grid.grid = new ArrayList<>();

		this.grid.forEach(t-> grid.grid.add(t.clone()));

		return grid;
	}
}
