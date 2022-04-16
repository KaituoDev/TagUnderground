package fun.kaituo;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FreezeListener implements Listener {
    Player p ;
    public FreezeListener(Player p) {
        this.p = p;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent pme) {
        if (!pme.getPlayer().equals(p)) {
            return;
        }
        if (pme.getTo() != null) {
            Location to = pme.getTo().clone();
            Location from = pme.getFrom().clone();
            to.setX(from.getX());
            to.setY(from.getY());
            pme.setTo(to);
        }
    }
}
