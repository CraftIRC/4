package org.kitteh.craftirc.endpoint.filter.defaults;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.junit.Assert;
import org.junit.Test;
import org.kitteh.craftirc.endpoint.Message;
import org.kitteh.craftirc.endpoint.TargetedMessage;
import org.kitteh.craftirc.endpoint.filter.FilterManager;
import org.kitteh.craftirc.util.MapBuilder;
import org.kitteh.craftirc.util.PointyEnd;

import java.util.LinkedList;
import java.util.List;

public class AntiHighlightTest {
    private static final String VAR_KEY = "test";

    @Test
    public void meow() {
        try {
            FilterManager registry = new FilterManager(null, SimpleConfigurationNode.root());
            PointyEnd point = new PointyEnd();
            List<ConfigurationNode> list = new LinkedList<>();
            ConfigurationNode node = SimpleConfigurationNode.root();
            node.getNode("splitter").setValue("`");
            node.getNode("variable").setValue(VAR_KEY);
            node.getNode("type").setValue("antihighlight");
            list.add(node);
            registry.loadList(list, point.getLoader());
            TargetedMessage message = new TargetedMessage(null, new Message(null, "Meow", new MapBuilder<String, Object>().put(VAR_KEY, "test").build()));
            point.message(message);
            Assert.assertEquals("t`e`s`t", message.getCustomData().get(VAR_KEY));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}