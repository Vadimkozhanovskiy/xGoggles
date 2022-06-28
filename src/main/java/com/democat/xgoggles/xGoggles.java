package com.democat.xgoggles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.democat.xgoggles.config.ConfigHandler;
import com.democat.xgoggles.config.DefaultConfig;
import com.democat.xgoggles.proxy.ServerProxy;
import com.democat.xgoggles.reference.OreInfo;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="xGoggles", name="xGoggles", version="1.0.1.75")
public class xGoggles
{
	public static int localPlyX, localPlyY, localPlyZ; // For internal use in the ClientTick thread.
	public static boolean drawOres = false; // Off by default
	
	public static final String[] distStrings = new String[] // Strings for use in the GUI Render Distance button
	{
			"4", "8", "12", "16", "20", "24", "28", "32", "36", "40", "44", "48", "52", "56", "60", "64"
	};  
	public static final int[] distNumbers = new int[] // Radius +/- around the player to search. So 8 is 8 on left and right of player plus under the player. So 17x17 area.
	{
		4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56, 60, 64
	};
    public static int distIndex = 0; // Index for the distNumers array. Default search distance.
    public static boolean skipGenericBlocks = true; // See ClientTick.run() thread. Skip common blocks in overworld/nether/end.
	
	// Keybindings
	public static final int keyIndex_toggleXray = 0;
	public static final int keyIndex_showXrayMenu = 1;
	public static final int[] keyBind_keyValues = 
	{
		Keyboard.KEY_LEFT,
		Keyboard.KEY_RIGHT
	};
	public static final String[] keyBind_descriptions =
	{
		"Переключить радар ресурсов",
		"Меню радара ресурсов"
	};
	public static KeyBinding[] keyBind_keys = null;
	
	public static Configuration config = null;
	
	public static Map<String, OreInfo> oredictOres = new HashMap<String, OreInfo>();
		/* Ores to check through the ore dictionary and add each instance found to the searchList. 
		 * put( "oreType", new OreInfo(...) ) oreType is the ore dictionary string id. Press Print OreDict and check console to see list.
		 * OreInfo( String "Gui Name", // The name to be displayed in the GUI.
		 *     int id, int meta, // Leave these at 0. The OresSearch will set them through the ore dictionary.
		 *     int color, // 0x RED GREEN BLUE (0xRRGGBB)
		 *     bool enabled) // Should the be on by default. Its then set internally by GuiSettings.
		 * Open DefaultConfig.java for more info.
		 */
	
	public static List<OreInfo> customOres = new ArrayList<OreInfo>();
		/* List of custom id:meta to add.
		 * OreInfo( String "Gui Name", // Displayed in the GUI.
		 *     int id, int meta, // Set these to whatever the id:meta is for your block.
		 *     int color, // color 0xRRGGBB
		 *     bool enabled) // On by default? 
		 */
	
	// The instance of your mod that Forge uses.
	@Instance(value = "xGoggles")
	public static xGoggles instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="com.democat.xgoggles.proxy.ClientProxy", serverSide="com.democat.xgoggles.ServerProxy")
	public static ServerProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
    {
		Configuration config = new Configuration( event.getSuggestedConfigurationFile() );
		config.load();
		
		if( config.getCategoryNames().isEmpty() )
        {
			System.out.println("[xGoggles] Config file not found. Creating now.");
			DefaultConfig.create( config );
			config.save();
		}
		
		ConfigHandler.setup( event ); // Read the config file and setup environment.
        System.out.println("[xGoggles] PreInit ");
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
    {
		proxy.proxyInit();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
    {
	}
}
