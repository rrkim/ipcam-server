package com.rrkim.ipcamserver.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RSAKeyPair {

    private String publicKey;

    private String privateKey;

}