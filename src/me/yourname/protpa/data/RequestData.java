package me.yourname.protpa.data;

import java.util.UUID;

public class RequestData {
    private final UUID requesterUUID;
    private final RequestType type;
    private final long timestamp;

    public enum RequestType {
        TPA,
        TPAHERE
    }

    public RequestData(UUID requesterUUID, RequestType type) {
        this.requesterUUID = requesterUUID;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getRequesterUUID() {
        return requesterUUID;
    }

    public RequestType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired(long timeoutMs) {
        return System.currentTimeMillis() - timestamp > timeoutMs;
    }
}
