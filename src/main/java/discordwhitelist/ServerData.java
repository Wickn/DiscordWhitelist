package discordwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import org.apache.commons.io.IOUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.io.IOException;
import java.net.URL;

import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServerData implements Listener {
    public static void ServerWhitelist() {
        //gets UUID from name. getScheduler cuz if not it'd stop the server for a solid second each time
        Bukkit.getScheduler().runTaskAsynchronously(DiscordWhitelist.instance, () -> {
            String mcUsername = DiscordHandlers.getMcUsername();
            String mcUuid = getUuid(mcUsername);

            DiscordHandlers.uuidSuccess(mcUuid);
        });
    }

    public static void AddToWhitelist(String uuid) {
        String mcUsername = DiscordHandlers.getMcUsername();
        String discordID = DiscordHandlers.getDiscordID();
        String discordUsername = DiscordHandlers.getDiscordUsername();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        System.out.print("Minecraft user " + mcUsername + " (" + uuid + ") has been whitelisted on " + date + ". They are known as " + discordUsername + " (" + discordID + ") on Discord. ");

        DiscordWhitelist.instance.getData().set(uuid, "");
        DiscordWhitelist.instance.getData().set(uuid + ".uuid", uuid);
        DiscordWhitelist.instance.getData().set(uuid + ".mcUsername", mcUsername);
        DiscordWhitelist.instance.getData().set(uuid + ".discordID", discordID);
        DiscordWhitelist.instance.getData().set(uuid + ".discordUsername", discordUsername);
        DiscordWhitelist.instance.getData().set(uuid + ".addedOn", date);


        DiscordWhitelist.instance.saveData();

        DiscordHandlers.whitelistSuccessful();
    }


    /**
    ALWAYS DO THIS TO GET THE UUID:

    Bukkit.getScheduler().runTaskAsynchronously(DiscordWhitelist.instance, () -> {
        getUuid();
    });

    IF NOT YOU WILL STOP THE SERVER FOR SEVERAL SECONDS
    */
    public static String getUuid(String mcUsername) {
        if (mcUsername.contains(" ")) {
            return "invalid name";
        }
        String url = "https://api.mojang.com/users/profiles/minecraft/" + mcUsername;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));
            if(UUIDJson.isEmpty()) {
                return "invalid name";
            }
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            return UUIDObject.get("id").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return "error";
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String uuidDash = uuid.toString();
        String uuidClean = uuidDash.replace("-", "");
        String whitelistUuid = DiscordWhitelist.instance.getData().getString(uuidClean + ".uuid");

        String kickReason = DiscordWhitelist.instance.getDefaultMessages().getString("deniedAccessKickReason");

        if (!uuidClean.equals(whitelistUuid)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickReason);
        }
    }
}
