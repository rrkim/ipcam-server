package com.rrkim.ipcamserver.core.auth.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@ToString
@Getter
public class CameraIdentity {

    String deviceId;

    String credential;
}
