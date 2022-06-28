// Forge proxy for the client side.
package com.democat.xgoggles.proxy;

import com.democat.xgoggles.client.KeyBindingHandler;
import com.democat.xgoggles.client.RenderTick;
import com.democat.xgoggles.handler.MCEventHandler;
import com.democat.xgoggles.client.ClientTick;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import com.democat.xgoggles.xGoggles;

public class ClientProxy extends ServerProxy
{
	@Override
	public void proxyInit()
	{
		// Setup Keybindings
		xGoggles.keyBind_keys = new KeyBinding[ xGoggles.keyBind_descriptions.length ];
		for(int i = 0; i < xGoggles.keyBind_descriptions.length; ++i )
        {
			xGoggles.keyBind_keys[i] = new KeyBinding( xGoggles.keyBind_descriptions[i], xGoggles.keyBind_keyValues[i], "xGoggles" );
			ClientRegistry.registerKeyBinding( xGoggles.keyBind_keys[i] );
		}

		FMLCommonHandler.instance().bus().register( new KeyBindingHandler() );
		FMLCommonHandler.instance().bus().register( new ClientTick() );
		MinecraftForge.EVENT_BUS.register( new RenderTick() );	// RenderTick is forge subscribed to onRenderEvent. Which is called when drawing the world.
		MinecraftForge.EVENT_BUS.register(new MCEventHandler());
	}
}