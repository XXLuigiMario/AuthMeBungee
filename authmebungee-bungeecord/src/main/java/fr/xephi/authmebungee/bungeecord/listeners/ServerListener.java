package fr.xephi.authmebungee.bungeecord.listeners;

import fr.xephi.authmebungee.bungeecord.annotations.IncomingChannel;
import fr.xephi.authmebungee.bungeecord.services.AuthPlayerManager;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ServerListener implements Listener {

    @Inject
    @IncomingChannel
    private String incomingChannel;

    @Inject
    private AuthPlayerManager authPlayerManager;

    public ServerListener() {
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        // Check if the message is for us
        if (!event.getTag().equals(incomingChannel)) {
            return;
        }

        // Check if a player is not trying to rip us off sending a fake message
        if (!(event.getSender() instanceof Server)) {
            return;
        }

        // Now that's sure, it's for us, so let's go
        event.setCancelled(true);

        try {
            // Read the plugin message
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

            // For now that's the only type of message the server is able to receive
            String task = in.readUTF();
            if (!task.equals("LOGIN:")) {
                return;
            }

            // Gather informations from the plugin message
            String name = in.readUTF();
            // Set the player status to logged in
            authPlayerManager.getAuthPlayer(name).setLogged(true);
        } catch (IOException ex) {
            // Something nasty happened
            ex.printStackTrace();
        }
    }
}
