package dev.ex4.speedrunnerplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

public class SpeedrunManager {
    private Plugin plugin;

    Player runner = null;

    World speedrunWorld = null;

    boolean inCountdown = false;
    public SpeedrunManager(Plugin plugin) {
        this.plugin = plugin;
    }
    public void startRun(CommandSender sender, Player runner) {
        this.runner = runner;
        sender.sendMessage(Component.text("Deleting existing speedrun world...", NamedTextColor.YELLOW));
        deleteWorld(Bukkit.getWorld("speedrun"));
        sender.sendMessage(Component.text("Deleting existing nether & end...", NamedTextColor.YELLOW));
        deleteWorld(Bukkit.getWorld("world_nether"));
        deleteWorld(Bukkit.getWorld("world_the_end"));
        sender.sendMessage(Component.text("Generating new speedrun world...", NamedTextColor.YELLOW));
        speedrunWorld = Bukkit.createWorld(new WorldCreator("speedrun").seed(System.currentTimeMillis()));
        sender.sendMessage(Component.text("Teleporting players...", NamedTextColor.YELLOW));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(speedrunWorld.getSpawnLocation());
            p.getInventory().clear();
            p.setHealth(20f);
            p.setFoodLevel(20);
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
        }
        sender.sendMessage(Component.text("Starting soon!", NamedTextColor.YELLOW));
        final int[] timeLeft = {10};
        final int[] id = {-1};
        inCountdown = true;
        id[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (timeLeft[0] <= 0) {
                    Bukkit.getScheduler().cancelTask(id[0]);
                    p.showTitle(Title.title(Component.text("GO!", NamedTextColor.GREEN, TextDecoration.BOLD), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)));
                    inCountdown = false;
                    return;
                }
                p.showTitle(Title.title(Component.text(timeLeft[0], NamedTextColor.AQUA), Component.text("Get ready!", NamedTextColor.YELLOW), Title.Times.times(Duration.ZERO, Duration.ofSeconds(5), Duration.ZERO)));
                timeLeft[0]--;
            }
        }, 0, 20);
    }

    private static void deleteWorld(World world) {
        if (world != null) {
            World defaultWorld = Bukkit.getWorld("world");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(world)) {
                    p.teleport(defaultWorld.getSpawnLocation());
                }
                Bukkit.unloadWorld(world, false);
                File worldFolder = world.getWorldFolder();

                deleteDirectory(worldFolder);
            }
        }
    }

    private static void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDirectory(f);
                }
            }
        }
        file.delete();
    }
}
