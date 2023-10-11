package com.northernneckgarbage.nngc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/sse")
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onCompletion(() -> emitters.remove(emitter));

        emitter.onError((Throwable throwable) -> {
            emitters.remove(emitter);
           log.info("Error: " + throwable.getMessage());
        });
        return emitter;
    }

    public void sendEventToClients(Object eventData) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(eventData);
            } catch (IOException e) {
                // Handle the exception, for example by removing the emitter from the list
                emitters.remove(emitter);
            }
        }
    }
}
