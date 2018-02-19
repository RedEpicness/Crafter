package me.redepicness.crafter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main extends JavaPlugin {

    public HashMap<String, CustomMaterial> materials = new HashMap<String, CustomMaterial>();

    @Override
    public void onEnable() {
        YamlConfiguration materialConf = null;
        YamlConfiguration recipeConf = null;
        try {
            if(!getDataFolder().exists()) {
                if(!getDataFolder().mkdir()){
                    throw new RuntimeException("Could not create configuration folder!");
                }
            }
            File materialConfig = new File(getDataFolder(), "materials.yml");
            File recipeConfig = new File(getDataFolder(), "recipes.yml");

            if(!materialConfig.exists()){
                if(!materialConfig.createNewFile()){
                    throw new RuntimeException("Could not create configuration file!");
                }
                InputStream stream = getClass().getResourceAsStream("/materials.yml");
                OutputStream stream1 = new FileOutputStream(materialConfig);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = stream.read(bytes)) != -1) {
                    stream1.write(bytes, 0, read);
                }
                stream.close();
                stream1.close();
            }
            materialConf = YamlConfiguration.loadConfiguration(materialConfig);
            if(!recipeConfig.exists()){
                if(!recipeConfig.createNewFile()){
                    throw new RuntimeException("Could not create configuration file!");
                }
                InputStream stream = getClass().getResourceAsStream("/recipes.yml");
                OutputStream stream1 = new FileOutputStream(recipeConfig);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = stream.read(bytes)) != -1) {
                    stream1.write(bytes, 0, read);
                }
                stream.close();
                stream1.close();
            }
            recipeConf = YamlConfiguration.loadConfiguration(recipeConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Map<?, ?> map : materialConf.getMapList("materials")) {
            try{
                CustomMaterial material = new CustomMaterial();
                material.setId((String) map.get("id"));
                material.setName((String) map.get("name"));
                material.setItem(Material.valueOf(((String) map.get("item")).toUpperCase()));
                if(map.containsKey("amount")){
                    material.setAmount((Integer) map.get("amount"));
                }
                if(map.containsKey("lore")){
                    material.addLore((List<?>) map.get("lore"));
                }
                materials.put(material.getId(), material);
                System.out.println("Loaded: "+material.getId()+"!");
            }
            catch (Exception e){
                System.out.println("Error loading material!");
                e.printStackTrace();
            }
        }
        int count = 0;
        for(Map<?, ?> map : recipeConf.getMapList("recipes")) {
            try{
                CustomRecipe recipe = new CustomRecipe();
                boolean shapeless = (Boolean)map.get("shapeless");
                recipe.setShapeless(shapeless);
                String result = (String) map.get("result");
                if(materials.containsKey(result)){
                    recipe.setResult(materials.get(result).getItemStack());
                }
                else{
                    ItemStack s = new ItemStack(Material.valueOf(result));
                    if(map.containsKey("amount")){
                        s.setAmount((Integer)map.get("amount"));
                    }
                    recipe.setResult(new ItemStack(Material.valueOf(result)));
                }
                if(shapeless){
                    ArrayList<String> items = (ArrayList<String>)map.get("items");
                    Map<String, Integer> i = new HashMap<String, Integer>();
                    for(String s : items){
                        String[] a = s.split("%");
                        i.put(a[0], Integer.valueOf(a[1]));
                    }
                    recipe.setItems(i);
                }
                else{
                    ArrayList<String> items = (ArrayList<String>)map.get("items");
                    Map<String, Character> i = new HashMap<String, Character>();
                    for(String s : items){
                        String[] a = s.split("%");
                        i.put(a[0], a[1].charAt(0));
                    }
                    recipe.setShape(((String) map.get("shape")).split("%"));
                    recipe.setShapedItems(i);
                }
                recipe.registerRecipe((Boolean)map.get("removeVanillaRecipe"));
                count++;
            }
            catch (Exception e){
                System.out.println("Error loading recipe!");
                e.printStackTrace();
            }
        }
        System.out.println("Loaded "+count+" recipes!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!command.getName().equals("mat") && !command.getName().equals("material")) return false;
        if(args.length < 1) {
            sender.sendMessage(ChatColor.RED+"Usage: "+ChatColor.GOLD+"/mat list/[material name] (amount)");
            return false;
        }
        if(args[0].equals("list")){
            String list = "";
            for(String s : materials.keySet()){
                list += ", "+s;
            }
            list = list.length() > 1?ChatColor.AQUA+list.substring(1): ChatColor.RED+"None";
            sender.sendMessage(ChatColor.GOLD+"Loaded materials:");
            sender.sendMessage(list);
        }
        else if(materials.containsKey(args[0])){
            Player p = ((Player) sender);
            ItemStack stack = materials.get(args[0]).getItemStack();
            if(args.length > 1){
                try{
                    stack.setAmount(Integer.valueOf(args[1]));
                }
                catch (IllegalArgumentException ignored){}
            }
            p.getInventory().addItem(materials.get(args[0]).getItemStack());
            p.sendMessage(ChatColor.GREEN+"You received "+stack.getAmount()+" of '"+args[0]+"'!");
        }
        else{
            sender.sendMessage(ChatColor.RED+"No material with the id '"+args[0]+"' was found! (Perhaps it was not properly loaded?)");
        }
        return true;
    }
}
