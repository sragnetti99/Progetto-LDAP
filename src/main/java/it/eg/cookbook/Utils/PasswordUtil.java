package it.eg.cookbook.Utils;

import jcifs.util.Hexdump;
import jcifs.util.MD4;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import org.bouncycastle.util.encoders.Hex;
import java.util.Base64;
import java.io.UnsupportedEncodingException;
import jcifs.util.MD4;
import jcifs.util.Hexdump;

public final class PasswordUtil {

    private static final int SALT_LENGTH = 4;

    public static String generateSSHA(byte[] password) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password);
        crypt.update(salt);
        byte[] hash = crypt.digest();

        byte[] hashPlusSalt = new byte[hash.length + salt.length];
        System.arraycopy(hash, 0, hashPlusSalt, 0, hash.length);
        System.arraycopy(salt, 0, hashPlusSalt, hash.length, salt.length);

        return new StringBuilder().append("{SSHA}")
                .append(Base64.getEncoder().encodeToString(hashPlusSalt))
                .toString();
    }


    public static String hashNTPassword(String password) throws UnsupportedEncodingException {
        MD4 md4 = new MD4();
        byte[] bpass = password.getBytes("UnicodeLittleUnmarked");
        md4.engineUpdate(bpass, 0, bpass.length);
        byte[] hashbytes = md4.engineDigest();
        return Hexdump.toHexString(hashbytes, 0, hashbytes.length * 2);
    }

    public static String hashLMPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(password.getBytes(StandardCharsets.UTF_8));
        String md5Password = jcifs.util.Base64.encode(digest.digest());
        return "{MD5}" + md5Password;

/*
        byte[] r = password.getBytes(StandardCharsets.UTF_16LE);
        MD4Digest d = new MD4Digest();
        d.update (r, 0, r.length);
        byte[] o = new byte[d.getDigestSize()];
        d.doFinal (o, 0);
        return new String(Hex.encode(o));

       Security.addProvider(new BouncyCastleProvider());

        MessageDigest crypt = MessageDigest.getInstance("MD4");
        crypt.update(password.getBytes(StandardCharsets.UTF_16LE));
        return Hex.toHexString(crypt.digest());
        MD4 md4 = new MD4();
        byte[] bpass;
        bpass = password.getBytes(StandardCharsets.UTF_16LE);
        md4.engineUpdate(bpass, 0, bpass.length);
        byte[] hashBytes;
        hashBytes = md4.engineDigest();
        return Hexdump.toHexString(hashBytes, 0, hashBytes.length * 2);
        byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
        MD4 md4 = new MD4();
        md4.update(unicodePassword,0,unicodePassword.length);
        return  Hexdump.toHexString(md4.digest(), 0, md4.digest().length * 2);*/
    }

}
