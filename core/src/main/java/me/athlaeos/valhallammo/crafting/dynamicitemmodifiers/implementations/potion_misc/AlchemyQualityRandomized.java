package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.implementations.potion_misc;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategoryRegistry;
import me.athlaeos.valhallammo.dom.Pair;
import me.athlaeos.valhallammo.item.ItemBuilder;
import org.bukkit.command.CommandSender;
import me.athlaeos.valhallammo.item.AlchemyItemPropertyManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlchemyQualityRandomized extends DynamicItemModifier {
    private double lowerBound = -0.1;
    private double upperBound = 0.1;

    public AlchemyQualityRandomized(String name) {
        super(name);
    }

    @Override
    public void processItem(Player crafter, ItemBuilder outputItem, boolean use, boolean validate, int timesExecuted) {
        if (!use) return;
        int quality = AlchemyItemPropertyManager.getQuality(outputItem.getMeta());
        if (quality == 0) return;
        if (lowerBound > upperBound) {
            double temp = upperBound;
            upperBound = lowerBound;
            lowerBound = temp;
        }
        int lowerBound = (int) Math.floor(this.lowerBound * quality);
        int upperBound = (int) Math.ceil(this.upperBound * quality);
        int newQuality = Utils.getRandom().nextInt((upperBound + 1) - lowerBound) + lowerBound;
        AlchemyItemPropertyManager.setQuality(outputItem.getMeta(), Math.max(0, newQuality));
    }

    @Override
    public void onButtonPress(InventoryClickEvent e, int button) {
        if (button == 11)
            lowerBound = Math.min(upperBound, lowerBound + ((e.isLeftClick() ? 1 : -1) * (e.isShiftClick() ? 0.1 : 0.01)));
        if (button == 13)
            upperBound = upperBound + ((e.isLeftClick() ? 1 : -1) * (e.isShiftClick() ? 0.1 : 0.01));
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        return new Pair<>(11,
                new ItemBuilder(Material.PAPER)
                        .name("&fMaximum quality reduction (lower bound)")
                        .lore("&fQuality changed to being between ",
                                String.format("&e%.2f%% and %.2f%% &fof its original value", lowerBound * 100, upperBound * 100),
                                "&6Click to add/subtract 1%",
                                "&6Shift-Click to add/subtract 10%")
                        .get()).map(Set.of(
                new Pair<>(13,
                        new ItemBuilder(Material.PAPER)
                                .name("&fMaximum quality increase (upper bound)")
                                .lore("&fQuality changed to being between ",
                                        String.format("&e%.2f%% and %.2f%% &fof its original value", lowerBound * 100, upperBound * 100),
                                        "&6Click to add/subtract 1%",
                                        "&6Shift-Click to add/subtract 10%")
                                .get())
        ));
    }

    @Override
    public ItemStack getModifierIcon() {
        return new ItemBuilder(Material.NETHER_STAR).get();
    }

    @Override
    public String getDisplayName() {
        return "&dAlchemy Quality (RANDOMIZED)";
    }

    @Override
    public String getDescription() {
        return "&fTakes the item's quality value and randomizes it between two percentages. /n&fExample: -10% to 10% will will add between -20 and 20 quality to a 200 quality item";
    }

    @Override
    public String getActiveDescription() {
        return String.format("&fTakes the item's quality value and randomizes it between &e%.2f%% and %.2f%% &fof its original value", lowerBound * 100, upperBound * 100);
    }

    @Override
    public Collection<String> getCategories() {
        return Set.of(ModifierCategoryRegistry.POTION_MISC.id());
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public DynamicItemModifier copy() {
        AlchemyQualityRandomized m = new AlchemyQualityRandomized(getName());
        m.setLowerBound(this.lowerBound);
        m.setUpperBound(this.upperBound);
        m.setPriority(this.getPriority());
        return m;
    }

    @Override
    public String parseCommand(CommandSender executor, String[] args) {
        if (args.length != 2) return "Two numbers are expected: both doubles.";
        try {
            lowerBound = Double.parseDouble(args[0]);
            upperBound = Double.parseDouble(args[1]);
        } catch (NumberFormatException ignored){
            return "Two numbers are expected: both doubles. At least one was not a number";
        }
        return null;
    }

    @Override
    public List<String> commandSuggestions(CommandSender executor, int currentArg) {
        if (currentArg == 0) return List.of("<lower_bound>");
        if (currentArg == 1) return List.of("<upper_bound>");
        return null;
    }

    @Override
    public int commandArgsRequired() {
        return 2;
    }
}
