package com.rrkim.ipcamserver.module.auth.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@ToString
@Getter
public class CameraIdentity {

    String deviceId;

    String credential;
}
