package me.pokerman99.EasterEggs;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.pokerman99.EasterEggs.commands.*;
import me.pokerman99.EasterEggs.data.Data;
import me.pokerman99.EasterEggs.data.ListTypes;
import me.pokerman99.EasterEggs.data.RewardTypes;
import me.pokerman99.EasterEggs.listeners.FoundListener;
import me.pokerman99.EasterEggs.listeners.InteractBlockListener;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static me.pokerman99.EasterEggs.data.ListTypes.*;

@Plugin(id = "eastereggs",
        name = "EasterEggsEC",
        version = "2.0",
        description = "Plugin for Justin's servers providing easter eggs for the players")

public class Main {
	@Inject @DefaultConfig(sharedRoot = false)
	private Path defaultConfig;

	@Inject @DefaultConfig(sharedRoot = false)
	public ConfigurationLoader<CommentedConfigurationNode> loader;

	@Inject @ConfigDir(sharedRoot = false)
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
    public static Main instance;
    public static Main getInstance(){
        return instance;
    }
    public static EconomyService economyService;
    public static Map<UUID, Data> adding = new HashMap<>();
    public static List<String> removing = new ArrayList<>();
    public static String host;
    public static int port;
    public static String db;
    public static String user;
    public static String pass;
    public static Key<ListValue<String>> EGGDATA;

    public void save() throws IOException {
        loader.save(config());
    }

    @Listener
    public void onInit(GameInitializationEvent event) throws IOException{
		Optional<EconomyService> optionalEconomyService = Sponge.getServiceManager().provide(EconomyService.class);
        economyService = optionalEconomyService.get();
        rootNode = loader.load();
        instance = this;
        if (!defaultConfig.toFile().exists()){
            generateConfig();
        }

        if (rootNode.getNode("config-version").getString().equals("1.5")){
            //generateConfig();
            generateConfig17();
        }

        loadSQL();
        registerCommands();

        try {
            getConnection();
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DataRegistration.builder()
                .dataClass(Data.class)
                .immutableClass(Data.Immutable.class)
                .builder(new Data.Builder())
                .manipulatorId("eggdatas")
                .dataName("eggdata")
                .buildAndRegister(Sponge.getPluginManager().getPlugin("eastereggs").get());
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent e) {
        EGGDATA = Key.builder()
                .type(new TypeToken<ListValue<String>>(){})
                .query(DataQuery.of("Eggdata"))
                .id("eastereggs:eggdata")
                .name("Eggdata")
                .build();
    }

    @Listener
    public void onRegister(GameRegistryEvent.Register<Key<?>> e) {
        e.register(EGGDATA);
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

        CommandSpec ChangeRewardCommand = CommandSpec.builder()
                .permission("easteregg.admin")
                .arguments(GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("event_type"), ListTypes.class)),
                        GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("reward_type"), RewardTypes.class)),
                        GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("value"))))
                .executor(new EggChangeRewardCommand())
                .build();

        CommandSpec LocationRemoveCommand = CommandSpec.builder()
                .permission("easteregg.admin")
                .arguments(GenericArguments.onlyOne(GenericArguments.enumValue(Text.of("list"), ListTypes.class)))
                .executor(new EggRemoveCommand())
                .build();

        CommandSpec CheckCommand = CommandSpec.builder()
                .permission("easteregg.check")
                .executor(new CheckCommand())
                .build();

        CommandSpec ReloadCommand = CommandSpec.builder()
                .permission("easteregg.admin")
                .executor(new ReloadCommand(this)).build();

        CommandSpec Main = CommandSpec.builder()
                .permission("easteregg.base")
                .child(LocationAddCommand, "add")
                .child(ChangeRewardCommand, "change")
                .child(LocationRemoveCommand, "remove")
                .child(CheckCommand, "check")
                .child(ReloadCommand, "reload")
                .build();

        Sponge.getCommandManager().register(this, Main, "easteregg", "presents");
        Sponge.getEventManager().registerListeners(this, new FoundListener(this));
        Sponge.getEventManager().registerListeners(this, new InteractBlockListener());
    }

    private void generateConfig()throws IOException{
        rootNode.getNode("config-version").setValue("1.6");
        rootNode.getNode("types").setValue(null);

        CommentedConfigurationNode total = rootNode.getNode("types");
        total.getNode(SPAWN, "total").setValue(0);
        total.getNode(EASTER, "total").setValue(0);
        total.getNode(DSHOP, "total").setValue(0);
        total.getNode(EV, "total").setValue(0);
        total.getNode(SAFARI, "total").setValue(0);
        total.getNode(GYMS, "total").setValue(0);
        total.getNode(HUB, "total").setValue(0);
        total.getNode(WILDS, "total").setValue(0);
        total.getNode(ADEVENTURE, "total").setValue(0);
        total.getNode(BATTLE, "total").setValue(0);
        total.getNode(SHRINES, "total").setValue(0);

        CommentedConfigurationNode money = rootNode.getNode("types");
        money.getNode(SPAWN, "money").setValue(500);
        money.getNode(EASTER, "money").setValue(500);
        money.getNode(DSHOP, "money").setValue(500);
        money.getNode(EV, "money").setValue(500);
        money.getNode(SAFARI, "money").setValue(500);
        money.getNode(GYMS, "money").setValue(300);
        money.getNode(HUB, "money").setValue(400);
        money.getNode(WILDS, "money").setValue(400);
        money.getNode(ADEVENTURE, "money").setValue(300);
        money.getNode(BATTLE, "money").setValue(300);
        money.getNode(SHRINES, "money").setValue(300);

        CommentedConfigurationNode tokens = rootNode.getNode("types");
        tokens.getNode(SPAWN, "tokens").setValue(5);
        tokens.getNode(EASTER, "tokens").setValue(5);
        tokens.getNode(DSHOP, "tokens").setValue(25);
        tokens.getNode(EV, "tokens").setValue(25);
        tokens.getNode(SAFARI, "tokens").setValue(25);
        tokens.getNode(GYMS, "tokens").setValue(10);
        tokens.getNode(HUB, "tokens").setValue(20);
        tokens.getNode(WILDS, "tokens").setValue(20);
        tokens.getNode(ADEVENTURE, "tokens").setValue(10);
        tokens.getNode(BATTLE, "tokens").setValue(10);
        tokens.getNode(SHRINES, "tokens").setValue(10);
        save();
    }

    private void generateConfig17()throws IOException{
        CommentedConfigurationNode total = rootNode.getNode("types");
        total.getNode(HALLOWEEN, "total").setValue(0);


        CommentedConfigurationNode money = rootNode.getNode("types");
        money.getNode(HALLOWEEN, "money").setValue(500);


        CommentedConfigurationNode tokens = rootNode.getNode("types");
        tokens.getNode(HALLOWEEN, "tokens").setValue(5);

        rootNode.getNode("config-version").setValue("1.7");
        save();
    }

    public void createTables() {
        //language=H2
        String sql = "CREATE TABLE IF NOT EXISTS `eggdata` ( `id` INT NOT NULL , `playeruuid` VARCHAR(50) NOT NULL , `egguuid` VARCHAR(50) NOT NULL , `type` VARCHAR(50) NOT NULL);";
        execute(sql);
    }

    public Connection getConnection() throws SQLException {
       return DriverManager.getConnection("jdbc:h2:./config/eastereggs/EggData");
    }

    public static void execute(String sql) {
        try {
            Connection connection = Main.getInstance().getConnection();
            connection.prepareStatement(sql).executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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