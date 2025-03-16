package fun.kaituo.tagunderground.util;

import fun.kaituo.tagunderground.TagUnderground;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static fun.kaituo.gameutils.util.ItemUtils.removeItem;

public abstract class ActiveItem extends Item{
    public abstract boolean use(Player p);

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!TagUnderground.inst().playerIds.contains(p.getUniqueId())) {
            return;
        }
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block b = e.getClickedBlock();
            assert b != null;
            if (b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST)) {
                return;
            }
        }
        ItemStack handItem = p.getInventory().getItemInMainHand().clone();
        if (!handItem.isSimilar(itemStack)) {
            return;
        }
        // Prevent player from using items such as experience bottles
        e.setCancelled(true);
        if (p.hasCooldown(itemStack.getType())) {
            return;
        }
        if (use(p)) {
            removeItem(p.getInventory(), itemStack);
            p.setCooldown(itemStack.getType(), 10);
        }
    }
}
