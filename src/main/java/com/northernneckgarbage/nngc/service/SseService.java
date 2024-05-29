package com.northernneckgarbage.nngc.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
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


    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }
}
