package fun.kaituo.tagunderground.util;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.state.HuntState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LootChest {
    public static final double CHANCE = 0.15;
    private final Location location;
    private final Random random = new Random();

    private Shulker shulker;

    public LootChest(Location location) {
        this.location = location;
    }

    private final Set<Integer> taskIds = new HashSet<>();

    public void enable() {
        shulker = (Shulker) location.getWorld().spawnEntity(location, EntityType.SHULKER);
        shulker.setAI(false);
        shulker.setInvulnerable(true);
        shulker.setSilent(true);
        shulker.setCollidable(false);
        shulker.setGravity(false);
        shulker.setInvisible(true);
        AttributeInstance attribute = shulker.getAttribute(Attribute.GENERIC_SCALE);
        if (attribute != null) {
            attribute.setBaseValue(0.87);
        }

        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagUnderground.inst(), this::updateColor, 1, 1));

        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagUnderground.inst(), () -> {
            if (random.nextDouble() > CHANCE) {
                return;
            }
            addItem(HuntState.INST.getRandomItem());
        }, 100, 100));
    }

    public void disable() {
        for (int taskId : taskIds) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        taskIds.clear();
        clear();
        if (shulker != null && shulker.isValid()) {
            shulker.remove();
        }
    }

    private void updateColor() {
        if (location.getBlock().getType() != Material.CHEST && location.getBlock().getType() != Material.TRAPPED_CHEST) {
            Bukkit.broadcastMessage("x " + location.getX() + " y " + location.getY() + " z " + location.getZ() + " 不是箱子！");
        }
        Inventory inv = ((Chest) (location.getBlock().getState())).getBlockInventory();
        if (inv.isEmpty()) {
            shulker.setGlowing(false);
            return;
        }
        shulker.setGlowing(true);
        Item.Rarity currentHighestRarity = Item.Rarity.COMMON;
        for (ItemStack itemStack : inv.getContents()) {
            Item item = Item.getItem(itemStack);
            if (item == null) {
                continue;
            }
            Item.Rarity rarity = item.getRarity();
            if (rarity.compareTo(currentHighestRarity) > 0) {
                currentHighestRarity = rarity;
            }
        }
        switch (currentHighestRarity) {
            case COMMON:
                HuntState.INST.getWhite().addEntity(shulker);
                break;
            case RARE:
                HuntState.INST.getAqua().addEntity(shulker);
                break;
            case LEGENDARY:
                HuntState.INST.getYellow().addEntity(shulker);
                break;
        }
    }

    public void addItem(ItemStack item) {
        if (location.getBlock().getType() != Material.CHEST && location.getBlock().getType() != Material.TRAPPED_CHEST) {
            Bukkit.broadcastMessage("x " + location.getX() + " y " + location.getY() + " z " + location.getZ() + " 不是箱子！");
        }
        ((Chest) (location.getBlock().getState())).getBlockInventory().addItem(item);
    }

    public void clear() {
        if (location.getBlock().getType() != Material.CHEST && location.getBlock().getType() != Material.TRAPPED_CHEST) {
            Bukkit.broadcastMessage("x " + location.getX() + " y " + location.getY() + " z " + location.getZ() + " 不是箱子！");
        }
        ((Chest) (location.getBlock().getState())).getBlockInventory().clear();
    }
}
