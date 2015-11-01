package com.ado.trader.map;

public class TileMask {
	int tileID;
	int dir;
	int overlay;

	public TileMask(int tileID, int overlay, int dir) {
		this.tileID = tileID;
		this.dir = dir;
		this.overlay = overlay;
	}
	public int getMask() {
		return overlay;
	}
	public void setMask(int overlay) {
		this.overlay = overlay;
	}
	public int getTileID() {
		return tileID;
	}
	public int getDir() {
		return dir;
	}
	public void setDir(int dir){
		this.dir = dir;
	}
}
