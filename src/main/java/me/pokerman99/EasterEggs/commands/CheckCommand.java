package me.pokerman99.EasterEggs.commands;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import me.pokerman99.EasterEggs.data.ListTypes;
import me.pokerman99.EasterEggs.listeners.FoundListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class CheckCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        boolean completed_any = false;

        for (ListTypes type_enum : ListTypes.values()) {
            String type = type_enum.toString();
            int total = Main.rootNode.getNode("types", type.toUpperCase(), "total").getInt(0);

            if (total == 0 || player.getOption("presents-completed-" + type.toLowerCase()).isPresent()) continue;

            int found = FoundListener.found(player.getUniqueId(), type);

            if (total <= found) {
                Utils.sendMessage(player, "&e&l[PresentHunt] &aCongratulations, you found every present in the &l" + type.toLowerCase() + "&a category!");
                String reward = Main.rootNode.getNode("types", type, "completion_command").getString(null);
                completed_any = true;

                if (reward != null) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
                            "lp user " + player.getName() + " meta set presents-completed-" + type.toLowerCase() + " true");

                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), reward.replaceAll("%player%", player.getName())) ;
                }
            }
        }

        if (!completed_any) {
            Utils.sendMessage(player, "&e&l[PresentHunt] &cYou aren't eligible to claim a completion reward in any category");
        }

        return CommandResult.success();
    }
}