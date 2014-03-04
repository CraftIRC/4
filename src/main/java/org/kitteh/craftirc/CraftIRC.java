package org.kitteh.craftirc;

import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.craftirc.endpoint.EndpointManager;
import org.kitteh.craftirc.exceptions.CraftIRCFoundTabsException;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class CraftIRC extends JavaPlugin {
    EndpointManager endpointManager;

    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("I do nothing!");
        CraftIRCInvalidConfigException exception = null;
        List<?> endpoints = null;
        List<?> links = null;

        try {
            File configFile = new File(this.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                this.getLogger().info("No config.yml found, creating a default configuration.");
                this.saveDefaultConfig();
            }

            BufferedReader reader = new BufferedReader(new FileReader(configFile));

            StringBuilder builder = new StringBuilder();
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("\t")) {
                    throw new CraftIRCFoundTabsException(lineNumber, line);
                }
                builder.append(line).append('\n');
                lineNumber++;
            }

            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);

            String configString = builder.toString();
            Object yamlBase = yaml.load(configString);

            if (!(yamlBase instanceof Map)) {
                throw new CraftIRCInvalidConfigException("Config doesn't even start with mappings. Would advise starting from scratch.");
            }

            Map<?, ?> config = (Map<?, ?>) yamlBase;
            Object endpointsObject = config.get("endpoints");
            if (!(endpointsObject instanceof List)) {
                throw new CraftIRCInvalidConfigException("No endpoints defined! Would advise starting from scratch.");
            }
            endpoints = (List<?>) endpointsObject;

            Object linksObject = config.get("links");
            if (!(linksObject instanceof List)) {
                throw new CraftIRCInvalidConfigException("No links defined! How can your endpoints be useful?");
            }
            links = (List<?>) linksObject;
        } catch (IOException e) {
            exception = new CraftIRCInvalidConfigException(e);
        } catch (CraftIRCInvalidConfigException e) {
            exception = e;
        }

        if (exception != null) {
            this.getLogger().log(Level.SEVERE, "Could not start CraftIRC!", exception);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.endpointManager = new EndpointManager(this, endpoints, links);
    }
}