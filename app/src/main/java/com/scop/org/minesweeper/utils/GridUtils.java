package com.scop.org.minesweeper.utils;

import com.scop.org.minesweeper.control.MainLogic;
import com.scop.org.minesweeper.control.CanvasWrapper;
import com.scop.org.minesweeper.control.GridDrawer;
import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class GridUtils {



	public static List<Tile> getNeighbors(Grid grid, Tile tile){
		List<Tile> tiles = grid.getGrid();
		return getNeighborsIdx(grid, tile).stream()
				.map(tiles::get)
				.collect(Collectors.toList());
	}

	public static List<Integer> getNeighborsIdx(Grid grid, Tile tile){
		List<Integer> neighbors = new ArrayList<>();
		int w = grid.getW(),
			h = grid.getH(),
			idx = tile.getY()*w + tile.getX();

		// Test (w=7, h=3):
		// 0  1  2  3  4  5  6
		// 7  8  9 10 11 12 13
		//14 15 16 17 18 19 20

		boolean checkLeft = idx%w != 0;
		boolean checkTop = idx >= w;
		boolean checkRight = (idx+1)%w != 0;
		boolean checkBottom = idx < w*(h-1);

		if (checkLeft) {
			neighbors.add(idx-1); // L

			if (checkTop) {
				neighbors.add(idx-w); // T
				neighbors.add(idx-w-1); // TL
			}
			if (checkBottom) {
				neighbors.add(idx+w); // B
				neighbors.add(idx+w-1); // BL
			}
		}
		if (checkRight) {
			neighbors.add(idx+1); // R

			if (checkTop) {
				if (!checkLeft) neighbors.add(idx-w); // T
				neighbors.add(idx-w+1); // TR
			}
			if (checkBottom) {
				if (!checkLeft) neighbors.add(idx+w); // B
				neighbors.add(idx+w+1); // BR
			}
		}
		else if (!checkLeft) {
			if (checkTop)    neighbors.add(idx-w); // T
			if (checkBottom) neighbors.add(idx+w); // B
		}
		return neighbors;
	}

	public static Tile getTileByScreenCoords(Grid grid, float x, float y){
		int tileSize = GridDrawer.getTileSize();
		float scale = CanvasWrapper.getScale();
		float posX = CanvasWrapper.getPosX();
		float posY = CanvasWrapper.getPosY();

		return getTileByCoords(grid,
				Math.round((-posX + x) / scale) / tileSize,
				Math.round((-posY + y) / scale) / tileSize);
	}

	public static Tile getTileByCoords(Grid grid, int x, int y){
		int w = grid.getW(),
			idx = y*w + x;

		if (idx >= 0 && idx < grid.getGrid().size())
			return grid.getGrid().get(idx);

		return null;
	}

	public static Tile findSafeOpenTile(Grid grid) {
		Random rnd = new Random();
		List<Tile> tiles = grid.getGrid();
		Tile t;
		do {
			t = tiles.get(rnd.nextInt(tiles.size()));

		} while (t.hasBomb() || t.getBombsNear() > 0 || t.getStatus() != Tile.Status.COVERED);

		return t;
	}

	public static int findSafeOpenTileIdx(Grid grid) {
		Random rnd = new Random();
		List<Tile> tiles = grid.getGrid();
		Tile t;
		int idx;
		do {
			idx = rnd.nextInt(tiles.size());
			t = tiles.get(idx);

		} while (t.hasBomb() || t.getBombsNear() > 0 || t.getStatus() != Tile.Status.COVERED);

		return idx;
	}

	public static int getTileNumber(Tile tile){
		switch (tile.getStatus()){
			case A1: return 1;
			case A2: return 2;
			case A3: return 3;
			case A4: return 4;
			case A5: return 5;
			case A6: return 6;
			case A7: return 7;
			case A8: return 8;
		}
		return -1;
	}
	public static Tile.Status getTileStatus(int value){
		switch (value){
			case 1: return Tile.Status.A1;
			case 2: return Tile.Status.A2;
			case 3: return Tile.Status.A3;
			case 4: return Tile.Status.A4;
			case 5: return Tile.Status.A5;
			case 6: return Tile.Status.A6;
			case 7: return Tile.Status.A7;
			case 8: return Tile.Status.A8;
		}
		return Tile.Status.A0;
	}


	public static JSONObject getJsonStatus(Grid grid, int seconds) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("w", grid.getW());
			obj.put("h", grid.getH());
			obj.put("g", grid.getGeneratorClass());
			obj.put("x", CanvasWrapper.getPosX());
			obj.put("y", CanvasWrapper.getPosY());
			obj.put("s", CanvasWrapper.getScale());
			obj.put("t", seconds);
			obj.put("ts",
					grid.getGrid().stream()
						.map(t->{
							String c;
							switch(t.getStatus()) {
								case BOMB: c = "B"; break;
								case FLAG: c = t.hasBomb()? "F":"f"; break;
								case COVERED: c = t.hasBomb()? "C":"c"; break;
								case A1: c = "1"; break;
								case A2: c = "2"; break;
								case A3: c = "3"; break;
								case A4: c = "4"; break;
								case A5: c = "5"; break;
								case A6: c = "6"; break;
								case A7: c = "7"; break;
								case A8: c = "8"; break;
								default:
								case A0: c = " "; break;
							}
							return c;
						})
						.collect(Collectors.joining())
			);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static MainLogic calculateLogicFromBareGrid(Grid grid) {
		MainLogic logic = new MainLogic(grid);

		for (Tile t : grid.getGrid()) {
			if (t.hasBomb()) {
				GridUtils.getNeighbors(grid, t).forEach(Tile::hasBombNear);
			}

			if (!t.isCovered()) {
				logic.addRevealedTiles();
			}
			if (t.getStatus() == Tile.Status.FLAG){
				GridUtils.getNeighbors(grid, t).forEach(Tile::addFlaggedNear);

				logic.addFlaggedBombs();
				if (t.hasBomb()) {
					logic.addCorrectFlaggedBombs();
				}
			}
		}

		return logic;
	}
}
