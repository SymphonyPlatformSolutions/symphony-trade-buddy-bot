package com.symphony.platformsolutions.tradebuddy.bot;

import listeners.RoomListener;
import model.InboundMessage;
import model.Stream;
import model.events.*;
import org.springframework.stereotype.Service;

@Service
public class RoomListenerImpl implements RoomListener {
    private TradeBuddyController controller;

    public RoomListenerImpl(TradeBuddyController controller) {
        this.controller = controller;
    }

    public void onRoomMessage(InboundMessage inMsg) {
        controller.handleIncoming(inMsg);
    }

    public void onRoomCreated(RoomCreated roomCreated) {}
    public void onRoomDeactivated(RoomDeactivated roomDeactivated) {}
    public void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {}
    public void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner) {}
    public void onRoomReactivated(Stream stream) {}
    public void onRoomUpdated(RoomUpdated roomUpdated) {}
    public void onUserJoinedRoom(UserJoinedRoom userJoinedRoom) {}
    public void onUserLeftRoom(UserLeftRoom userLeftRoom) {}
}
