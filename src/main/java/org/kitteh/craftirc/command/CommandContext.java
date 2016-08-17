/*
 * * Copyright (C) 2014-2016 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kitteh.craftirc.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Information about an executed command.
 */
public class CommandContext<SenderType> {
    public interface Sender<SenderType> {
        String getName();

        SenderType getSender();

        void sendReply(String message);
    }

    private final List<String> args;
    private final String command;
    private final Sender<SenderType> sender;

    public CommandContext(String command, Sender<SenderType> sender) {
        this.command = command;
        this.args = Collections.unmodifiableList(Arrays.asList(command.split(" ")));
        this.sender = sender;
    }

    /**
     * Gets the args. Duh.
     *
     * @return args
     */
    public List<String> getArgs() {
        return this.args;
    }

    /**
     * Gets the entire command, not split.
     *
     * @return executed command
     * @see #getArgs() for the command split by spaces
     */
    public String getCompleteCommand() {
        return this.command;
    }

    public void reply(String message) {
        this.sender.sendReply(message);
    }
}