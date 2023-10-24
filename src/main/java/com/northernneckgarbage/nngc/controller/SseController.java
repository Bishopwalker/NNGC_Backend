package com.northernneckgarbage.nngc.controller;

import com.northernneckgarbage.nngc.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SseService sseService;

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(1800000L);
        sseService.addEmitter(emitter);

        emitter.onTimeout(() -> {
            try {
                emitter.send(SseEmitter.event().name("timeout").data("Connection timed out, still alive"));
            } catch (IllegalStateException ise) {
                log.warn("Emitter has already completed: ", ise);
            } catch (IOException e) {
                log.error("Error handling timeout: ", e);
            }
        });

        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onError((Throwable throwable) -> removeEmitter(emitter));

        return emitter;
    }

    private void removeEmitter(SseEmitter emitter) {
        sseService.removeEmitter(emitter);
    }

    public void sendEventToClients(Object eventData) {
        sseService.sendEventToClients(eventData);
    }
}
