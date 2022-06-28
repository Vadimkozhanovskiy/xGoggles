// Structure for damn near everything.
package com.democat.xgoggles.reference;

public class OreInfo
{
	public String oreName; // Clean name of this ore
	public int id;         // Id of this block
	public int meta;       // Metadata value of this block. 0 otherwise.
	public int color;	   // Color in 0xRRGGBB to draw.
	public boolean draw;   // Should we draw this ore?
	public String asptect;
	
	public OreInfo( String name, int id, int meta, int color, boolean draw, String aspect )
	{
		this.oreName = name;
		this.id = id;
		this.meta = meta;
		this.color = color;
		this.draw = draw;
		this.asptect = aspect;
	}
	
	public void disable() // Stop drawing this ore.
	{
		this.draw = false;
	}
	
	public void enable()  // Start drawing this ore.
	{
		this.draw = true;
	}
}
