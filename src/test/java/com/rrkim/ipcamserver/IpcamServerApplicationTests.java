package com.rrkim.ipcamserver;

import com.rrkim.ipcamserver.core.utility.AesUtility;
import com.rrkim.ipcamserver.core.utility.FileUtility;
import com.rrkim.ipcamserver.core.utility.JsonUtility;
import com.rrkim.ipcamserver.core.utility.RsaUtility;
import com.rrkim.ipcamserver.module.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.module.auth.dto.SecureKey;
import com.rrkim.ipcamserver.module.auth.service.IdentificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

@SpringBootTest
public class IpcamServerApplicationTests {
    @Autowired
    IdentificationService identificationService;

    final String TARGET_STRING = "Hello World!";

    @Test
    void AESTest() throws Exception {
        String key = AesUtility.getRandomAesKey();

        String encrypted = AesUtility.encodeAesCbc(TARGET_STRING.getBytes(), key);
        String decrypted = new String(AesUtility.decodeAesCbc(encrypted, key));

        assert TARGET_STRING.equals(decrypted);
    }

    @Test
    void RSATest() throws Exception {
        RSAKeyPair keyPair = RsaUtility.createKeyPair();

        String encrypted = RsaUtility.encryptData(TARGET_STRING, keyPair.getPublicKey());
        String decrypted = RsaUtility.decryptData(encrypted, keyPair.getPrivateKey());

        assert decrypted.equals(TARGET_STRING);
    }

    @Test
    void RSAAndAESTest() throws Exception {
        /*
         * when client received shared key encrypted with public key
         * - decrypt shared key with secret key
         * - encrypt sample data with shared key
         * - decrypt sample data with shared key
         */

        // make sure camera identity(RSA key pair) is present
        identificationService.createCameraIdentity();

        // generate RSA-encrypted secure key
        identificationService.createSymmetricKey();

        // SERVER: server-owned symmetric key
        String serverSymmetricKey = identificationService.getSymmetricKey();

        // CLIENT: read TCI file and get private key
        String tciString = FileUtility.readFileByDataStream("keys/3859b5ab-e9e5-4630-a61c-e1814c323e1a.tci");
        String tciConfiguration = new String(Base64.getDecoder().decode(tciString));
        String privateKeyString = JsonUtility.getProperty(tciConfiguration, "credential");

        assert privateKeyString != null;

        // CLIENT: get secure key and decrypt with private key
        SecureKey secureKey = identificationService.getSecureKey();

        String clientSymmtricKey = RsaUtility.decryptData(secureKey.getSecureKey(), privateKeyString);

        assert serverSymmetricKey.equals(clientSymmtricKey);
    }
}
