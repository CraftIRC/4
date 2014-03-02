package org.kitteh.craftirc.endpoint.filter;

import org.kitteh.craftirc.message.EndpointMessage;

public interface Filter {
    // TODO handle outgoing
    void processIncomingMessage(EndpointMessage message);
}