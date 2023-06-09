package dev.ex4.speedrunnerplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public final class SpeedrunnerPlugin extends JavaPlugin implements Listener {

    private SpeedrunManager manager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        manager = new SpeedrunManager(this);
        getCommand("startrun").setExecutor(new StartRunCommand(manager));
        getCommand("compass").setExecutor(new CompassCommand());
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (manager.runner != null) {
                    p.setCompassTarget(manager.runner.getLocation());
                }
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        event.setCancelled(manager.inCountdown);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getRespawnLocation().getWorld().getName().equals(manager.speedrunWorld.getName())) return;
        event.setRespawnLocation(manager.speedrunWorld.getSpawnLocation());
    }

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent event) {
        if (manager.speedrunWorld != null)
            event.setSpawnLocation(manager.speedrunWorld.getSpawnLocation());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
