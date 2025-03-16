package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.util.Human;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static fun.kaituo.gameutils.util.ItemUtils.removeItem;

@SuppressWarnings("unused")
public class Victoria extends Human {
    public static final String displayName = "维多利亚";
    public static final String chooseMessage = ".......好了,我们出发吧";
    public static final ChatColor color = ChatColor.LIGHT_PURPLE;

    private final double radius;
    private final ItemStack maidItem;
    private final ItemStack succubusItem;
    private boolean isMaid = true;

    public Victoria(Player p) {
        super(p);
        radius = getConfigDouble("radius");
        maidItem = TagUnderground.inst().getItem("VictoriaMaid");
        succubusItem = TagUnderground.inst().getItem("VictoriaSuccubus");
        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagUnderground.inst(), this::regenerate, 50, 50));
    }

    @Override
    public void onRejoin() {
        super.onRejoin();
        taskIds.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(TagUnderground.inst(), this::regenerate, 50, 50));
    }

    private void regenerate() {
        if (!isMaid) {
            for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
                if (!(e instanceof Player p)) {
                    continue;
                }
                if (p.equals(player)) {
                    continue;
                }
                if (TagUnderground.inst().getPlayers().contains(p) && !p.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 55, 0));
                    break;
                }
            }
        }
    }

    @Override
    public boolean castSkill() {
        if (isMaid) {
            removeItem(player.getInventory(), maidItem);
            player.getInventory().addItem(succubusItem);
            player.removePotionEffect(PotionEffectType.JUMP_BOOST);
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, -1, 0, false, false));
        } else {
            removeItem(player.getInventory(), succubusItem);
            player.getInventory().addItem(maidItem);
            player.removePotionEffect(PotionEffectType.RESISTANCE);
            player.removePotionEffect(PotionEffectType.GLOWING);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, -1, 1, false, false));
        }
        isMaid = !isMaid;
        return true;
    }
}
