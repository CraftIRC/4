package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.exceptions.CraftIRCInvalidConfigException;
import org.kitteh.craftirc.util.MapGetter;
import org.kitteh.craftirc.util.loadable.Loadable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Maps data to a message.
 * <p/>
 * TODO get a better name
 */
@Loadable.Type(name = "datamapper")
public class DataMapper extends Filter {
    private static final Pattern PERCENT_VARIABLE = Pattern.compile("%([^ \\n]+)%");
    private String format;
    private String message;
    private List<String> variables;

    public String getMessageFormat() {
        return this.message;
    }

    @Override
    public void processMessage(TargetedMessage message) {
        Object[] vars = new Object[this.variables.size()];
        for (int i = 0; i < vars.length; i++) {
            vars[i] = message.getCustomData().get(this.variables.get(i)).toString();
        }
        message.setCustomMessage(String.format(this.format, vars));
    }

    @Override
    protected void load(Map<Object, Object> data) throws CraftIRCInvalidConfigException {
        if ((this.message = MapGetter.getString(data, "message")) == null) {
            throw new CraftIRCInvalidConfigException("Message required!");
        }

        Matcher matcher = PERCENT_VARIABLE.matcher(this.message);
        this.variables = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        int last = 0;
        while (matcher.find()) {
            builder.append(this.message.substring(last, matcher.start())).append("%s");
            this.variables.add(matcher.group(1));
            last = matcher.end();
        }
        builder.append(message.substring(last, message.length()));
        this.format = builder.toString();
    }
}