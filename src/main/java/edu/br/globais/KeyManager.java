package edu.br.globais;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

import static edu.br.globais.security.HMAC.hex2Byte;

public class KeyManager {
    private static final Map<String, SecretKey> chavesAES = new HashMap<>();
    private static final Map<String, String> chavesHMAC = new HashMap<>();

    public KeyManager() {
        byte[] aes1 = hex2Byte("42e51a101ed22a5e9560002060e7f38083f389c692097da9ce73f39a8b593c90");

        chavesAES.put("client1", new SecretKeySpec(aes1, 0, aes1.length, "AES"));
        chavesHMAC.put("client1", "641f00d6032ac82902d745c28fd7c570b00067391c6d7b7226cccb9a40695ef0");

        byte[] aes2 = hex2Byte("ee8278dc041b3ecd2ca090d88698e53d6b25d9392d1cf51ad8e56c86d45342c9");

        chavesAES.put("client2", new SecretKeySpec(aes2, 0, aes2.length, "AES"));
        chavesHMAC.put("client2", "2da6e67a02ad431a083db8d4f902f23609dfd62c9593a58917a1fa5503286dbf");

        byte[] aes3 = hex2Byte("a1b16a83e617018c0b4c529e7eaa820afc667c2f89fd3f084c4b5f6ac0c4fd8f");

        chavesAES.put("client3", new SecretKeySpec(aes3, 0, aes3.length, "AES"));
        chavesHMAC.put("client3", "692d07cd4ffc81b9ecb1cf621bc9f4d3468a7284a8b123b4a3b344726b6bf085");

        byte[] aes4 = hex2Byte("8a5c8f4a403487761aedd2316e13e832e278d60c989e2db053f98f1f4449d5a9");

        chavesAES.put("client4", new SecretKeySpec(aes4, 0, aes4.length, "AES"));
        chavesHMAC.put("client4", "6e07c3ee123662290e7f80c891c72c995fd78e2372e7804c64089ded6d07831e");

        byte[] aes5 = hex2Byte("df58e311fad67b5ae8a831c4fe5584dfb2d2e9102f7255de8c30604acaa93b69");

        chavesAES.put("client5", new SecretKeySpec(aes5, 0, aes5.length, "AES"));
        chavesHMAC.put("client5", "a6366e21f073f44e0ba013d47a08b1a190a4cd847361de100f6453f4bda41cc6");

        byte[] aes6 = hex2Byte("9925b51193f8e91402a29f131b44902cc756137506e046e09b43683a2bc0e2e0");

        chavesAES.put("client6", new SecretKeySpec(aes6, 0, aes6.length, "AES"));
        chavesHMAC.put("client6", "72c573312f8b6d61e727a25a975211143e951011d86e161b87d0d0aa8bbb407b");

        byte[] aes7 = hex2Byte("e309a0a1b11da1f370fdc035546adbe8ab8d167966418cc954eb06a652adf5f2");

        chavesAES.put("client7", new SecretKeySpec(aes7, 0, aes7.length, "AES"));
        chavesHMAC.put("client7", "186caceefe48f14ffd3d0e324dcf8e2a7a55f87add6bf2d7ee3142364ce34380");

        byte[] aes8 = hex2Byte("e90332cc58a800609cb8dd83e19e2aa0ccf620370fac6a39dddff1ec11e10c10");

        chavesAES.put("client8", new SecretKeySpec(aes8, 0, aes8.length, "AES"));
        chavesHMAC.put("client8", "6cfec063a43c4701bae5ffcf9b54fa81e215c328d1052cc690fbe3b1c5010982");

        byte[] aes9 = hex2Byte("74511684bdfdbc56d3bff372ee2e8cc4ede859fd030ab702bf3046750b0a7d71");

        chavesAES.put("client9", new SecretKeySpec(aes9, 0, aes9.length, "AES"));
        chavesHMAC.put("client9", "46870f68dc1a680061dde07f3ec67a10b65365f0db0b8dba9dce91921d61b004");

        byte[] aes10 = hex2Byte("a70e6396025cba129757e0d087ce0801f282cab2436746eb9c0f2d8f8268dffc");

        chavesAES.put("registrador", new SecretKeySpec(aes10, 0, aes10.length, "AES"));
        chavesHMAC.put("registrador", "3edbdd2e8de917277a20ab9b548243da5a816329e79be0afc062e33ce659eafe");
    }

    public static SecretKey getKeyAES(String clientName) {
        return chavesAES.get(clientName);
    }

    public static String getKeyHMAC(String clientName) {
        return chavesHMAC.get(clientName);
    }
}
