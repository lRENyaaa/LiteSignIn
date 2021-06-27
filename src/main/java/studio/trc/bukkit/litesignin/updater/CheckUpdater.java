package studio.trc.bukkit.litesignin.updater;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litesignin.config.MessageUtil;
import studio.trc.bukkit.litesignin.util.SignInDate;

public class CheckUpdater
{
    private static boolean foundANewVersion = false;
    private static String newVersion;
    private static String link;
    private static String description;
    private static Thread checkUpdateThread;
    private static SignInDate date;
    
    /**
     * Initialize programs.
     */
    public static void initialize() {
        checkUpdateThread = new Thread(() -> {
            try {
                URL url = new URL("https://trc.studio/resources/spigot/litesignin/update.yml");
                try (Reader reader = new InputStreamReader(url.openStream(), "UTF-8")) {
                    YamlConfiguration yaml = new YamlConfiguration();
                    yaml.load(reader);
                    String version = yaml.getString("latest-version");
                    String downloadLink = yaml.getString("link");
                    String description_;
                    if (MessageUtil.getLanguage().equalsIgnoreCase("Chinese") || MessageUtil.getLanguage().contains("中文")) {
                        description_ = yaml.getString("description.Chinese");
                    } else {
                        description_ = yaml.getString("description.Default");
                    }
                    String nowVersion = Bukkit.getPluginManager().getPlugin("LiteSignIn").getDescription().getVersion();
                    if (!nowVersion.equalsIgnoreCase(version)) {
                        newVersion = version;
                        foundANewVersion = true;
                        link = downloadLink;
                        description = description_;
                        Map<String, String> placeholders = new HashMap();
                        placeholders.put("{version}", version);
                        placeholders.put("%link%", MessageUtil.getMessage("Updater.Link.Console-Text"));
                        placeholders.put("{link}", downloadLink);
                        placeholders.put("{nowVersion}", nowVersion);
                        placeholders.put("{description}", description_);
                        MessageUtil.sendMessage(Bukkit.getConsoleSender(), "Updater.Checked", placeholders);
                        date = SignInDate.getInstance(new Date());
                    }
                } catch (InvalidConfigurationException | IOException ex) {
                    MessageUtil.sendMessage(Bukkit.getConsoleSender(), "Updater.Error");
                }
            } catch (MalformedURLException ex) {
                MessageUtil.sendMessage(Bukkit.getConsoleSender(), "Updater.Error");
            }
        });
    }
    
    /**
     * Start check updater.
     */
    public static void checkUpdate() {
        checkUpdateThread.start();
    }
    
    /**
     * Return whether found a new version.
     * @return 
     */
    public static boolean isFoundANewVersion() {
        return foundANewVersion;
    }
    
    /**
     * Get new version.
     * @return 
     */
    public static String getNewVersion() {
        return newVersion;
    }
    
    /**
     * Get download link.
     * @return 
     */
    public static String getLink() {
        return link;
    }
    
    /**
     * Get new version's update description.
     * @return 
     */
    public static String getDescription() {
        return description;
    }
    
    /**
     * Get the time of last check update.
     * @return 
     */
    public static SignInDate getTimeOfLastCheckUpdate() {
        return date;
    }
}
