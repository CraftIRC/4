package org.kitteh.craftirc.endpoint.filter;

import org.kitteh.craftirc.endpoint.EndpointMessage;

public interface Filter {
    // TODO handle outgoing
    void processIncomingMessage(EndpointMessage message);
}