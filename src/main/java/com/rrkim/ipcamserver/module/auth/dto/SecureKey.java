package com.rrkim.ipcamserver.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class SecureKey {

    String secureKey;

    LocalDateTime createDate;
}
