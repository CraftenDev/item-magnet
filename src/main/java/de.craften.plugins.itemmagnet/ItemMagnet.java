package de.craften.plugins.itemmagnet;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class ItemMagnet extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getScheduler().runTaskTimer(this, () -> {
            // run magnet
            getServer().getWorlds().forEach((world) -> {
                // for each world...
                world.getEntitiesByClass(Item.class).forEach((item) -> {
                    // get chests near that item
                    Chest chest = getNearestChest(item.getLocation(), 2);
                    if (chest != null) {
                        Map<Integer, ItemStack> remainingItems = chest.getBlockInventory().addItem(item.getItemStack());
                        item.remove();

                        // re-drop items that didn't match
                        remainingItems.forEach((i, stack) -> item.getWorld().dropItem(item.getLocation(), stack));

                        // nom nom, cookies
                        if (item.getItemStack().getType() == Material.COOKIE) {
                            chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.EAT, 1, 1);
                        }
                    }
                });
            });
        }, 20 * 5, 20 * 5); // all 5 seconds
    }

    private static Chest getNearestChest(Location location, int maxRadius) {
        double nearestDistance = Double.MAX_VALUE;
        Chest nearestChest = null;

        for (int x = -(maxRadius); x <= maxRadius; x++) {
            for (int y = -(maxRadius); y <= maxRadius; y++) {
                for (int z = -(maxRadius); z <= maxRadius; z++) {
                    Block block = location.getBlock().getRelative(x, y, z);
                    BlockState blockState = block.getState();
                    double distance = location.distanceSquared(block.getLocation());
                    if (blockState instanceof Chest && distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestChest = (Chest) blockState;
                    }
                }
            }
        }

        return nearestChest;
    }
}
