package fun.kaituo.tagunderground.state;

import fun.kaituo.gameutils.game.GameState;
import fun.kaituo.tagunderground.TagUnderground;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

import static fun.kaituo.gameutils.util.Misc.displayCountDown;

public class ReadyState implements GameState {
    public static final int COUNTDOWN_SECONDS = 10;
    public static final ReadyState INST = new ReadyState();
    private ReadyState() {}
    private Set<Integer> taskIds = new HashSet<>();

    private TagUnderground game;

    public void init() {
        game = TagUnderground.inst();
    }

    @Override
    public void enter() {
        for (Player p : game.getPlayers()) {
            addPlayer(p);
            taskIds.addAll(displayCountDown(p, COUNTDOWN_SECONDS, game));
        }
        taskIds.add(Bukkit.getScheduler().runTaskLater(game, () -> {
            game.setState(HuntState.INST);
        }, COUNTDOWN_SECONDS * 20).getTaskId());
    }

    @Override
    public void exit() {
        for (Player p : TagUnderground.inst().getPlayers()) {
            removePlayer(p);
        }
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void addPlayer(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 4, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, -1, 0, false, false));
    }

    @Override
    public void removePlayer(Player p) {
        p.removePotionEffect(PotionEffectType.RESISTANCE);
        p.removePotionEffect(PotionEffectType.SATURATION);
    }

    @Override
    public void forceStop() {
        game.setState(WaitState.INST);
    }
}
