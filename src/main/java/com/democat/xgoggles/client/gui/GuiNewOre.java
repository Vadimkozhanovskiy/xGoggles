package com.democat.xgoggles.client.gui;

import com.democat.xgoggles.client.OresSearch;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiNewOre extends GuiScreen {
	GuiTextField oreName;
	GuiTextField oreIdent;
	GuiSlider redSlider;
	GuiSlider greenSlider;
	GuiSlider blueSlider;
	GuiButton addButton;

	boolean oreNameCleared  = false;
	boolean oreIdentCleared = false;
	
	@Override
	public void initGui()
    {
        // Called when the gui should be (re)created
		this.buttonList.add( new GuiButton( 98, width / 2 + 5, height / 2 + 58, 108, 20, "Add" ) ); // Add button
		this.buttonList.add( new GuiButton( 99, width / 2 - 108, height / 2 + 58, 108, 20, "Cancel" ) ); // Cancel button

        //this.buttonList.add( new GuiButton( 8, width / 2 - 102, height / 2 + 80, 105, 20, "test" ) ); // Cancel button

		this.buttonList.add( new GuiSlider( 1, width / 2 - 108, height / 2 - 63, "Red", 0, 255 )  );
		this.buttonList.add( new GuiSlider( 2, width / 2 - 108, height / 2 - 40, "Green", 0, 255 )  );
		this.buttonList.add( new GuiSlider( 3, width / 2 - 108, height / 2 - 17, "Blue", 0, 255 )  );
		
		for( int i = 0; i < buttonList.size(); i++ )
		{
			GuiButton btn = (GuiButton)buttonList.get( i );
			switch( btn.id )
			{
				case 1: // Red slider
					redSlider = (GuiSlider)btn;
					break;
				case 2: // Green slider
					greenSlider = (GuiSlider)btn;
					break;
				case 3: // Blue slider
					blueSlider = (GuiSlider)btn;
					break;
				case 98: // Add button
					addButton = btn;
					break;
				default:
					break;
			}
		}
		redSlider.sliderValue   = 0.0F;
		greenSlider.sliderValue = 1.0F;
		blueSlider.sliderValue  = 0.0F;
		
		oreName = new GuiTextField( this.fontRendererObj, width / 2 - 108, height / 2 + 8, 220, 20 );
		oreIdent = new GuiTextField( this.fontRendererObj, width / 2 - 108, height / 2 + 32, 220, 20 );
		oreName.setText( "Name of block");
		oreIdent.setText( "ID:META" ); // TODO: oreName
	}
	
	@Override
	public void actionPerformed( GuiButton button ) // Called on left click of GuiButton
	{
		switch(button.id)
		{
			case 98: // Add
				int color = (int)(redSlider.sliderValue * 255);
				color = (color<<8) + (int)(greenSlider.sliderValue * 255);
				color = (color<<8) + (int)(blueSlider.sliderValue * 255);
				String aspect = "ordo";
				
				OresSearch.add(oreIdent.getText(), oreName.getText(), color, aspect);
				
				mc.thePlayer.closeScreen();
				mc.displayGuiScreen( new GuiSettings() );
				break;
						
			case 99: // Cancel
				mc.thePlayer.closeScreen();
				mc.displayGuiScreen( new GuiSettings() );
				break;
				
			default:
				break;
		}
	}
	
	@Override
	protected void keyTyped( char par1, int par2 ) // par1 is char typed, par2 is ascii hex (tab=15 return=28)
	{
		//System.out.println( String.format( "keyTyped: %c : %d", par1, par2 ) );
		super.keyTyped( par1, par2 );
		if( oreName.isFocused() )
		{
			oreName.textboxKeyTyped( par1, par2 );
			if( par2 == 15 )
			{
				oreName.setFocused( false );
				if( !oreIdentCleared )
				{
					oreIdent.setText("");
				}
				oreIdent.setFocused( true );
			}
			
		}
		else if( oreIdent.isFocused() )
		{
			oreIdent.textboxKeyTyped( par1, par2 );
			if( par2 == 28 )
			{
				this.actionPerformed( addButton );
			}
			
		}
		else
		{
			switch( par2 )
			{
				case 15: // Change focus to oreName on focus-less tab
					if( !oreNameCleared )
					{
						oreName.setText("");
					}
					oreName.setFocused( true );
					break;
				case 1: // Exit on escape
                    mc.displayGuiScreen( new GuiSettings() );
					mc.thePlayer.closeScreen();
				default:
					break;
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() // Dont pause the game in single player.
	{
		return false;
	}
	
	@Override
	public void updateScreen()
    {
		oreName.updateCursorCounter();
		oreIdent.updateCursorCounter();
	}

	@Override
	public void drawScreen( int x, int y, float f )
    {
        drawDefaultBackground();
        mc.renderEngine.bindTexture( new ResourceLocation("xgoggles:textures/gui/oreAddBackground.png") );
        drawTexturedModalRect(width / 2 - 125, height / 2 - 95, 0, 0, 256, 205);

        FontRenderer fr = this.mc.fontRenderer;
        fr.drawString("Add an Ore", width / 2 - 108, height / 2 - 80, 0x404040);

		oreName.drawTextBox();
		oreIdent.drawTextBox();
		super.drawScreen(x, y, f);

		GL11.glDisable( GL11.GL_TEXTURE_2D );
		GL11.glEnable( GL11.GL_BLEND );
		GL11.glDepthMask( false );
		GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
		
		GL11.glBegin( GL11.GL_QUADS );
		GL11.glColor3f( redSlider.sliderValue, greenSlider.sliderValue, blueSlider.sliderValue );
        GL11.glVertex2d( width / 2 + 46, height / 2 - 63 ); // TL
        GL11.glVertex2d( width / 2 + 46, height / 2 + 3 ); // BL
        GL11.glVertex2d( width / 2 + 113, height / 2 + 3 ); // BR
        GL11.glVertex2d( width / 2 + 113, height / 2 - 63 ); // TR
		GL11.glEnd();


        // new
        // I want to render the item here but i am unsure on how to
        // do it so i am leaving it for now. :)
        // RenderItem renderItem = new RenderItem();
        // IIcon icon = net.minecraft.block.Block.getBlockById(3).getIcon( 1, 2 );
        // renderItem.renderIcon(50, 50, icon, 16, 16);
	}
	
	@Override
	public void mouseClicked( int x, int y, int mouse )
	{
		super.mouseClicked( x, y, mouse );
		oreName.mouseClicked( x, y, mouse );
		oreIdent.mouseClicked( x, y, mouse );
		
		if( oreName.isFocused() && !oreNameCleared )
		{
			oreName.setText( "" );
			oreNameCleared = true;
		}
		if( oreIdent.isFocused() && !oreIdentCleared )
		{
			oreIdent.setText( "" );
			oreIdentCleared = true;
		}

        // TODO: fix bug where if you type then remove it the text will not be put back.
        if( !oreName.isFocused() && oreNameCleared && oreName.getText() == "" )
        {
            oreNameCleared = false;
            oreName.setText( "Name of block");
        }

        if( !oreIdent.isFocused() && oreIdentCleared && oreIdent.getText() == "" )
        {
            oreIdentCleared = false;
            oreIdent.setText( "ID:META");
        }

		if( mouse == 1 ) // Right clicked
		{
			for( int i = 0; i < this.buttonList.size(); i++ )
			{
				GuiButton button = (GuiButton)this.buttonList.get( i );
				if( button.func_146115_a() ) //func_146115_a() returns true if the button is being hovered
				{
					/*if( button.id == 99 ){
					}*/
				}
			}
		}
	}
}
