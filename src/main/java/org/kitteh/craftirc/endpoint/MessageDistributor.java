package org.kitteh.craftirc.endpoint;

import org.kitteh.craftirc.CraftIRC;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Distributes messages.
 * <p/>
 * YOU GET A MESSAGE, AND YOU GET A MESSAGE! EVERYBODY GETS A MESSAGE!
 */
final class MessageDistributor extends Thread {
    private class SendMessage implements Callable<Object> {
        private Message message;
        private Endpoint target;

        private SendMessage(Message message, Endpoint target) {
            this.message = message;
            this.target = target;
        }

        @Override
        public Object call() throws Exception {
            this.target.receiveMessage(message);
            return null;
        }
    }

    private final EndpointManager endpointManager;
    private ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
    private final CraftIRC plugin;

    MessageDistributor(EndpointManager manager, CraftIRC plugin) {
        this.endpointManager = manager;
        this.plugin = plugin;
    }

    void addMessage(Message message) {
        this.messages.add(message);
    }

    @Override
    public void run() {
        long timeTrack;
        while (!this.isInterrupted()) {
            timeTrack = System.currentTimeMillis();
            Message message = this.messages.poll();
            if (message != null) {
                for (Endpoint target : this.endpointManager.getDestinations(message.getSource().getName())) {
                    if (target.getClass().getAnnotation(EndpointType.class).sync()) {
                        this.plugin.getServer().getScheduler().callSyncMethod(this.plugin, new SendMessage(message, target));
                    } else {
                        target.receiveMessage(message);
                    }
                }
            }
            timeTrack = System.currentTimeMillis() - timeTrack;
            if (timeTrack < 50 && timeTrack > 0) {
                try {
                    Thread.sleep(50 - timeTrack);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}