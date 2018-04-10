package me.pokerman99.EasterEggs.commands;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.IOException;

public class ReloadCommand implements CommandExecutor {

    public Main plugin;

    public ReloadCommand(Main pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            plugin.rootNode = plugin.loader.load();
            Utils.sendMessage(src, "&aReloaded");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return CommandResult.success();
    }

}
