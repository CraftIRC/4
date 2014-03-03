package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;

/**
 * Anti highlight aww yes.
 */
public class AntiHighlight implements Filter {
    private String splitter;
    private String variable;

    public AntiHighlight(String splitter, String variable) {
        this.splitter = splitter;
        this.variable = variable;
    }

    @Override
    public void processIncomingMessage(TargetedMessage message) {
        if (message.getCustomData().containsKey(this.variable)) {
            String oldValue = message.getCustomData().get(this.variable).toString();
            if (oldValue.length() > 1) {
                StringBuilder builder = new StringBuilder();
                for (char c : oldValue.toCharArray()) {
                    builder.append(c).append(this.splitter);
                }
                builder.setLength(builder.length() - this.splitter.length());
                String newValue = builder.toString();
                message.getCustomData().put(this.variable, newValue);
                message.setCustomMessage(message.getCustomMessage().replace(oldValue, newValue));
            }
        }
    }
}