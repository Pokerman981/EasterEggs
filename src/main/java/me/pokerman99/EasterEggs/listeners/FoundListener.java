package me.pokerman99.EasterEggs.listeners;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import me.pokerman99.EasterEggs.event.FoundEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;

import java.sql.*;
import java.util.UUID;

public class FoundListener {
    public Main plugin;

    public FoundListener(Main pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Listener
    public void onFoundListener(FoundEvent e, @First Player player){
        String type = e.getData().getEggdata().get(0);
        Boolean isNotFound = isFound(player.getUniqueId(), e.getData().getEggdata().get(2), Integer.valueOf(e.getData().getEggdata().get(1)), type);
        int total = Main.rootNode.getNode("types", type, "total").getInt();
        int count = found(player.getUniqueId(), type);

        if (isNotFound) {
            double rewardmoney = Main.rootNode.getNode("types", type, "money").getInt();
            int rewardtokens = Main.rootNode.getNode("types", type, "tokens").getInt();

            Utils.sendMessage(player, "");
            Utils.sendMessage(player, "&e&l[PresentHunt] &aYou've found a present containing " + rewardtokens + " Tokens and $" + rewardmoney
                    + "! &o(" + count + "/" + total + " found in the " + type.toLowerCase() + " category)");
            Utils.sendMessage(player, "");

            Utils.depositEcon(player, rewardmoney);
            Utils.setPlayerBalance(player.getUniqueId(), Utils.getPlayerBalance(player.getUniqueId()) + rewardtokens);

            if (count == total) {
                if (player.getOption("presents-completed-" + type.toLowerCase()).isPresent())  return;
                String reward = Main.rootNode.getNode("types", type, "completion_command").getString(null);

                Utils.sendMessage(player, "&e&l[PresentHunt] &aCongratulations, you found every present in the &l" + type.toLowerCase() + " &acategory!");

                if (reward != null) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
                            "lp user " + player.getName() + " meta set presents-completed-" + type.toLowerCase() + " true");

                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), reward.replaceAll("%player%", player.getName())) ;
                }
            }
        } else {
            Utils.sendMessage(player, "&e&l[PresentHunt] &cYou've already found this present! &o(" + count + "/" + total + " found in the " + type.toLowerCase() + " category)");
        }
    }

    private Boolean isFound(UUID playeruuid, String egguuid, int id, String type){
        String sql = "SELECT COUNT(*) as total FROM eggdata WHERE playeruuid='" + playeruuid + "' AND egguuid='"+ egguuid +"';";

        try {
            Connection connection = Main.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();

            if (resultSet.getInt("total") == 0 ){
                String sql2 = "INSERT INTO eggdata(id,playeruuid,egguuid,type) VALUES (?,?,?,?);";
                PreparedStatement preparedStatement = connection.prepareStatement(sql2);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, playeruuid.toString());
                preparedStatement.setString(3, egguuid);
                preparedStatement.setString(4, type);
                preparedStatement.execute();
                resultSet.close();
                return true;
            }

            resultSet.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int found(UUID playeruuid, String type) {
        String sql = "SELECT COUNT(*) as total FROM eggdata WHERE playeruuid='" + playeruuid + "' AND type='" + type + "';";

        try {
            Connection connection = Main.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();

            return resultSet.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}