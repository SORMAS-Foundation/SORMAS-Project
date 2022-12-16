package de.symeda.sormas.api.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enum describing the communication channel for notifications.")
public enum NotificationProtocol {
    EMAIL,
    SMS;
}
