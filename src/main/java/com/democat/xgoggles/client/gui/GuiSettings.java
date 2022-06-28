package com.democat.xgoggles.client.gui;

import java.util.*;

import com.democat.xgoggles.config.ConfigHandler;
import com.democat.xgoggles.reference.OreInfo;
import com.democat.xgoggles.xGoggles;
import com.democat.xgoggles.OreButtons;
import com.democat.xgoggles.client.OresSearch;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class GuiSettings extends GuiScreen {
	Map<String, OreButtons> buttons = new HashMap<String, OreButtons>();

	List<GuiPage> pageIndex = new ArrayList<GuiPage>();
	GuiButton aNextButton;
	GuiButton aPrevButton;

	protected int pageCurrent = 0;
	protected int pageMax = 0;
	public boolean called = false;

	@Override
	public void initGui() {
		// Called when the gui should be (re)created.
		if (OresSearch.searchList.isEmpty()) {
			// This shouldnt happen. But return if it does.
			System.out.println("[xGoggles] Error: searchList is empty inside initGui call!");
			return;
		}

		this.buttons = new HashMap<String, OreButtons>(); // String id for the button. Same as the button text. (Diamond / Iron ect.)
		this.buttonList.clear();
		pageIndex.clear(); // took me 2 hours to figure out i had forgotten this...

		this.buttonList.add(aNextButton = new GuiCustomButton(-150, width / 2 + 168, height / 2 - 30, 30, 20, ">"));                                     // Static next button
		this.buttonList.add(aPrevButton = new GuiCustomButton(-151, width / 2 - 150, height / 2 - 30, 30, 20, "<"));

		int x = width / 2 - 100;
		int y = height / 2 - 100;

		int Count = 0;
		int Page = 0;
		int CountPerPage = 0;
		for (OreInfo ore : OresSearch.searchList) {
			if (buttons.get(ore.oreName) != null) {
				// Button already created for this ore.
				buttons.get(ore.oreName).ores.add(ore); // Add this new OreInfo to the internal ArrayList for this button
			} else {
				// Create the new button for this ore.
				int id = Integer.parseInt(Integer.toString(ore.id) + Integer.toString(ore.meta));                     // Unique button id. int( str(id) + str(meta) )
				// very hacky... Need to keep an eye on it.
				if (Count % 14 == 0 && Count != 0) {
					Page++;
					if (Page > pageMax) {
						pageMax++;
					}

					x = width / 2 - 100;
					y = height / 2 - 100;
					CountPerPage = 0;
				}
				pageIndex.add(new GuiPage(Page, new GuiCustomButton(id, x, y, 125, 20, (ore.draw ? "§2" : "§c") + ore.oreName ))); // create new button and set the text to Name: On||Off
				buttons.put(ore.oreName, new OreButtons(ore.oreName, id, ore)); // Add this new button to the buttons hashmap.
				y += 21.8; // Next button should be placed down from this one.

				// this should reset each page to split the list :)
				if (CountPerPage == 6) {
					x += 125;
					y = height / 2 - 100; // Move next button to the right and reset the y.
				}
				Count++;
				CountPerPage++;
			}
		}

		// only draws the current page
		for (int a = 0; a < pageIndex.size(); a++) {
			if (pageIndex.get(a).getPage() != pageCurrent) {
				continue; // skip the ones that are not on this page.
			}

			this.buttonList.add(pageIndex.get(a).getButton());
		}

		// because i am lazy
		int tempx = width / 2;
		this.buttonList.add(new GuiCustomButton(97, tempx - 100, height / 2 + 52, 68, 20, "Вкл. все"));
		this.buttonList.add(new GuiCustomButton(95, tempx - 30	, height / 2 + 52, 68, 20,"Выкл. все"));

		this.buttonList.add(new GuiCustomButton(98, tempx + 70, height / 2 + 52, 66, 20, "Радиус: " + xGoggles.distStrings[xGoggles.distIndex])); // Static button for printing the ore dictionary / searchList.
		//this.buttonList.add(new GuiCustomButton(99, this.width - 102, this.height - 22, 100, 20, "Print OreDict")); // Static button for search distance.

		if (pageMax < 1) {
			aNextButton.enabled = false;
			aPrevButton.enabled = false;
		}

		if (pageCurrent == 0) {
			aPrevButton.enabled = false;
		}

		if (pageCurrent == pageMax) {
			aNextButton.enabled = false;
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		// Called on left click of GuiButton
		switch (button.id) {
			case 99: // Print OreDict
				for (String name : OreDictionary.getOreNames()) // Print the ore dictionary.
				{
					ArrayList<ItemStack> oreStack = OreDictionary.getOres(OreDictionary.getOreID(name));
					System.out.print(String.format("[OreDict] %-40.40s [%d types] ( ", name, oreStack.size()));
					StringBuilder idMetaCsv = new StringBuilder();
					if (oreStack.size() < 1) {
						idMetaCsv.append(" )");
					}

					for (ItemStack stack : oreStack) {
						if (stack == oreStack.get(oreStack.size() - 1)) {
							idMetaCsv.append(String.format("%d:%d )", Item.getIdFromItem(stack.getItem()), stack.getItemDamage()));
						} else {
							idMetaCsv.append(String.format("%d:%d, ", Item.getIdFromItem(stack.getItem()), stack.getItemDamage()));
						}
					}
					System.out.println(idMetaCsv.toString());
				}

				if (!OresSearch.searchList.isEmpty()) // Print out the searchList.
				{
					for (OreInfo ore : OresSearch.searchList) {
						System.out.println(String.format("[xGoggles] OreInfo( %s, %d, %d, 0x%x, %b )", ore.oreName, ore.id, ore.meta, ore.color, ore.draw));
					}
				}
				break;

			case 98: // Distance Button
				if (xGoggles.distIndex < xGoggles.distNumbers.length - 1) {
					xGoggles.distIndex++;
				} else {
					xGoggles.distIndex = 0;
				}
				ConfigHandler.update("searchdist", false);
				break;

			case 97: // New Ore button

				for (OreInfo ore : OresSearch.searchList) {
					ore.draw = true;
					ConfigHandler.update(ore.oreName, ore.draw);
				}

				break;
			case 95: // New Ore button

				for (OreInfo ore : OresSearch.searchList) {
					ore.draw = false;
					ConfigHandler.update(ore.oreName, ore.draw);
				}

				break;


			case -150:
				if (pageCurrent < pageMax) {
					pageCurrent++;
				}
				break;

			case -151:
				if (pageCurrent > 0) {
					pageCurrent--;
				}
				break;

			default:
				for (Map.Entry<String, OreButtons> entry : buttons.entrySet()) {
					// Iterate through the buttons map and check what ores need to be toggled
					String key = entry.getKey();            // Block name (Diamond)
					OreButtons value = entry.getValue();    // OreButtons structure

					if (value.id == button.id) {
						// Matched the buttons unique id.
						for (OreInfo tempOre : value.ores) {
							// Iterate through the ores that this button should toggle.
							for (OreInfo ore : OresSearch.searchList) {
								// Match this ore with the one in the searchList.
								if ((tempOre.id == ore.id) && (tempOre.meta == ore.meta)) {
									ore.draw = !ore.draw; // Invert searchList.ore.draw
									ConfigHandler.update(ore.oreName, ore.draw);
								}
							}
						}
					}
				}
				break;
		}

		this.initGui();
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		if ((par2 == 1) || (par2 == mc.gameSettings.keyBindInventory.getKeyCode()) || par2 == xGoggles.keyBind_keys[xGoggles.keyIndex_showXrayMenu].getKeyCode()) {
			// Close on esc, inventory key or keybind
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		// Dont pause the game in single player.
		return false;
	}

	// this should be moved to some sort of utility package but fuck it :).
	// this removes the stupid power of 2 rule that comes with minecraft.
	public static void drawTexturedQuadFit(double x, double y, double width, double height, double zLevel) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0, 1);
		tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
		tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1, 0);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
		tessellator.draw();
	}

	@Override
	public void drawScreen(int x, int y, float f) {
		drawDefaultBackground();  // Draws the opaque black background
		// Right lets get a background :)
		mc.renderEngine.bindTexture(new ResourceLocation("xgoggles:textures/gui/Background.png"));
		drawTexturedQuadFit(width / 2 - 110, height / 2 - 110, 270, 193, 0);
		super.drawScreen(x, y, f);
	}

	@Override
	public void mouseClicked(int x, int y, int mouse) {
		super.mouseClicked(x, y, mouse);
		if (mouse == 1) {
			// Right clicked
			for (int i = 0; i < this.buttonList.size(); i++) {
				GuiButton button = (GuiButton) this.buttonList.get(i);
				if (button.func_146115_a()) { //func_146115_a() returns true if the button is being hovered
					//mc.theWorld.playSoundAtEntity( mc.thePlayer, "minecraft.sound.random.click", 1.0F, 1.0F ); TODO: click sound...
					if (button.id == 98) {
						if (xGoggles.distIndex > 0) {
							xGoggles.distIndex--;
						} else {
							xGoggles.distIndex = xGoggles.distNumbers.length - 1;
						}
						ConfigHandler.update("searchdist", false);
						this.initGui();
					}
				}
			}
		}
	}
}