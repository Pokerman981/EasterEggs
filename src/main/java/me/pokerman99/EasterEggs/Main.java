package me.pokerman99.EasterEggs;

import com.google.inject.Inject;
import me.pokerman99.EasterEggs.commands.EggAddCommand;
import me.pokerman99.EasterEggs.commands.EggRemoveCommand;
import me.pokerman99.EasterEggs.commands.ReloadCommand;
import me.pokerman99.EasterEggs.data.Data;
import me.pokerman99.EasterEggs.data.ListTypes;
import me.pokerman99.EasterEggs.listeners.ConnectionListener;
import me.pokerman99.EasterEggs.listeners.FoundListener;
import me.pokerman99.EasterEggs.listeners.InteractBlockListener;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static me.pokerman99.EasterEggs.data.ListTypes.EASTER;
import static me.pokerman99.EasterEggs.data.ListTypes.SPAWN;

@Plugin(id = "eastereggs",
name = "EasterEggsEC",
version = "2.0",
description = "Plugin for Justin's servers providing easter eggs for the players",
dependencies = {
        @Dependency(id = "luckperms", optional = false)
})

public class Main {

	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = false)
	public ConfigurationLoader<CommentedConfigurationNode> loader;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path ConfigDir;
	
	@Inject
	public PluginContainer plugin;
	public PluginContainer getPlugin() {
		return this.plugin;
	}

	public static CommentedConfigurationNode rootNode;

	public static CommentedConfigurationNode config() {
		return rootNode;
	}

	public void save() throws IOException {
		loader.save(config());
	}

	@Inject
	private Logger logger;

	public Logger getLogger() {
		return logger;
	}
	
    public static Main instance;

    public static Main getInstance(){
        return instance;
    }
    
    public static EconomyService economyService;

    public static Map<UUID, Data> adding = new HashMap<>();
    public static Map<UUID, Data> removing = new HashMap<>();
    
    public static String host;
    public static int port;
    public static String db;
    public static String user;
    public static String pass;
    
    @Listener
    public void onInit(GameInitializationEvent event) throws IOException{
		Optional<EconomyService> optionalEconomyService = Sponge.getServiceManager().provide(EconomyService.class);
        economyService = optionalEconomyService.get();
        rootNode = loader.load();
        instance = this;
        if (!defaultConfig.toFile().exists()){
            generateConfig();
        }

        if (rootNode.getNode("config-version").getString().equals("1.2")){
            rootNode.getNode("locations").setValue(null);
            rootNode.getNode("easter").setValue(null);
            generateConfig();
        }

        loadSQL();

        registerCommands();

        DataRegistration.builder()
                .dataClass(Data.class)
                .immutableClass(Data.Immutable.class)
                .builder(new Data.Builder())
                .manipulatorId("eggdatas")
                .dataName("eggdata")
                .buildAndRegister(Sponge.getPluginManager().getPlugin("eastereggs").get());
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event){
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "easteregg reload");
    }

    private void registerCommands(){
        CommandSpec LocationAddCommand = CommandSpec.builder()
                .permission("easteregg.admin")
                .arguments(GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("list"), ListTypes.class)))
                .executor(new EggAddCommand(this))
                .build();

        CommandSpec LocationRemoveCommand = CommandSpec.builder()
                .permission("easteregg.admin")
                .arguments(GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("list"), ListTypes.class)))
                .executor(new EggRemoveCommand())
                .build();

        CommandSpec ReloadCommand = CommandSpec.builder()
                .permission("easteregg.admin")
                .executor(new ReloadCommand(this)).build();

        CommandSpec Main = CommandSpec.builder()
                .permission("easteregg.admin")
                .child(LocationAddCommand, "add")
                .child(ReloadCommand, "reload")
                .build();

        Sponge.getCommandManager().register(this, Main, "easteregg", "presents");

        Sponge.getEventManager().registerListeners(this, new FoundListener(this));
        Sponge.getEventManager().registerListeners(this, new ConnectionListener(this));
        Sponge.getEventManager().registerListeners(this, new InteractBlockListener());
    }

    private void generateConfig()throws IOException{
        rootNode.getNode("config-version").setValue("1.3");
        rootNode.getNode("types").setValue(null);
        rootNode.getNode("types", SPAWN, "total").setValue(0);
        rootNode.getNode("types", EASTER, "total").setValue(0);
        rootNode.getNode("data").setValue(null);
        save();
    }

    private void loadSQL(){
        if (rootNode.getNode("sql").isVirtual()){
            rootNode.getNode("sql", "host").setValue("localhost");
            rootNode.getNode("sql", "user").setValue("change");
            rootNode.getNode("sql", "db").setValue("change");
            rootNode.getNode("sql", "pass").setValue("change");
            rootNode.getNode("sql", "port").setValue("3306");
            try {save();} catch (IOException e) {e.printStackTrace();}
        }

        Main.host = rootNode.getNode(new Object[] { "sql" }).getNode(new Object[] { "host" }).getString();
        Main.port = rootNode.getNode(new Object[] { "sql" }).getNode(new Object[] { "port" }).getInt();
        Main.db = rootNode.getNode(new Object[] { "sql" }).getNode(new Object[] { "db" }).getString();
        Main.user = rootNode.getNode(new Object[] { "sql" }).getNode(new Object[] { "user" }).getString();
        Main.pass = rootNode.getNode(new Object[] { "sql" }).getNode(new Object[] { "pass" }).getString();
    }

  
    
    
    
    
	
}
