package com.democat.xgoggles.handler;

import com.democat.xgoggles.xGoggles;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

@SideOnly(Side.CLIENT)
public class MCEventHandler {
    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onUpdateEntity(LivingEvent.LivingUpdateEvent e) {
        if (xGoggles.drawOres) {
            if (e.entity instanceof EntityClientPlayerMP) {
                EntityClientPlayerMP cplayer = mc.thePlayer;
                ItemStack helmet = cplayer.inventory.armorInventory[3];
                if (helmet == null || helmet.getTagCompound() == null || helmet.getTagCompound().getByte("xray") != 1) {
                    xGoggles.drawOres = false;
                }
            }
        }
    }


}
