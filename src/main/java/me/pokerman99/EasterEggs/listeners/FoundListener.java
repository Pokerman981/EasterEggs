package me.pokerman99.EasterEggs.listeners;

import me.pokerman99.EasterEggs.Main;
import me.pokerman99.EasterEggs.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class FoundListener {
    public Main plugin;

    public FoundListener(Main pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Listener
    public void onFoundListener(me.pokerman99.EasterEggs.event.FoundEvent e, @First Player player){
        Boolean isNotFound = isFound(player.getUniqueId(), e.getData().getEggdata().get(2), Integer.valueOf(e.getData().getEggdata().get(1)), e.getData().getEggdata().get(0));
        if (isNotFound){
            final int total = Main.rootNode.getNode("types", e.getData().getEggdata().get(0), "total").getInt();
            final double rewardmoney = Main.rootNode.getNode("types", e.getData().getEggdata().get(0), "money").getInt();
            final int rewardtokens = Main.rootNode.getNode("types", e.getData().getEggdata().get(0), "tokens").getInt();

            Utils.sendMessage(player, "&aYou've successfully collected a present! " + found(player.getUniqueId(), e.getData().getEggdata().get(0)) + "/" + total + " found!");
            Utils.sendMessage(player, "&aYou've found "+ rewardtokens +" tokens and $"+ rewardmoney +"!");

            Utils.depositEcon(player, rewardmoney);
            Utils.setPlayerBalance(player.getUniqueId(), Utils.getPlayerBalance(player.getUniqueId()) + rewardtokens);
        } else {
            final int total = Main.rootNode.getNode("types", e.getData().eggdata().get(0), "total").getInt();

            Utils.sendMessage(player, "&cYou've already found this present! " + found(player.getUniqueId(), e.getData().getEggdata().get(0)) + "/" + total + " found!" );
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
                connection.close();
                return true;
            }

            resultSet.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int found(UUID playeruuid, String type) {
        String sql = "SELECT COUNT(*) as total FROM eggdata WHERE playeruuid='" + playeruuid + "' AND type='" + type + "';";

        try {
            Connection connection = Main.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();
            connection.close();

            return resultSet.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

}