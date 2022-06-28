package com.democat.xgoggles.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.democat.xgoggles.reference.OreInfo;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;

public class DefaultConfig {
	// Below are the 'default' ores/blocks to add through the ore dictionary.
	final static Map<String, OreInfo> defaults = new HashMap<String, OreInfo>()
	{{
		put("oreLapis", new OreInfo("Лазурит", 0, 0, 0x0000FF, false, "aqua") );
		put("oreCopper", new OreInfo("Медная руда", 0, 0, 0xCC6600, true, "ordo") );
		put("oreTin", new OreInfo("Оловянная руда", 0, 0, 0xA1A1A1, true, "ordo") );
		put("oreCobalt", new OreInfo("Кобальт", 0, 0, 0x0000FF, false, "ordo") );
		put("oreArdite", new OreInfo("Ардит", 0, 0, 0xFF9900, false, "vinculum") );
		put("oreUranium", new OreInfo("Уран", 0, 0, 0x00FF00, true, "terra") );
		put("oreDiamond", new OreInfo("Алмазны", 0, 0, 0x8888FF, false, "tutamen") );
		put("oreEmerald", new OreInfo("Изумруды", 0, 0, 0x008810, true, "limus") );
		put("oreGold", new OreInfo("Золото", 0, 0, 0xFFFF00, false, "aer") );
		put("oreRedstone", new OreInfo("Редстоун", 0, 0, 0xFF0000, false, "sano") );
		put("oreIron", new OreInfo("Железо", 0, 0, 0xAA7525, false, "ordo") );
		put("oreSilver", new OreInfo("Серебро", 0, 0, 0x8F8F8F, false, "ordo") );
		put("mossystone", new OreInfo("Замшелые камни", 0, 0, 0x1E4A00, false, "terra") );
		put("oreQuartz", new OreInfo("Кварц", 0, 0, 0x8888FF, false, "auram") );
		put("oreCoal", new OreInfo("Уголь", 0, 0, 0x000000, false , "vacuos") );
	}};
	
	// Default block to add. Mostly just so people can add custom blocks manually through the config until I setup a gui for it.
	final static List<OreInfo> custom = new ArrayList<OreInfo>()
	{{
		add( new OreInfo("Сундук", Block.getIdFromBlock( Blocks.chest ), 0, 0xFF00FF, true, "aer") );
	}};
	

	public static void create(Configuration config) // Put default blocks and settings into the config file.
	{
		config.get(config.CATEGORY_GENERAL, "searchdist", 0); // Default search distance is index 0 (8)
		
		for( Entry<String, OreInfo> ore : defaults.entrySet() )
		{
			String key = ore.getKey();
			OreInfo value = ore.getValue();
			String category = value.oreName.replaceAll("\\s+", "").toLowerCase(); // No whitespace or capitals in the config file categories
			
			config.get("oredict."+category, "dictname", "SOMETHINGBROKE").set( key ); // We need the capitals for the ore dictionary.
			config.get("oredict."+category, "guiname", "SOMETHINGBROKE").set( value.oreName );
			config.get("oredict."+category, "id", -1).set( value.id );
			config.get("oredict."+category, "meta", -1).set( value.meta );
			config.get("oredict."+category, "color", -1).set( value.color );
			config.get("oredict."+category, "enabled", false).set( value.draw );
			config.get("oredict."+category, "aspect", "ordo").set(value.asptect);
		}
		
		for( OreInfo ore : custom ) // Put custom block into the config file.
		{
			String name = ore.oreName.replaceAll("\\s+", "").toLowerCase(); // No whitespace or capitals in the config file categories.
			config.get("customores."+name, "name", "SOMETHINGBROKE").set( ore.oreName );
			config.get("customores."+name, "id", -1).set( ore.id );
			config.get("customores."+name, "meta", -1).set( ore.meta );
			config.get("customores."+name, "color", -1).set( ore.color );
			config.get("customores."+name, "enabled", false).set( ore.draw );
			config.get("customores."+name, "aspect", "ordo").set(ore.asptect);
		}
		
	}
}
