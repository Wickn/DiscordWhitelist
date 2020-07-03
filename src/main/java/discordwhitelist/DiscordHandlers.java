package discordwhitelist;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.stream.Collectors;

public class DiscordHandlers extends ListenerAdapter {
    private static String mcUsername;
    private static String discordID;
    private static String discordUsername;
    private static User discordUser;

    public static String getMcUsername() {
        return mcUsername;
    }
    public static String getDiscordID() {
        return discordID;
    }
    public static String getDiscordUsername() {
        return discordUsername;
    }
    public static User getDiscordUser() {
        return discordUser;
    }

    static String usernameUnknownError = DiscordWhitelist.instance.getDefaultMessages().getString("usernameUnknownError");
    static String usernameInvalid = DiscordWhitelist.instance.getDefaultMessages().getString("usernameInvalid");
    static String attemptingWhitelist = DiscordWhitelist.instance.getDefaultMessages().getString("attemptingWhitelist");
    static String successfulWhitelist = DiscordWhitelist.instance.getDefaultMessages().getString("successfulWhitelist");
    static String onPingResponse = DiscordWhitelist.instance.getDefaultMessages().getString("onPingResponse");

    static String discordAdmins = DiscordWhitelist.instance.getPluginConfig().getString("discordAdmins");

    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA();

        Message message = event.getMessage(); //typecasting
        MessageChannel channel = event.getChannel(); //typecasting
        String msg = message.getContentDisplay(); //typecasting

        String discordIDCompare = event.getAuthor().getId();
        String selfUser = jda.getSelfUser().getId();
        boolean mentionsMe = !message.getMentionedUsers()
                .stream()
                .filter(x -> x.getId().equals(selfUser))
                .collect(Collectors.toList())
                .isEmpty();

        if (event.isFromType(ChannelType.PRIVATE)) {
            if (!event.getAuthor().isBot()) {
                if (msg.contains("\"")) {
                    channel.sendMessage(attemptingWhitelist).queue();

                    String username = msg.replace("\"", "");

                    discordID = event.getAuthor().getId();
                    discordUsername = event.getAuthor().getAsTag();
                    discordUser = event.getAuthor();
                    mcUsername = username;

                    ServerData.ServerWhitelist();
                }

                /*else if (discordAdmins.contains(discordIDCompare)) {
                    if (msg.equalsIgnoreCase("comrade")) {
                        channel.sendMessage("commie").queue();
                    }
                    else {
                        String msgIgnoredCase = msg.toLowerCase();
                        String key = DiscordWhitelist.instance.getDefaultMessages().getString(msgIgnoredCase);
                        if (key != null) {
                            channel.sendMessage(key).queue();
                        }
                    }
                }*/

                else {
                    String msgIgnoredCase = msg.toLowerCase();
                    String key = DiscordWhitelist.instance.getDefaultMessages().getString(msgIgnoredCase);
                    if (key != null) {
                        channel.sendMessage(key).queue();
                    }
                }
            }
        }

        else if (mentionsMe) {
            channel.sendMessage(onPingResponse).queue();
        }
    }

    //just tells people if their entered username is wrong. if correct, it'll go log 'em
    public static void uuidSuccess(String uuid) {
        User user = DiscordHandlers.getDiscordUser();
        if (uuid.equals("error")) {
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(usernameUnknownError).queue();
            });
        }

        else if (uuid.equals("invalid name")) {
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(usernameInvalid).queue();
            });
        }

        else { //UUID is valid
            ServerData.AddToWhitelist(uuid);
        }
    }

    public static void whitelistSuccessful() { //literally just tells the user they were successful
        User user = getDiscordUser();
        user.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(successfulWhitelist).queue();
        });
    }
}