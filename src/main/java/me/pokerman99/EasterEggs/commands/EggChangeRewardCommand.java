package me.pokerman99.EasterEggs.commands;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import me.pokerman99.EasterEggs.data.ListTypes;
import me.pokerman99.EasterEggs.data.RewardTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.io.IOException;

public class EggChangeRewardCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String event_type = args.<ListTypes>getOne("event_type").get().toString();
        String reward_type = args.<RewardTypes>getOne("reward_type").get().toString().toLowerCase();
        String value = args.<String>getOne("value").get();

        Main.rootNode.getNode("types", event_type.toUpperCase(), reward_type).setValue(value);

        try {Main.getInstance().save();} catch (IOException e){e.printStackTrace();}

        Utils.sendMessage(src, "&e&l[PresentHunt] &aSuccessfully set " + event_type + "'s " + reward_type + " value to " + value);

        return CommandResult.success();
    }
}