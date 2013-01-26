package com.github.DarkSeraphim.PunishTheBad;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author DarkSeraphim
 */
public class PunishTheBad extends JavaPlugin implements Listener
{
    
    HashMap<String, String> commandPerm = new HashMap<String, String>();
    
    String punishment = "";
    
    @Override
    public void onEnable()
    {
        getLogger().info("PunishTheBad - created by DarkSeraphim");
        if(!getDataFolder().exists() || !getDataFolder().isDirectory())
        {
            getDataFolder().mkdirs();
        }
        InputStream defStream = getResource("config.yml");
        if(defStream != null)
        {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
            getConfig().setDefaults(defConfig);
            getConfig().options().copyHeader(true);
            saveConfig();
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        
        List<String> cmdsNperms = getConfig().getStringList("commands");
        for(String cmdNperm : cmdsNperms)
        {
            String[] parts = cmdNperm.split(":",2);
            String cmd = (parts.length > 0 ? parts[0] : "");
            String perm = (parts.length == 2 ? parts[1] : "");
            if(!cmd.isEmpty())
            {
                commandPerm.put("/"+cmd, perm);
            }
        }
        
        this.punishment = getConfig().getString("punishment");
    }
    
    @Override
    public void onDisable()
    {
        
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e)
    {
        Player p = e.getPlayer();
        String cmd = e.getMessage().split(" ")[0];
        if(commandPerm.containsKey(cmd))
        {
            String perm = commandPerm.get(cmd);
            boolean hasPerm = (perm.isEmpty() ? p.isOp() : p.hasPermission(perm));
            if(!hasPerm)
            {
                e.setCancelled(true);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.punishment.replace("%p", p.getName()));
            }
        }
    }
}
