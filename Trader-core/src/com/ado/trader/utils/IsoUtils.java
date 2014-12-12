package com.ado.trader.utils;

import java.awt.Dimension;

import com.badlogic.gdx.math.Vector2;

/**
 * Iso utility.
 * 
 * <pre>
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 * 
 *       |----| isoTileWidth (pixels)
 *              _    
 *         /\   |
 *        /  \  | isoTileHeight (pixels)
 *        \  /  |
 *         \/   |
 *              -
 * 
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 * 
 *             isoWidth (pixels)
 *           |--------|
 * 
 *                |-> isoX (pixels)
 *    ------------------------
 *   |                        | _               _
 *   |   (row) 0 /\ 0 (col)   | |               |
 *   |          /  \          | v               |
 *   | (row) 1 /\  /\ 1 (col) |                 |
 *   |        /  \/  \        | isoY (pixels)   | isoHeight (pixels)
 *   |        \  /\  /        |                 |
 *   |         \/  \/         |                 |
 *   |          \  /          |                 |
 *   |           \/           |                 -
 *   |                        |
 *    ------------------------
 * 
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 * </pre>
 * 
 * @author  Christoph Aschwanden (http://www.noblemaster.com)
 * @since April 8, 2007
 * Modified
 */
public final class IsoUtils{
	/**
	 * Constructor.
	 */
	private IsoUtils() {
		// prevents instantiation.
	}

	/**
	 * Returns the x/y coordinate for the given column. The middle of the column/row
	 * will be assumed for x/y calcuation!
	 * 
	 * @param col  The column.
	 * @param row  The row.
	 * @param isoTileWidth  The iso width.
	 * @param isoTileHeight  The iso height.
	 * @return  The x/y coordinate.
	 */
	public static Vector2 getIsoXY(int col, int row, int isoTileWidth, int isoTileHeight) {
		return getIsoXY((float)(col +0.5), (float)(row +0.5), isoTileWidth, isoTileHeight);
	}

	/**
	 * Returns the x/y coordinate for the given column. 
	 * 
	 * @param col  The column. Column 0 is from [0, 1). Etc.
	 * @param row  The row. Row 0 is from [0, 1). Etc.
	 * @param isoTileWidth  The iso width.
	 * @param isoTileHeight  The iso height.
	 * @return  The x/y coordinate.
	 */
	public static Vector2 getIsoXY(float col, float row, int isoTileWidth, int isoTileHeight) {
		int isoHalfWidth = isoTileWidth / 2;
		int isoHalfHeight = isoTileHeight / 2;
		int isoX = (int)((col - row) * isoHalfWidth);
		int isoY = (int)((col + row) * isoHalfHeight);
		return new Vector2(isoX, isoY);
	}

	/**
	 * Returns the row and column for x and y.
	 * 
	 * @param isoX  X position.
	 * @param isoY  Y position.
	 * @param isoTileWidth  The iso width.
	 * @param isoTileHeight  The iso height.
	 * @return  The row and column. Column 0 is any where fisoTileHeightrom [0, 1). Ditto row.
	 */
	public static Vector2 getColRow(int isoX, int isoY, int isoTileWidth, int isoTileHeight) {
		float col = (((float)isoX) / ((float)isoTileWidth)) + (((float)isoY) / ((float)isoTileHeight));
		float row = (((float)isoY) / ((float)isoTileHeight)) - ((float)isoX) / ((float)isoTileWidth);
		return new Vector2(col-1, row);
	}

	/**
	 * Returns the size in pixels.
	 * 
	 * @param numCols  The number of rows.
	 * @param numRows  The number of columns.
	 * @param isoTileWidth  The iso width.
	 * @param isoTileHeight  The iso height.
	 * @return  The size.
	 */
	public static Dimension getIsoSize(int numCols, int numRows, int isoTileWidth, int isoTileHeight) {
		int width = ((numCols + numRows) * isoTileWidth) / 2;
		int height = ((numCols + numRows) * isoTileHeight) / 2;
		return new Dimension(width, height);
	}
}
