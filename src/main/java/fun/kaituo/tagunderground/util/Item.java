package fun.kaituo.tagunderground.util;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.state.HuntState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static fun.kaituo.tagunderground.util.Misc.isCharacterHuman;

public abstract class Item implements Listener {
    public enum Rarity {
        COMMON, RARE, LEGENDARY
    }
    @Getter
    protected final ItemStack itemStack;
    protected final Set<Integer> taskIds = new HashSet<>();

    public Item() {
        itemStack = TagUnderground.inst().getItem(this.getClass().getSimpleName());
    }

    public static @Nullable Item getItem(ItemStack itemStack) {
        for (Item item : HuntState.INST.items) {
            if (item.itemStack.isSimilar(itemStack)) {
                return item;
            }
        }
        return null;
    }

    public abstract Rarity getRarity();

    public boolean canObtainDirectly() {
        return true;
    }

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, TagUnderground.inst());
    }

    public void disable() {
        HandlerList.unregisterAll(this);
        taskIds.forEach(Bukkit.getScheduler()::cancelTask);
        taskIds.clear();
    }

    @EventHandler
    public void preventHunterPickUp(PlayerPickupItemEvent e) {
        if (!e.getItem().getItemStack().isSimilar(itemStack)) {
            return;
        }
        Player p = e.getPlayer();
        if (!TagUnderground.inst().playerIds.contains(p.getUniqueId())) {
            return;
        }
        PlayerData data = TagUnderground.inst().idDataMap.get(p.getUniqueId());
        if (data == null) {
            return;
        }
        if (!isCharacterHuman(data.getClass())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void preventHunterClick(InventoryClickEvent e) {
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        if (!currentItem.isSimilar(itemStack)) {
            return;
        }
        HumanEntity entity = e.getWhoClicked();
        if (!TagUnderground.inst().playerIds.contains(entity.getUniqueId())) {
            return;
        }
        PlayerData data = TagUnderground.inst().idDataMap.get(entity.getUniqueId());
        if (data == null) {
            return;
        }
        if (!isCharacterHuman(data.getClass())) {
            e.setCancelled(true);
        }
    }
}
