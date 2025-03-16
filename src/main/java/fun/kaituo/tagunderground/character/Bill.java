package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.TagUnderground;
import fun.kaituo.tagunderground.state.HuntState;
import fun.kaituo.tagunderground.util.Human;
import fun.kaituo.tagunderground.util.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class Bill extends Human {

    public static final String displayName = "比尔";
    public static final String chooseMessage = "那个...我为您做了便当...不介意的话请您尝尝看吧~";
    public static final ChatColor color = ChatColor.DARK_GREEN;

    private final int serpentGodBloodObtainCooldown;
    private int serpentGodBloodObtainCooldownCounter;
    private final ItemStack bento;
    private final ItemStack serpentGodBlood;

    public Bill(Player p) {
        super(p);
        serpentGodBloodObtainCooldown = getConfigInt("serpent-god-blood-obtain-cooldown");
        serpentGodBloodObtainCooldownCounter = serpentGodBloodObtainCooldown;
        bento = TagUnderground.inst().getItem("BillBento");
        serpentGodBlood = TagUnderground.inst().getItem("SerpentGodBlood");
    }

    @Override
    public void tick() {
        super.tick();
        if (serpentGodBloodObtainCooldownCounter == 0) {
            player.getInventory().addItem(serpentGodBlood);
            serpentGodBloodObtainCooldownCounter = serpentGodBloodObtainCooldown;
            player.sendMessage("§a获得蛇神之血！");
        } else {
            serpentGodBloodObtainCooldownCounter -= 1;
        }
    }

    @Override
    public boolean castSkill() {
        Player targetWithLowestHealth = null;
        for (Player target : HuntState.INST.getHumans()) {
            PlayerData data = TagUnderground.inst().idDataMap.get(target.getUniqueId());
            assert data != null;
            if (data.getClass().equals(Bill.class)) {
                continue;
            }
            if (targetWithLowestHealth == null || target.getHealth() < targetWithLowestHealth.getHealth()) {
                targetWithLowestHealth = target;
            }
        }
        if (targetWithLowestHealth == null) {
            player.sendMessage("§c没有可以发放便当的目标！");
            return false;
        }
        targetWithLowestHealth.getInventory().addItem(bento);
        player.sendMessage("§a成功给" + targetWithLowestHealth.getName() + "发放了便当！");
        return true;
    }
}
