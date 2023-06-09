package dev.ex4.speedrunnerplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class StartRunCommand implements CommandExecutor {
    private SpeedrunManager speedrunManager;
    public StartRunCommand(SpeedrunManager speedrunManager) {
        this.speedrunManager = speedrunManager;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(Component.text("Please specify a runner!", NamedTextColor.RED));
            return true;
        }
        Player runner;
        if (args.length == 0) runner = (Player) sender;
        else runner = Bukkit.getPlayer(args[0]);
        if (runner == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }
        speedrunManager.startRun(sender, runner);
        return true;
    }
}
