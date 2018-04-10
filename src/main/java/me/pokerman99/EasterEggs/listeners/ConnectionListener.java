package me.pokerman99.EasterEggs.listeners;

import me.pokerman99.EasterEggs.Main;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.io.IOException;

public class ConnectionListener {

    public Main plugin;

    public ConnectionListener(Main pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Listener
    public void onConnection(ClientConnectionEvent.Join event){
        if (Main.rootNode.getNode("data", event.getTargetEntity().getIdentifier()).isVirtual()){
            Main.rootNode.getNode("data", event.getTargetEntity().getIdentifier(), "SPAWN", "found").setValue(0);
            Main.rootNode.getNode("data", event.getTargetEntity().getIdentifier(), "EASTER", "found").setValue(0);
            try {plugin.save();} catch (IOException e){}
            return;
        }
    }
}
