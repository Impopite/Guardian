package it.impo.protect.update;

import it.impo.protect.Protect;
import it.impo.protect.config.constant.ConfigKey;
import it.impo.protect.config.constant.LangKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;

public class UpdateChecker {

    private static final String API_URL = "https://api.modrinth.com/v2/project/protect/version";

    private final Protect plugin;
    private final HttpClient httpClient;

    private volatile boolean updateAvailable;
    private volatile String latestVersion;

    public UpdateChecker(Protect plugin) {
        this.plugin = plugin;
        this.updateAvailable = false;
        this.latestVersion = null;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void checkAsync() {
        if (!plugin.getConfigLoader().get(ConfigKey.UPDATE_CHECKER, true)) return;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::check);
    }

    private void check() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "AuthSystem-" + plugin.getDescription().getVersion())
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                plugin.getLogger().log(Level.WARNING,"[UpdateChecker] Modrinth API returned status: " + response.statusCode());
                return;
            }

            JSONArray versions = new JSONArray(response.body());
            if (versions.isEmpty()) return;

            JSONObject latest = versions.getJSONObject(0);
            String remoteVersion = latest.getString("version_number");
            String currentVersion = plugin.getDescription().getVersion();

            if (isNewer(remoteVersion, currentVersion)) {
                this.latestVersion = remoteVersion;
                this.updateAvailable = true;
                plugin.getLogger().info("[UpdateChecker] New version available: "
                        + remoteVersion + " (current: " + currentVersion + ")");
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "[UpdateChecker] Failed to check for updates: " + e.getMessage());
        }
    }

    public void notify(Player player) {
        if (!updateAvailable || latestVersion == null) return;
        plugin.getLangLoader().send(player, LangKey.UPDATE_AVAILABLE,
                Placeholder.parsed("latest_version", latestVersion));
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    private boolean isNewer(String latest, String current) {
        String[] latestParts = latest.split("\\.");
        String[] currentParts = current.split("\\.");
        int length = Math.max(latestParts.length, currentParts.length);

        for (int i = 0; i < length; i++) {
            int l = i < latestParts.length ? parsePart(latestParts[i]) : 0;
            int c = i < currentParts.length ? parsePart(currentParts[i]) : 0;
            if (l > c) return true;
            if (l < c) return false;
        }
        return false;
    }

    private int parsePart(String part) {
        try {
            return Integer.parseInt(part.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
