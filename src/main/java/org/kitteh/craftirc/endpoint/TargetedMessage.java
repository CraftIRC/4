package org.kitteh.craftirc.endpoint;

import org.kitteh.craftirc.util.WrappedMap;

/**
 * Defines a message as received by a particular
 * {@link org.kitteh.craftirc.endpoint.Endpoint}.
 * <p/>
 */
public final class TargetedMessage {
    private final Message originatingMessage;
    private final Endpoint target;
    private String customMessage;
    private final WrappedMap<String, Object> customData;

    /**
     * Creates a message targetted at an
     * {@link org.kitteh.craftirc.endpoint.Endpoint}.
     *
     * @param target message destination
     * @param originatingMessage the message being sent
     */
    public TargetedMessage(Endpoint target, Message originatingMessage) {
        this.target = target;
        this.originatingMessage = originatingMessage;
        this.customData = new WrappedMap<String, Object>(originatingMessage.getData());
        this.customMessage = originatingMessage.getDefaultMessage();
    }

    /**
     * Gets any custom data associated with this message. The data can be
     * modified specifically for this TargetedMessage.
     *
     * @return the custom data associated with the message
     */
    public WrappedMap<String, Object> getCustomData() {
        return this.customData;
    }

    /**
     * Gets the current message to be outputted to the target Endpoint. By
     * default, this message is
     * {@link Message#getDefaultMessage()}.
     *
     * @return the message to be displayed to the Endpoint
     */
    public String getCustomMessage() {
        return this.customMessage;
    }

    /**
     * Sets the message to be output to the target Endpoint.
     *
     * @param message the new message
     * @return the previously set message
     */
    public String setCustomMessage(String message) {
        String oldMessage = this.customMessage;
        this.customMessage = message;
        return oldMessage;
    }

    /**
     * Gets the target of this message.
     *
     * @return the Endpoint at which this message is targetted
     */
    public Endpoint getTarget() {
        return this.target;
    }

    /**
     * Gets the message sent by the source.
     *
     * @return the originating message
     */
    public Message getOriginatingMessage() {
        return this.originatingMessage;
    }
}