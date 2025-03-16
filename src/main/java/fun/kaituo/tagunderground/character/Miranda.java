package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.util.Hunter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class Miranda extends Hunter {
    public static final String displayName = "米兰达";
    public static final String chooseMessage = "总有一天，这个虚假的世界会迎来崩坏的时刻......";
    public static final ChatColor color = ChatColor.DARK_GRAY;

    private final int pearlCooldown;
    private int pearlCooldownCounter = 0;

    public Miranda(Player p) {
        super(p);
        pearlCooldown = getConfigInt("pearl-cooldown");
    }

    @Override
    public void tick() {
        super.tick();
        if (pearlCooldownCounter == 0) {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            pearlCooldownCounter = pearlCooldown;
            player.sendMessage("§a获得末影珍珠！");
        } else {
            pearlCooldownCounter -= 1;
        }
    }
}
