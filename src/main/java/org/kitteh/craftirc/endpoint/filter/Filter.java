package org.kitteh.craftirc.endpoint.filter;

import org.kitteh.craftirc.endpoint.TargetedMessage;

public interface Filter {
    // TODO handle outgoing
    void processIncomingMessage(TargetedMessage message);
}