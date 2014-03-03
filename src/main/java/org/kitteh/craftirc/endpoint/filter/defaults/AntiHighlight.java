package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;

/**
 * Anti highlight aww yes.
 */
public class AntiHighlight implements Filter {
    private String replacement;
    private String variable;

    public AntiHighlight(String replacement, String variable) {
        this.replacement = replacement;
        this.variable = variable;
    }

    @Override
    public void processIncomingMessage(TargetedMessage message) {
        if (message.getCustomData().containsKey(this.variable)) {
            String oldValue = message.getCustomData().get(this.variable).toString();
            if (oldValue.length() > 1) {
                String newValue = oldValue.charAt(0) + this.replacement + oldValue.substring(1);
                message.getCustomData().put(this.variable, newValue);
                message.setCustomMessage(message.getCustomMessage().replace(oldValue, newValue));
            }
        }
    }
}