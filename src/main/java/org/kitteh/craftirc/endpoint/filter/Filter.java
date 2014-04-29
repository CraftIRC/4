package org.kitteh.craftirc.endpoint.filter;

import org.kitteh.craftirc.CraftIRC;
import org.kitteh.craftirc.endpoint.Endpoint;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.Map;

public abstract class Filter extends Loadable {
    private Endpoint endpoint;

    // TODO handle outgoing
    public abstract void processIncomingMessage(TargetedMessage message);

    @Override
    protected void load(CraftIRC plugin, Map<?, ?> data) throws CraftIRCInvalidConfigException {
        if (data.containsKey(FilterRegistry.Target.Endpoint)) {
            this.endpoint = (Endpoint) data.get(FilterRegistry.Target.Endpoint);
        }
        this.load(data);
    }

    protected abstract void load(Map<?,?> data) throws CraftIRCInvalidConfigException;
}