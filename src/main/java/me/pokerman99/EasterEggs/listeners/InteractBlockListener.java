package me.pokerman99.EasterEggs.listeners;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import me.pokerman99.EasterEggs.data.Data;
import me.pokerman99.EasterEggs.event.FoundEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class InteractBlockListener {

	@Listener
	public void onRightClick(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        if (!(event instanceof InteractBlockEvent.Secondary.MainHand)) return;
        Location<World> location = event.getTargetBlock().getLocation().get();
        Optional<TileEntity> tileEntity = location.getTileEntity();

        if (!tileEntity.isPresent() || !location.getTileEntity().get().getType().equals(TileEntityTypes.SKULL)) return;

        if (Main.adding.containsKey(player.getUniqueId())){
            Data data = Main.adding.get(player.getUniqueId());
            tileEntity.get().offer(new Data(data.getEggdata()));

            Utils.sendMessage(player, "&aSuccessfully set egg/present!");

            Main.adding.remove(player.getUniqueId());

        } else if (location.get(Data.class).isPresent()){
            Data data = location.get(Data.class).get();
            Cause cause = Cause.builder().append(player).build(EventContext.builder().add(EventContextKeys.PLAYER_SIMULATED, player.getProfile()).build());

            Sponge.getEventManager().post(new FoundEvent(player, data, cause));
        }


    }
	
}
