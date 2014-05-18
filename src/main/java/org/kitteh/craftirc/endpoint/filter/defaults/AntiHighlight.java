package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.Map;

/**
 * Anti highlight aww yes.
 */
@Loadable.Type(name = "antihighlight")
public class AntiHighlight extends Filter {
    private String splitter;
    private String variable;

    @Override
    protected void load(Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        if ((this.splitter = MapGetter.getString(data, "splitter")) == null || (this.variable = MapGetter.getString(data, "variable")) == null) {
            throw new CraftIRCInvalidConfigException("Invalid AntiHighlight config. Requires 'splitter' and 'variable' defined");
        }
    }

    @Override
    public void processMessage(TargetedMessage message) {
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