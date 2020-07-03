package discordwhitelist;

import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DiscordWhitelist extends JavaPlugin {

    private FileConfiguration dataConfig = null;
    private File dataFile = null;
    private FileConfiguration defaultMessagesConfig = null;
    private File defaultMessagesFile = null;
    private FileConfiguration pluginConfigConfig = null;
    private File pluginConfigFile = null;

    public static DiscordWhitelist instance;

    @Override
    public void onEnable() { //stuff to do on startup

        saveDefaultDefaultMessages();
        saveDefaultPluginConfig();

        String discordToken = getPluginConfig().getString("discordToken");
        String statusMessage = getDefaultMessages().getString("statusMessage");

        if (discordToken.length() < 1) { //if no token, disable the whole plugin
            System.out.print("No Discord token has been entered. Please configure the PluginConfig.yml file.");
            System.out.print("It can be found in your server folder > plugins > DiscordWhitelist > PluginConfig.yml");
            onDisable();
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        //makes an instance
        instance = this;

        //runs the event listener in ServerData
        getServer().getPluginManager().registerEvents(new ServerData(), this);

        try { //runs bot
            JDA jda = new JDABuilder(discordToken)
                    .addEventListeners(new DiscordHandlers())
                    .setActivity(Activity.playing(statusMessage))
                    .build();
            jda.awaitReady();
            System.out.println("Finished Building JDA!");
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() { //stuff to do on shutdown
        saveData();
    }

    //anything below this point is just related to .yml files

    /**
     Data
     */

    public void reloadData() { //reloading
        if (dataFile == null) {
            dataFile = new File(getDataFolder(), "data.yml");
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getData() { //getting
        if (dataConfig == null) {
            reloadData();
        }
        return dataConfig;
    }

    public void saveData() { //saving
        if (dataConfig == null || dataFile == null) {
            return;
        }
        try {
            getData().save(dataFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + dataFile, ex);
        }
    }

    /**
     Default Messages
     */

    public void reloadDefaultMessages() { //reloading
        if (defaultMessagesFile == null) {
            defaultMessagesFile = new File(getDataFolder(), "data.yml");
        }
        defaultMessagesConfig = YamlConfiguration.loadConfiguration(defaultMessagesFile);
    }

    public FileConfiguration getDefaultMessages() { //getting
        if (defaultMessagesConfig == null) {
            reloadDefaultMessages();
        }
        return defaultMessagesConfig;
    }

    public void saveDefaultMessages() { //saving
        if (defaultMessagesConfig == null || defaultMessagesFile == null) {
            return;
        }
        try {
            getData().save(defaultMessagesFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + defaultMessagesFile, ex);
        }
    }

    public void saveDefaultDefaultMessages() { //save default
        if (defaultMessagesFile == null) {
            defaultMessagesFile = new File(getDataFolder(), "defaultMessages.yml");
        }
        if (!defaultMessagesFile.exists()) {
            saveResource("defaultMessages.yml", false);
        }
    }

    /**
     Plugin Config
    */

    public void reloadPluginConfig() { //reloading
        if (pluginConfigFile == null) {
            pluginConfigFile = new File(getDataFolder(), "pluginConfig.yml");
        }
        pluginConfigConfig = YamlConfiguration.loadConfiguration(pluginConfigFile);
    }

    public FileConfiguration getPluginConfig() { //getting
        if (pluginConfigConfig == null) {
            reloadPluginConfig();
        }
        return pluginConfigConfig;
    }

    public void savePluginConfig() { //saving
        if (pluginConfigConfig == null || pluginConfigFile == null) {
            return;
        }
        try {
            getData().save(pluginConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + pluginConfigFile, ex);
        }
    }

    public void saveDefaultPluginConfig() { //save default
        if (pluginConfigFile == null) {
            pluginConfigFile = new File(getDataFolder(), "pluginConfig.yml");
        }
        if (!pluginConfigFile.exists()) {
            saveResource("pluginConfig.yml", false);
        }
    }
}