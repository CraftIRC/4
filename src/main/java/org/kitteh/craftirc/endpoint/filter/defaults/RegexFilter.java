package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.Map;

/**
 * Filter of information into the displayed message, via regular expression.
 */
@Loadable.Type(name = "regex")
public class RegexFilter extends Filter {
    @Override
    protected void load(Map<?, ?> data) throws CraftIRCInvalidConfigException {
        // TODO get dx to do this
    }

    @Override
    public void processIncomingMessage(TargetedMessage message) {
        // TODO get dx to do this
    }
}