package me.redepicness.crafter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Iterator;
import java.util.Map;

public class CustomRecipe {

    private boolean shapeless;
    private String[] shape;
    private Map<String, Character> shapedItems;
    private Map<String, Integer> items;
    private ItemStack result;

    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    public void setShape(String[] matrix) {
        this.shape = matrix;
    }

    public void setShapedItems(Map<String, Character> shapedItems) {
        this.shapedItems = shapedItems;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public void setResult(ItemStack result){
        this.result = result;
    }

    public void registerRecipe(boolean removeDefault){
        if(removeDefault){
            Iterator<Recipe> i = Bukkit.getServer().recipeIterator();
            while(i.hasNext()){
                Recipe r = i.next();
                if(r.getResult().getType().equals(result.getType())){
                    i.remove();
                }
            }
        }
        if(shapeless){
            ShapelessRecipe recipe = new ShapelessRecipe(result);
            for(Map.Entry<String, Integer> entry : items.entrySet()){
                recipe.addIngredient(entry.getValue(), Material.valueOf(entry.getKey().toUpperCase()));
            }
            Bukkit.getServer().addRecipe(recipe);
        }
        else{
            ShapedRecipe recipe = new ShapedRecipe(result);
            recipe.shape(shape);
            for(Map.Entry<String, Character> e : shapedItems.entrySet()){
                recipe.setIngredient(e.getValue(), Material.valueOf(e.getKey().toUpperCase()));
            }
            Bukkit.getServer().addRecipe(recipe);
        }
    }

}
