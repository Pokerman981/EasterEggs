package me.pokerman99.EasterEggs;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.DimensionType;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;

import static me.pokerman99.EasterEggs.data.ListTypes.EASTER;

public class Utils {
	
	public Main plugin;

	public Utils(Main pluginInstance) {
		this.plugin = pluginInstance;
	}
	
    public static MetaData getMetaData(UUID uuid) {
        LuckPermsApi api = LuckPerms.getApi();

        User user = api.getUserSafe(uuid).orElse(null);
        if (user == null) {
            return null;
        }

        Contexts contexts = api.getContextForUser(user).orElse(null);
        if (contexts == null) {
            return null;
        }

        return user.getCachedData().getMetaData(contexts);
    }
    
    public static String timeDiffFormat(long timeDiff) {
        int seconds = (int) timeDiff % 60;
        timeDiff = timeDiff / 60;
        int minutes = (int) timeDiff % 60;
        timeDiff = timeDiff / 60;
        int hours = (int) timeDiff % 24;
        timeDiff = timeDiff / 24;
        int days = (int) timeDiff;

        String timeFormat;

        //Formatting
        if (days > 7) {
            timeFormat = days + " days";
        } else if (days > 0) {
            timeFormat = days + "d " + hours + "h";
        } else if (days == 0 && hours > 0) {
            timeFormat = hours + "h " + minutes + "m " + seconds + "s";
        } else if (days == 0 && hours == 0 && minutes > 0) {
            timeFormat = minutes + "m " + seconds + "s";
        } else {
            timeFormat = seconds + "s";
        }

        return timeFormat;
    }
    
	public static DimensionType getDim(UUID uuid){
		DimensionType dim = Sponge.getServer().getWorld(uuid).get().getDimension().getType();
		return dim;
	}

    
    public static String color(String string) {
        return TextSerializers.FORMATTING_CODE.serialize(Text.of(string));
    }

    public static void sendMessage(CommandSource sender, String message) {
        if (sender == null) { return; }
        sender.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(color(message)));
    }

    private final static Currency currency = Main.economyService.getDefaultCurrency();

    public static double getBalanceEcon(Player player) {
        BigDecimal bal = Main.economyService.getOrCreateAccount(player.getUniqueId()).get().getBalances().get(currency);
        return bal.doubleValue();
    }

    public static void withdrawEcon(Player player, double num) {
        BigDecimal value = new BigDecimal(num);
        Main.economyService.getOrCreateAccount(player.getUniqueId()).get().withdraw(currency , value, Sponge.getCauseStackManager().getCurrentCause());
    }

    public static void depositEcon(Player player, double num) {
        BigDecimal value = new BigDecimal(num);
        Main.economyService.getOrCreateAccount(player.getUniqueId()).get().deposit(currency, value, Sponge.getCauseStackManager().getCurrentCause());
    }

    private static String uri;

    public static void Tokens() {
        uri = "jdbc:mysql://" + Main.user + ":" + Main.pass + "@" + Main.host + ":" + Main.port + "/" + Main.db;
    }

    public static Connection getConnection() {
        try {
            final SqlService sql = Sponge.getServiceManager().provide(SqlService.class).get();
            final Connection con = sql.getDataSource("jdbc:mysql://" + Main.host + ":" + Main.port + "/" + Main.db + "?user=" + Main.user + "&password=" + Main.pass).getConnection();
            return con;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setPlayerBalance(final UUID uuid, final Number tokens) {
        try {
            final Connection conn = getConnection();
            final String sql = "INSERT INTO TOKENS (UUID, TOKENS) VALUES(?,?) ON DUPLICATE KEY UPDATE tokens=?;";
            final PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            ps.setInt(2, tokens.intValue());
            ps.setInt(3, tokens.intValue());
            ps.executeUpdate();
            ps.close();
            conn.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to update database");
            e.printStackTrace();
        }
    }

    public static int getPlayerBalance(final UUID uuid) {
        int tokens = 0;
        try {
            final Connection conn = getConnection();
            final String sql = "SELECT * FROM TOKENS WHERE uuid=?";
            final PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tokens = rs.getInt("tokens");
            }
            ps.close();
            conn.close();
        }
        catch (SQLException e) {
            System.out.println("Failed to check database");
            e.printStackTrace();
        }
        return tokens;
    }
}
