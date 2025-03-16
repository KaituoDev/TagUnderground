package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.util.Human;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("unused")
public class FakeTurtle extends Human {
    public static final String displayName = "假海龟";
    public static final String chooseMessage = "是呢...只要有你在就没什么可怕的了......";
    public static final ChatColor color = ChatColor.DARK_AQUA;

    private final int resistanceAmplifier;
    private final int slownessAmplifier;
    private final int jumpAmplifier;

    private final int soupCooldown;

    private final ItemStack turtleSoup;

    private int soupCooldownCounter = 0;

    public FakeTurtle(Player p) {
        super(p);
        resistanceAmplifier = getConfigInt("resistance-amplifier");
        slownessAmplifier = getConfigInt("slowness-amplifier");
        jumpAmplifier = getConfigInt("jump-amplifier");
        soupCooldown = getConfigInt("soup-cooldown");
        turtleSoup = TagUnderground.inst().getItem("TurtleSoup");
    }

    @Override
    public void applyPotionEffects() {
        super.applyPotionEffects();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, -1, slownessAmplifier, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, -1, jumpAmplifier, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, resistanceAmplifier, false, false));
    }

    public void tick() {
        super.tick();
        if (soupCooldownCounter == 0) {
            player.sendMessage("§a获得海龟汤！");
            player.getInventory().addItem(turtleSoup);
            soupCooldownCounter = soupCooldown;
        } else {
            soupCooldownCounter -= 1;
        }
    }
}
