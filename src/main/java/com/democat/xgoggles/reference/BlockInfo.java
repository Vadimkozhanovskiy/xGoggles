// This class (structure?) is for holding the blocks x,y,z and color to draw.
// This gets copied and accessed by RenderTick to draw the boxes around found ores/blocks.

package com.democat.xgoggles.reference;

public class BlockInfo
{
	public int x, y, z;
	public int color;
	public String aspect;
	
	public BlockInfo(int bx, int by, int bz, int c, String aspect)
	{
		this.x = bx;
		this.y = by;
		this.z = bz;
		this.color = c;
		this.aspect = aspect;
	}
}
