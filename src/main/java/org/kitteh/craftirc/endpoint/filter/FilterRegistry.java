package org.kitteh.craftirc.endpoint.filter;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.filter.defaults.AntiHighlight;
import org.kitteh.craftirc.endpoint.filter.defaults.BukkitPermissionFilter;
import org.kitteh.craftirc.endpoint.filter.defaults.DataMapper;
import org.kitteh.craftirc.endpoint.filter.defaults.RegexFilter;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.loadable.LoadableTypeManager;

import java.util.List;
import java.util.Map;

/**
 * Handles Filters.
 */
public final class FilterRegistry extends LoadableTypeManager<Filter> {
    enum Target {
        Endpoint
    }

    public FilterRegistry(CraftIRC plugin) {
        super(plugin, Filter.class);
        this.registerType(AntiHighlight.class);
        this.registerType(BukkitPermissionFilter.class);
        this.registerType(DataMapper.class);
        this.registerType(RegexFilter.class);
    }

    public void loadList(List<?> list) {
        throw new UnsupportedOperationException("Must provide Endpoint when loading filters!");
    }

    public void loadList(List<?> list, Endpoint endpoint) {
        for (final Object listElement : list) {
            final Map<Object, Object> data;
            if ((data = MapGetter.castToMap(listElement)) == null) {
                continue;
            }
            data.put(Target.Endpoint, endpoint);
        }
        super.loadList(list);
    }

    @Override
    protected void processCompleted(Filter loaded) {

    }

    @Override
    protected void processFailedLoad(Exception exception, Map<?, ?> data) {
        // TODO log
    }

    @Override
    protected void processInvalid(String reason, Map<?, ?> data) {
        // TODO log
    }
}