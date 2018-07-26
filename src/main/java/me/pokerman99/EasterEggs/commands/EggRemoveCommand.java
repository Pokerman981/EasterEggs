package me.pokerman99.EasterEggs.commands;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import me.pokerman99.EasterEggs.data.ListTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class EggRemoveCommand implements CommandExecutor{

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;

        Main.removing.add(player.getIdentifier());

        Utils.sendMessage(player, "&aRight click the present/egg you wish to remove!");
        return CommandResult.success();
    }
}
