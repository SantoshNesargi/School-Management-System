package Students.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * In-process pub-sub bus used by the SSE endpoint.
 * <p>
 * One singleton per JVM. Subscribers register a {@link Consumer} for a topic
 * string; publishers push a payload (any object). The bus is thread-safe and
 * uses {@link CopyOnWriteArrayList} so subscribers can be added/removed while
 * a publish is in flight without ConcurrentModificationException.
 */
public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    public static EventBus get() {
        return INSTANCE;
    }

    private final Map<String, CopyOnWriteArrayList<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    private EventBus() {
    }

    /**
     * Publish a payload to every subscriber of the given topic.
     * Subscriber exceptions are swallowed so a broken client cannot take down
     * the publisher; they are logged to stderr.
     */
    public void publish(String topic, Object payload) {
        CopyOnWriteArrayList<Consumer<Object>> list = subscribers.get(topic);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Consumer<Object> sub : list) {
            try {
                sub.accept(payload);
            } catch (Throwable t) {
                System.err.println("[EventBus] subscriber threw on topic '" + topic + "': " + t);
            }
        }
    }

    /**
     * Subscribe to a topic. The returned {@link AutoCloseable} removes the
     * subscription when closed.
     */
    public AutoCloseable subscribe(String topic, Consumer<Object> handler) {
        subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(handler);
        return () -> {
            CopyOnWriteArrayList<Consumer<Object>> list = subscribers.get(topic);
            if (list != null) {
                list.remove(handler);
            }
        };
    }
}
