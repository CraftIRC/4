package org.kitteh.craftirc.endpoint.filter.defaults;

import org.junit.Assert;
import org.junit.Test;
import org.kitteh.craftirc.endpoint.Message;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.Filter;
import org.kitteh.craftirc.util.MapBuilder;

public class AntiHighlightTest {
    private static final String VAR_KEY = "test";

    @Test
    public void meow() {
        try {
            Filter filter = new AntiHighlight();
            FilterUtil.loadFilter(filter, new MapBuilder<>().add("splitter", "`").add("variable", VAR_KEY).map());
            TargetedMessage message = new TargetedMessage(null, new Message(null, "Meow", new MapBuilder<String, Object>().add(VAR_KEY, "test").map()));
            filter.processMessage(message);
            Assert.assertEquals("t`e`s`t", message.getCustomData().get(VAR_KEY));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}