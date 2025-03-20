package me.athlaeos.valhallammo.version;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.CustomRecipeRegistry;
import me.athlaeos.valhallammo.hooks.WorldGuardHook;
import me.athlaeos.valhallammo.utility.Scheduling;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BrewingStartEvent;

public class BrewingPreventionListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStartBrewing(BrewingStartEvent e){
        if (WorldGuardHook.inDisabledRegion(e.getBlock().getLocation(), WorldGuardHook.VMMO_CRAFTING_BREWING) ||
                ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName()) ||
                CustomRecipeRegistry.getBrewingRecipes().isEmpty()) return;
        e.setTotalBrewTime(Integer.MAX_VALUE);
        Scheduling.runLocationTask(ValhallaMMO.getInstance(), e.getBlock().getLocation(), 1L, () -> {
            if (e.getBlock().getType() != Material.BREWING_STAND || !(e.getBlock().getState() instanceof BrewingStand stand)) return;
            stand.setFuelLevel(stand.getFuelLevel() + 1);
            stand.update();
        });
    }
}
