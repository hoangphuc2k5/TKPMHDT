package TKPMHDT.Service.donhang;

import TKPMHDT.Entity.donhang.DonHang;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class DonHangRealtimeService {

    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID donHangId) {
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(donHangId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeSubscriber(donHangId, emitter));
        emitter.onTimeout(() -> removeSubscriber(donHangId, emitter));
        emitter.onError(ex -> removeSubscriber(donHangId, emitter));
        return emitter;
    }

    public void publishOrderUpdated(DonHang donHang) {
        if (donHang == null || donHang.getId() == null) {
            return;
        }
        List<SseEmitter> emitters = subscribers.get(donHang.getId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("order-status")
                        .id(donHang.getId().toString())
                        .data(donHang));
            } catch (IOException e) {
                removeSubscriber(donHang.getId(), emitter);
            }
        }
    }

    private void removeSubscriber(UUID donHangId, SseEmitter emitter) {
        List<SseEmitter> emitters = subscribers.get(donHangId);
        if (emitters == null) {
            return;
        }
        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            subscribers.remove(donHangId);
        }
    }
}
