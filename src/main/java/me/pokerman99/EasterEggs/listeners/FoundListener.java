package me.pokerman99.EasterEggs.listeners;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.io.IOException;

public class FoundListener {

    public Main plugin;

    public FoundListener(Main pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Listener
    public void onFoundListener(me.pokerman99.EasterEggs.event.FoundEvent e, @First Player player){
        int number = Integer.valueOf(e.getData().getEggdata().get(1));
        int total = Main.rootNode.getNode("types", e.getData().getEggdata().get(0), "total").getInt();
        int found = Main.rootNode.getNode("data", player.getIdentifier(), e.getData().getEggdata().get(0), "found").getInt();
        if (Main.rootNode.getNode("data", player.getIdentifier(), e.getData().getEggdata().get(0), String.valueOf(number)).isVirtual()){
            Main.rootNode.getNode("data", player.getIdentifier(), e.getData().getEggdata().get(0), String.valueOf(number)).setValue(true);
            Main.rootNode.getNode("data", player.getIdentifier(), e.getData().getEggdata().get(0), "found").setValue(found + 1);
            try {plugin.save();} catch (IOException e1){}
            Utils.sendMessage(player, "&aSuccessfully found a present! You've found " + (found+1) + "/" + (total) + " presents!");
            Utils.sendMessage(player, "&aYou've been rewarded 10 tokens and $1000!");
            Utils.sendMessage(player, "");

            Utils.depositEcon(player, 1000.0);
            Utils.setPlayerBalance(player.getUniqueId(), Utils.getPlayerBalance(player.getUniqueId()) + 10);
            return;

        } else {
            Utils.sendMessage(player, "&cYou've already found this present! " + found + "/" + total + " currently found!");
            Utils.sendMessage(player, "");
            return;
        }
    }

}
