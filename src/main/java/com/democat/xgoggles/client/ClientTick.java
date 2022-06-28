package com.democat.xgoggles.client;

import java.util.ArrayList;
import java.util.List;

import com.democat.xgoggles.reference.BlockInfo;
import com.democat.xgoggles.reference.OreInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import com.democat.xgoggles.xGoggles;

public class ClientTick implements Runnable
{
	private final Minecraft mc = Minecraft.getMinecraft();
	private long nextTimeMs = System.currentTimeMillis();
	private final int delayMs = 200;
	private Thread thread = null;

	@SubscribeEvent
	public void tickEnd( TickEvent.ClientTickEvent event )
	{
		if ( (event.phase == TickEvent.Phase.END) && (mc.thePlayer != null) )
		{
			xGoggles.localPlyX = MathHelper.floor_double( mc.thePlayer.posX );
			xGoggles.localPlyY = MathHelper.floor_double( mc.thePlayer.posY );
			xGoggles.localPlyZ = MathHelper.floor_double( mc.thePlayer.posZ );

			if( xGoggles.drawOres && ((this.thread == null) || !this.thread.isAlive()) &&
			( (mc.theWorld != null) && (mc.thePlayer != null) ) ) // If we're in a world and want to start drawing create the thread.
			{
				this.thread = new Thread( this );
				this.thread.setDaemon( false );
				this.thread.setPriority( Thread.MAX_PRIORITY );
				this.thread.start();
			}
		}
	}


	@Override
	public void run() // Our thread code for finding ores near the player.
	{
		try
		{
			while( !this.thread.isInterrupted() ) // Check the internal interrupt flag. Exit thread if set.
			{
				if ( xGoggles.drawOres && !OresSearch.searchList.isEmpty() && (mc != null) && (mc.theWorld != null) && (mc.thePlayer != null) )
				{
					if ( nextTimeMs > System.currentTimeMillis() ) // Delay to avoid spamming ore updates.
					{
						continue; 
					}
					
					List temp = new ArrayList();
					int radius = xGoggles.distNumbers[ xGoggles.distIndex ]; // Get the radius around the player to search.
					int px = xGoggles.localPlyX;
					int py = xGoggles.localPlyY;
					int pz = xGoggles.localPlyZ;
					for (int y = Math.max( 0, py - 96 ); y < py + 32; y++) // Check the y axis. from 0 or the players y-96 to the players y + 32
					{
						for (int x = px - radius; x < px + radius; x++) // Iterate the x axis around the player in radius.
						{
							for (int z = pz - radius; z < pz + radius; z++) // z axis.
							{
								int id = Block.getIdFromBlock( mc.theWorld.getBlock( x, y, z ) );
								int meta = mc.theWorld.getBlockMetadata(x, y, z);
								
								if( mc.theWorld.getBlock( x, y, z ).hasTileEntity() )
								{
									meta = 0;
								}
								
								for( OreInfo ore : OresSearch.searchList ) // Now we're actually checking if the current x,y,z block is in our searchList.
								{
									if ( (ore.draw) && (id == ore.id) && (meta == ore.meta) ) // Dont check meta if its -1 (custom)
									{
										temp.add( new BlockInfo( x, y, z, ore.color, ore.asptect) ); // Add this block to the temp list
										break; // Found a match, move on to the next block.
									}
								}
							}
						}
					}
					RenderTick.ores.clear();
					RenderTick.ores.addAll(temp); // Add all our found blocks to the RenderTick.ores list. To be use by RenderTick when drawing.
					nextTimeMs = System.currentTimeMillis() + delayMs; // Update the delay.
				}
				else
				{
					this.thread.interrupt(); // Kill the thread if we turn off xray or the player/world object becomes null.
				}
			}
			//System.out.println(" --- Thread Exited Cleanly! ");
			this.thread = null;
		}
		catch ( Exception exc )
		{
			System.out.println(" ClientTick Thread Interrupted!!! " + exc.toString() ); // This shouldnt get called.
		}
	}
}
