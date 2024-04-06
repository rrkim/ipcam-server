package com.rrkim.ipcamserver.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Getter
public class RSAKeyPair {

    private String publicKey;

    private String privateKey;
}