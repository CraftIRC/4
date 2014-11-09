/*
 * * Copyright (C) 2014 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.craftirc;

import org.kitteh.craftirc.endpoint.EndpointManager;
import org.kitteh.craftirc.endpoint.filter.FilterManager;
import org.kitteh.craftirc.exceptions.CraftIRCFoundTabsException;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.exceptions.CraftIRCWillLeakTearsException;
import org.kitteh.craftirc.irc.BotManager;
import org.kitteh.craftirc.util.CraftIRCLogger;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.shutdownable.Shutdownable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CraftIRC {
    public interface Implementation {
        /**
         * Gets the logger CraftIRC can use.
         *
         * @return a logger
         */
        Logger getLogger();

        /**
         * Gets the data folder storing the config.
         *
         * @return the folder holding the config
         */
        File getDataFolder();

        /**
         * This method should trigger a shutdown of the implementation. Be
         * sure to call {@link CraftIRC#shutdown()} when done, to kill
         * threads still running.
         */
        void shutdown();
    }

    private static Logger logger;

    public static Logger log() {
        if (CraftIRC.logger == null) {
            throw new CraftIRCWillLeakTearsException();
        }
        return CraftIRC.logger;
    }

    private BotManager botManager;
    private EndpointManager endpointManager;
    private FilterManager filterManager;
    private final Set<Shutdownable> shutdownables = new CopyOnWriteArraySet<>();

    public BotManager getBotManager() {
        return this.botManager;
    }

    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    public FilterManager getFilterManager() {
        return this.filterManager;
    }

    /**
     * Starts tracking a feature which can be shut down.
     *
     * @param shutdownable feature to track
     */
    public void trackShutdownable(Shutdownable shutdownable) {
        this.shutdownables.add(shutdownable);
    }

    public CraftIRC(Implementation implementation) {
        CraftIRC.logger = new CraftIRCLogger(implementation.getLogger());

        CraftIRCInvalidConfigException exception = null;

        try {
            File configFile = new File(implementation.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                log().info("No config.yml found, creating a default configuration.");
                this.saveDefaultConfig(implementation.getDataFolder());
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

            Map<Object, Object> config = MapGetter.castToMap(yamlBase);
            if (config == null) {
                throw new CraftIRCInvalidConfigException("Config doesn't even start with mappings. Would advise starting from scratch.");
            }

            Map<Object, Object> repeatableFilterMap = MapGetter.getMap(config, "repeatable-filters");

            List<Object> bots = MapGetter.getList(config, "bots");
            if (bots == null) {
                throw new CraftIRCInvalidConfigException("No bots defined!");
            }

            List<Object> endpoints = MapGetter.getList(config, "endpoints");
            if (endpoints == null) {
                throw new CraftIRCInvalidConfigException("No endpoints defined! Would advise starting from scratch.");
            }

            List<Object> links = MapGetter.getList(config, "links");
            if (links == null) {
                throw new CraftIRCInvalidConfigException("No links defined! How can your endpoints be useful?");
            }

            this.filterManager = new FilterManager(this, repeatableFilterMap);
            this.botManager = new BotManager(this, bots);
            this.endpointManager = new EndpointManager(this, endpoints, links);
        } catch (IOException e) {
            exception = new CraftIRCInvalidConfigException(e);
        } catch (CraftIRCInvalidConfigException e) {
            exception = e;
        }

        if (exception != null) {
            log().log(Level.SEVERE, "Could not start CraftIRC!", exception);
            implementation.shutdown();
        }
    }

    /**
     * Shuts down any running threads. Call this method only to clean up.
     */
    public void shutdown() {
        for (Shutdownable shutdownable : this.shutdownables) {
            shutdownable.shutdown();
        }
        // And lastly...
        CraftIRC.logger = null;
    }

    private void saveDefaultConfig(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try {
            URL url = this.getClass().getClassLoader().getResource("config.yml");
            if (url == null) {
                log().warning("Could not find a default config to copy!");
                return;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            InputStream input = connection.getInputStream();

            File outFile = new File(dataFolder, "config.yml");
            OutputStream output = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = input.read(buffer)) > 0) {
                output.write(buffer, 0, lengthRead);
            }

            output.close();
            input.close();
        } catch (IOException ex) {
            log().log(Level.SEVERE, "Exception while saving default config", ex);
        }
    }
}