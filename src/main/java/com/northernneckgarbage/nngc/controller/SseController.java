package com.northernneckgarbage.nngc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(1800000L);
        emitters.add(emitter);

        emitter.onTimeout(() -> {
            try {
                emitter.send(SseEmitter.event().name("timeout").data("Connection timed out, still alive"));
            } catch (IOException e) {
                log.error("Error handling timeout: ", e);
            }
        });

        emitter.onCompletion(() -> emitters.remove(emitter));

        emitter.onError((Throwable throwable) -> {
            emitters.remove(emitter);
            log.info("Error: " + throwable.getMessage());
        });
        return emitter;
    }

    public void sendEventToClients(Object eventData) {
        Iterator<SseEmitter> iterator = emitters.iterator();
        while (iterator.hasNext()) {
            SseEmitter emitter = iterator.next();
            try {
                emitter.send(eventData);
            } catch (IllegalStateException e) {
                iterator.remove();
                log.warn("Emitter has already completed. Removing emitter from list.");
            } catch (IOException e) {
                iterator.remove();
                log.error("Error sending event to client: ", e);
            }
        }
    }
}