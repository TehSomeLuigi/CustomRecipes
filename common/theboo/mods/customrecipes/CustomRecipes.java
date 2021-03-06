package theboo.mods.customrecipes;

import java.io.File;
import java.util.Date;
import java.util.EnumSet;
import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import theboo.mods.customrecipes.handlers.CRTickHandler;
import theboo.mods.customrecipes.logger.Logger;
import theboo.mods.customrecipes.proxy.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * CustomRecipes CustomRecipes
 * 
 * <br> The main class of Custom Recipes
 * 
 * @license 
    Copyright (C) 2013 TheBoo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author TheBoo
 *   
 */
@Mod(modid = "customrecipes", name = "Custom Recipes", version = "4.4.1")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class CustomRecipes {
	

	private boolean keybindings;
    public static Configuration config;
        
    public RecipeLoader loader = new RecipeLoader();

	
	@Instance("customrecipes")
	public static CustomRecipes instance;
	
	@SidedProxy(clientSide = "theboo.mods.customrecipes.proxy.ClientProxy", serverSide = "theboo.mods.customrecipes.proxy.CommonProxy") 
	public static CommonProxy proxy;
	
	public String getPriorities()
	{
		return "after:*";
	}
	
    /**
     * tries to get a file using the path from either minecraft or minecraft server
     * 
     * @return the working minecraft path
     */
    public File getWorkingFolder(){
        File toBeReturned;
        try{
            if (FMLCommonHandler.instance().getSide().isClient()){
                toBeReturned = ModLoader.getMinecraftInstance().getMinecraft().mcDataDir;
            }
            else{
                toBeReturned = ModLoader.getMinecraftServerInstance().getFile("");
            }
            return toBeReturned;
            
        }
        catch(Exception ex){
            Logger.log(Level.SEVERE, "Couldn't get the path to the mod directory.");
        }
        return null;
    }



	// for the log
	
	@EventHandler
	public void preInit(final FMLPreInitializationEvent fml){		
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	        	System.out.println("Closing Custom Recipes log...");
	        	Logger.logClose();
	        }
	    }, "Shutdown-hook-thread"));
	    
		config = new Configuration(fml.getSuggestedConfigurationFile());
		loadConfig(config);
	}
	
	@EventHandler
	public void load(FMLPostInitializationEvent fml){
		System.out.println();
		System.out.println();
		
		Logger.log(Level.INFO, "=== CustomRecipes ===\n *** Created by MightyPork *** \n *** Developed by TheBoo ***\n\n"+(new Date()).toString()+"\n\nSave your recipe files into .minecraft/mods/customrecipes.\n");
		
		GameRegistry.registerFuelHandler(loader);
		
		loader.loadRecipes();
		
		if(keybindings) {
			proxy.addKeybindings();
			addTickhandler();
		}
	}
	
	private void addTickhandler() {   
        TickRegistry.registerTickHandler(new CRTickHandler(EnumSet.of(TickType.PLAYER)), Side.SERVER);
	}
	
	private void loadConfig(Configuration c) {
		c.load();
		keybindings = c.get(c.CATEGORY_GENERAL, "Enable reloading keybind", true).getBoolean(true);
    	c.save();
	}
}
