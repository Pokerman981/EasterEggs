package me.pokerman99.EasterEggs.commands;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import me.pokerman99.EasterEggs.data.Data;
import me.pokerman99.EasterEggs.data.ListTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EggAddCommand implements CommandExecutor{

    public Main plugin;

    public EggAddCommand(Main pluginInstance) {
        this.plugin = pluginInstance;
    }


    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player = (Player) src;
		ListTypes event = args.<ListTypes>getOne("list").get();
		String total = plugin.rootNode.getNode("types", event.toString(), "total").getString("0");
		UUID random = UUID.randomUUID();

		List<String> temp = new ArrayList<>();
		temp.add(event.toString());
		temp.add(String.valueOf((Integer.valueOf(total) +1)));
		temp.add(random.toString());

		Main.adding.put(player.getUniqueId(), new Data(temp));

		Utils.sendMessage(player,"&aRight click a minecraft:skull to set the egg/present!");

        return CommandResult.success();
	}
	

}
