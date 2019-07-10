package com.symphony.platformsolutions.tradebuddy.bot;

import listeners.IMListener;
import model.InboundMessage;
import model.Stream;
import org.springframework.stereotype.Service;

@Service
public class IMListenerImpl implements IMListener {
    private TradeBuddyController controller;

    public IMListenerImpl(TradeBuddyController controller) {
        this.controller = controller;
    }

    public void onIMMessage(InboundMessage inMsg) {
        controller.handleIncoming(inMsg);
    }

    public void onIMCreated(Stream stream) {}
}
