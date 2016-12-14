package com.siteview.svgmap.models;

public final class svPoint {

	public float x;
	public float y;

	public  svPoint clone()
	{
		svPoint varCopy = new svPoint();

		varCopy.x = this.x;
		varCopy.y = this.y;

		return varCopy;
	}
}
