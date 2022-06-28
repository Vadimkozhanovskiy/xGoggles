package com.democat.xgoggles.client;

import com.democat.xgoggles.client.gui.GuiSettings;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiChat;
import com.democat.xgoggles.xGoggles;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

@SideOnly(Side.CLIENT)
public class KeyBindingHandler {
	private Minecraft mc = Minecraft.getMinecraft();


	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {


		if ((!FMLClientHandler.instance().isGUIOpen(GuiChat.class)) && (mc.currentScreen == null) && (mc.theWorld != null)) {
			EntityClientPlayerMP cplayer = mc.thePlayer;
			ItemStack helmet = cplayer.inventory.armorInventory[3];
			if (OresSearch.searchList.isEmpty()) // Populate the OresSearch.searchList
			{
				OresSearch.get();
			}
			if (xGoggles.keyBind_keys[xGoggles.keyIndex_toggleXray].isPressed()) {


				if (isRadarEquipped(helmet)) {
					xGoggles.drawOres = !xGoggles.drawOres;
					if (xGoggles.drawOres) {
						cplayer.addChatMessage(new ChatComponentText("Радар ресурсов активирован"));
					} else {
						cplayer.addChatMessage(new ChatComponentText("Радар ресурсов деактивирован"));
					}
					RenderTick.ores.clear();
				}
			} else if (xGoggles.keyBind_keys[xGoggles.keyIndex_showXrayMenu].isPressed()) {
				if (isRadarEquipped(helmet)) {
					mc.displayGuiScreen(new GuiSettings());
				}
			}
		}
	}

	public static boolean isRadarEquipped(ItemStack helmet) {
		return helmet != null && helmet.getTagCompound() != null && helmet.getTagCompound().getByte("xray") == 1;
	}


}

