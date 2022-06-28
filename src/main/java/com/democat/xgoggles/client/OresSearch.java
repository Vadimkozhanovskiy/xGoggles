package com.democat.xgoggles.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.democat.xgoggles.config.ConfigHandler;
import com.democat.xgoggles.reference.OreInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.oredict.OreDictionary;

import com.democat.xgoggles.xGoggles;

public class OresSearch
{
	public static List<OreInfo> searchList = new ArrayList(); // List of ores/blocks to search for.
	public static Minecraft mc = Minecraft.getMinecraft();
	
	private static boolean checkList( List<OreInfo> temp, OreInfo value, ItemStack stack ) // Used to check if a OreInfo already exists in the searchList
	{
		for( OreInfo oreCheck : temp )
		{
			if( (oreCheck.oreName == value.oreName) && (oreCheck.id == Item.getIdFromItem( stack.getItem() ) ) && (oreCheck.meta == stack.getItemDamage()) )
			{
				return true; // This ore already exists in the temp list. (Sometimes the OreDict returns duplicate entries, like gold twice) 
			}
		}
		return false;
	}
	
	public static void add( String oreIdent, String name, int color, String aspect) // Takes a string of id:meta or oreName to add to our search list.
	{
		oreIdent = oreIdent.replaceAll( "\\p{C}",  "?" );
		int id = 0;
		int meta = 0;
		
		if( oreIdent.contains( ":" ) ) // Hopefully a proper id:meta string.
		{
			String[] splitArray = oreIdent.split( ":" );
			
			if( splitArray.length != 2 )
			{
				//System.out.println( String.format( "Can't add %s to searchList. Invalid format.", oreIdent ) );
				String notify = String.format( "[�axGoggles�r] %s is not a valid identifier. Try id:meta (example 1:0 for stone) or oreName (example oreDiamond or mossyStone)", oreIdent );
				ChatComponentText chat = new ChatComponentText( notify );
				mc.ingameGUI.getChatGUI().printChatMessage( chat );
				return;
			}
			
			try
			{
				id = Integer.parseInt( splitArray[0] );
				meta = Integer.parseInt( splitArray[1] );
			}
			catch( NumberFormatException e )
			{ // TODO: Some oredict ores are mod:block for some reason...
				//System.out.println( String.format( "%s is not a valid id:meta format.", oreIdent ) );
				String notify = String.format( "[�axGoggles�r] %s contains data other than numbers and the colon. Failed to add.", oreIdent );
				ChatComponentText chat = new ChatComponentText( notify );
				mc.ingameGUI.getChatGUI().printChatMessage( chat );
				return;
			}
			
		}
		else
		{
			try
			{
				id = Integer.parseInt( oreIdent );
				meta = 0;
			}
			catch( NumberFormatException e )
			{
				String notify = String.format( "[�axGoggles�r] Doesn't support in-game additions to the ore dictionary yet.. Failed to add." );
				ChatComponentText chat = new ChatComponentText( notify );
				mc.ingameGUI.getChatGUI().printChatMessage( chat );
				return;
			}
			
		}
		//System.out.println( String.format( "Adding ore: %s", oreIdent ) );
		OresSearch.searchList.add( new OreInfo( name, id, meta, color, true, "ordo" ) );
		String notify = String.format( "[�axGoggles�r] successfully added %s.", oreIdent );
		ChatComponentText chat = new ChatComponentText( notify );
		mc.ingameGUI.getChatGUI().printChatMessage(chat);

		ConfigHandler.add(name, oreIdent, color, aspect);
	}
	
	public static List<OreInfo> get() // Return the searchList, create it if needed.
	{
		if( OresSearch.searchList.isEmpty() )
		{
			System.out.println( "[xGoggles] --- Populating the searchList with the ore dictionary --- ");
			List<OreInfo> temp = new ArrayList(); // Temporary array of OreInfos to replace searchList
			Map<String, OreInfo> tempOredict = new HashMap<String, OreInfo>(); // Temporary oredict map to replace oredictOres
			
			// OreDictionary.getOres("string") adds the ore if it doesn't exist.
			// Here we check our oredictOres with the untouched oredict and delete any that dont exist already. This avoids polluting the oredict.
			for( String oreName : OreDictionary.getOreNames() )
			{
				if( xGoggles.oredictOres.containsKey( oreName ) )
				{
					tempOredict.put( oreName, xGoggles.oredictOres.get( oreName ) );
					//System.out.println( String.foramt( "[xGoggles]: Found ore %s in dictionary, adding.", oreName ) );
				}
			}
			// Debug loop to notify of invalid and removed oreDict names.
			for( Map.Entry<String, OreInfo> entry : xGoggles.oredictOres.entrySet() )
			{
				String key = entry.getKey();
				if( !tempOredict.containsKey( key ) )
				{
					System.out.println( String.format( "[xGoggles] Ore %s doesn't exist in dictionary! Deleting.", key ) );
				}
			}
			xGoggles.oredictOres.clear();
			xGoggles.oredictOres.putAll( tempOredict );
			tempOredict.clear();
			
			// Now we can iterate over the clean oredictOres and get all the different types of oreName
			for( Map.Entry<String, OreInfo> entry : xGoggles.oredictOres.entrySet() )
			{
				String key = entry.getKey(); // oreName string
				OreInfo value = entry.getValue(); // OreInfo class
				
				ArrayList<ItemStack> oreDictOres = OreDictionary.getOres( key ); // Get an itemstack array of all the oredict ores for 'key'
				if( oreDictOres.size() < 1 )
				{
					System.out.println( String.format( "[xGoggles] Ore %s doesn't exist! Skipping. (We shouldn't have this issue here! Please tell me about this!)", key ) );
					continue;
				}
				for( int i = 0; i < oreDictOres.size(); i++ )
				{
					ItemStack oreItem = oreDictOres.get( i );
					if( checkList( temp, value, oreItem ) )
					{	
						System.out.println("[xGoggles] Duplicate ore found in Ore Dictionary!!! ("+key+")");
						continue;
					}
					temp.add( new OreInfo( value.oreName, Item.getIdFromItem( oreItem.getItem() ), oreItem.getItemDamage(), value.color, value.draw, value.asptect) );
					System.out.println( String.format("[xGoggles] Adding OreInfo( %s, %d, %d, 0x%x, %b ) ", value.oreName, Item.getIdFromItem( oreItem.getItem() ), oreItem.getItemDamage(), value.color, value.draw ) );
				}
			}
			System.out.println( "[xGoggles] --- Done populating searchList! --- ");
			System.out.println( "[xGoggles] --- Adding custom blocks --- ");
			
			for( OreInfo ore : xGoggles.customOres ) //TODO: Check if custom already exists
			{
				System.out.println( String.format( "[xGoggles] Adding OreInfo( %s, %d, %d, 0x%x, %b ) ", ore.oreName, ore.id, ore.meta, ore.color, ore.draw ) );
				temp.add( ore );
			}
			System.out.println( "[xGoggles] --- Done adding custom blocks --- ");
			
			OresSearch.searchList.clear();
			OresSearch.searchList.addAll( temp );
			
		}
		return OresSearch.searchList;
	}
}
