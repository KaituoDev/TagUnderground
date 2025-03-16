package fun.kaituo.tagunderground.character;

import fun.kaituo.tagunderground.state.HuntState;
import fun.kaituo.tagunderground.util.Human;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class Leaf extends Human {
    public static final String displayName = "莉耶芙";
    public static final String chooseMessage = "对吧,这果然就是所谓的命运啊!";
    public static final ChatColor color = ChatColor.GREEN;
    
    private final int itemObtainCooldown;
    private int itemObtainCooldownCounter = 0;

    public Leaf(Player p) {
        super(p);
        itemObtainCooldown = getConfigInt("item-obtain-cooldown");
    }


    @Override
    public void tick() {
        super.tick();
        if (itemObtainCooldownCounter == 0) {
            player.getInventory().addItem(HuntState.INST.getRandomItem());
            itemObtainCooldownCounter = itemObtainCooldown;
            player.sendMessage("§a获得随机道具！");
        } else {
            itemObtainCooldownCounter -= 1;
        }
    }
}
