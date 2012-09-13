package aurelienribon.slidinglayout;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SLConfig {
	private final SLPanel panel;
	private final Map<Component, Tile> tiles = new HashMap<Component, Tile>();
	private Grid rootGrid = new Grid(), currentGrid = rootGrid;
	private int hgap = 0, vgap = 0;

	public SLConfig(SLPanel panel) {
		this.panel = panel;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public SLConfig gap(int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
		return this;
	}

	public SLConfig col(float relativeWidth) {
		Column column = new Column();
		column.fixedWidth = false;
		column.relWidth = relativeWidth;
		column.w = 0;
		currentGrid.cols.add(column);
		return this;
	}

	public SLConfig col(int width) {
		Column column = new Column();
		column.fixedWidth = true;
		column.relWidth = 1;
		column.w = width;
		currentGrid.cols.add(column);
		return this;
	}

	public SLConfig row(float relativeHeight) {
		Row row = new Row();
		row.fixedHeight = false;
		row.relHeight = relativeHeight;
		row.h = 0;
		currentGrid.rows.add(row);
		return this;
	}

	public SLConfig row(int height) {
		Row row = new Row();
		row.fixedHeight = true;
		row.relHeight = 1;
		row.h = height;
		currentGrid.rows.add(row);
		return this;
	}

	public SLConfig beginGrid(int row, int col) {
		Grid grid = new Grid();
		grid.parent = currentGrid;
		grid.row = row;
		grid.col = col;
		currentGrid.tiles.add(grid);
		currentGrid = grid;
		return this;
	}

	public SLConfig endGrid() {
		currentGrid = currentGrid.parent;
		return this;
	}

	public SLConfig tile(int row, int col, Component cmp) {
		Tile tile = new Tile();
		tile.parent = currentGrid;
		tile.row = row;
		tile.col = col;
		currentGrid.tiles.add(tile);
		tiles.put(cmp, tile);
		return this;
	}

	// -------------------------------------------------------------------------
	// Package API
	// -------------------------------------------------------------------------

	static class Tile {
		public Grid parent;
		public int row, col;
		public int x, y, w, h;

		@Override
		public Tile clone() {
			Tile t = new Tile();
			t.parent = parent;
			t.row = row;
			t.col = col;
			t.x = x;
			t.y = y;
			t.w = w;
			t.h = h;
			return t;
		}
	}

	SLPanel getPanel() {
		return panel;
	}

	List<Component> getCmps() {
		return new ArrayList<Component>(tiles.keySet());
	}

	Tile getTile(Component cmp) {
		return tiles.get(cmp);
	}

	List<Tile> getTiles(List<Component> cmps) {
		List<Tile> ts = new ArrayList<Tile>();
		for (Component c : cmps) ts.add(tiles.get(c));
		return ts;
	}

	void placeAndRoute() {
		rootGrid.x = hgap;
		rootGrid.y = vgap;
		rootGrid.w = panel.getWidth()-hgap*2;
		rootGrid.h = panel.getHeight()-vgap*2;
		placeAndRoute(rootGrid);
	}

	// -------------------------------------------------------------------------
	// Private API
	// -------------------------------------------------------------------------

	private static class Grid extends Tile {
		public final List<Row> rows = new ArrayList<Row>();
		public final List<Column> cols = new ArrayList<Column>();
		public final List<Tile> tiles = new ArrayList<Tile>();
	}

	private static class Row {
		public boolean fixedHeight;
		public float relHeight;
		public int h;
	}

	private static class Column {
		public boolean fixedWidth;
		public float relWidth;
		public int w;
	}

	private void placeAndRoute(Grid grid) {
		// Place rows

		float totalRelHeight = 0;
		int totalHeight = grid.h - vgap * (grid.rows.size() - 1);

		for (Row r : grid.rows) {
			if (r.fixedHeight) totalHeight -= r.h;
			else totalRelHeight += r.relHeight;
		}

		for (Row r : grid.rows) {
			if (!r.fixedHeight) r.h = (int) (totalHeight * r.relHeight / totalRelHeight);
		}

		// Place columns

		float totalRelWidth = 0;
		int totalWidth = grid.w - hgap * (grid.cols.size() - 1);

		for (Column c : grid.cols) {
			if (c.fixedWidth) totalWidth -= c.w;
			else totalRelWidth += c.relWidth;
		}

		for (Column c : grid.cols) {
			if (!c.fixedWidth) c.w = (int) (totalWidth * c.relWidth / totalRelWidth);
		}

		// Place tiles

		int x = grid.x, y = grid.y;

		for (int iRow=0; iRow<grid.rows.size(); iRow++) {
			for (int iCol=0; iCol<grid.cols.size(); iCol++) {
				for (Tile t : grid.tiles) {
					if (t.row != iRow || t.col != iCol) continue;
					t.x = x;
					t.y = y;
					t.w = grid.cols.get(t.col).w;
					t.h = grid.rows.get(t.row).h;
					if (t instanceof Grid) placeAndRoute((Grid) t);
				}
				x += grid.cols.get(iCol).w + hgap;
			}
			x = grid.x;
			y += grid.rows.get(iRow).h + vgap;
		}
	}
}
