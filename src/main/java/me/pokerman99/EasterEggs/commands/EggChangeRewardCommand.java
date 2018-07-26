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
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;

public class EggChangeRewardCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        ListTypes event = args.<ListTypes>getOne("list").get();
        RewardTypes placeholderreward = args.<RewardTypes>getOne("rewards").get();
        int amount = args.<Integer>getOne("amount").get();

        String rewards = String.valueOf(placeholderreward).toLowerCase();

        Main.getInstance().rootNode.getNode("types", event.toString(), rewards).setValue(amount);
        try{Main.getInstance().save();} catch (IOException e){e.printStackTrace();}

        Utils.sendMessage(player, "&aSuccessfully set " + event + "'s " + rewards + " reward to " + amount);


        return CommandResult.success();
    }
}
