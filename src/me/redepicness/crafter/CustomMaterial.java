package me.redepicness.crafter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomMaterial {

    private String id;
    private String name;
    private Material item;
    private int amount;
    private boolean shapeless;
    private String[] shape;
    private Map<String, Character> shapedItems;
    private Map<String, Integer> items;
    private ArrayList<String> lore = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    public void setShape(String[] matrix) {
        this.shape = matrix;
    }

    public void setShapedItems(Map<String, Character> shapedItems) {
        this.shapedItems = shapedItems;
    }

    public void addLore(List<?> lore) {
        this.lore.addAll((List<? extends String>) lore);
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public ItemStack getItemStack(){
        ItemStack i = new ItemStack(item, amount);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        i.setItemMeta(meta);
        return i;
    }

    public void registerRecipe(boolean removeDefault){
        if(removeDefault){
            Iterator<Recipe> i = Bukkit.getServer().recipeIterator();
            while(i.hasNext()){
                Recipe r = i.next();
                if(r.getResult().getType().equals(item)){
                    i.remove();
                }
            }
        }
        if(shapeless){
            ShapelessRecipe recipe = new ShapelessRecipe(getItemStack());
            for(Map.Entry<String, Integer> entry : items.entrySet()){
                recipe.addIngredient(entry.getValue(), Material.valueOf(entry.getKey().toUpperCase()));
            }
            Bukkit.getServer().addRecipe(recipe);
        }
        else{
            ShapedRecipe recipe = new ShapedRecipe(getItemStack());
            recipe.shape(shape);
            for(Map.Entry<String, Character> e : shapedItems.entrySet()){
                recipe.setIngredient(e.getValue(), Material.valueOf(e.getKey().toUpperCase()));
            }
            Bukkit.getServer().addRecipe(recipe);
        }
    }


}
