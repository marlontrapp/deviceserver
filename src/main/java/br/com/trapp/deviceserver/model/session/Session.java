package br.com.trapp.deviceserver.model.session;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;

import br.com.trapp.deviceserver.api.DH;
import br.com.trapp.deviceserver.api.DeviceException;
import br.com.trapp.deviceserver.api.DeviceMessage;
import br.com.trapp.deviceserver.api.DeviceModule;

public class Session {

    private Integer id;
    private DH dh;
    private DeviceModule module;
    private PaddedBufferedBlockCipher cipher;
    private byte[] keyBytes;
    private String certificate;
    private long lastUse = System.currentTimeMillis();

    public Session(Integer id, DH dh, DeviceModule module, String certificate) {
	this.id = id;
	this.dh = dh;
	this.module = module;
	this.certificate = certificate;
    }

    public DH getDh() {
	return dh;
    }

    public void setDh(DH dh) {
	this.dh = dh;
    }

    public DeviceModule getModule() {
	return module;
    }

    public void setModule(DeviceModule module) {
	this.module = module;
    }

    public String getCertificate() {
	return certificate;
    }

    public void setCertificate(String certificate) {
	this.certificate = certificate;
    }

    public void init() throws NoSuchAlgorithmException, NoSuchProviderException {
	MessageDigest digest = MessageDigest.getInstance("SHA256", "BC");
	byte[] keyByteArray = dh.getSharedSecret().toString().getBytes();
	digest.update(keyByteArray);
	this.keyBytes = digest.digest();

	// setup AES cipher in CBC mode with PKCS7 padding
	BlockCipherPadding padding = new PKCS7Padding();
	this.cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), padding);
    }

    /**
     * Encrypt the given message
     * 
     * @param message
     *            the message to be encrypted
     * 
     * @return the message encrypted
     * 
     * @throws InvalidCipherTextException
     */
    public DeviceMessage encrypt(DeviceMessage message) throws InvalidCipherTextException {

	SecureRandom secureRandom = new SecureRandom();
	byte[] iv = new byte[16];
	secureRandom.nextBytes(iv);
	message.setIV(Base64.toBase64String(iv));
	return processCipher(message, true);
    }

    /**
     * Decrypt the given message
     * 
     * @param message
     *            the message to be decrypted
     * 
     * @return the message decrypted
     * 
     * @throws InvalidCipherTextException
     */
    public DeviceMessage decrypt(DeviceMessage message) throws InvalidCipherTextException {
	return this.processCipher(message, false);
    }

    /**
     * Process the message, encrypting or decrypting it depends on crypt
     * parameter
     * 
     * @param message
     *            the message to be processed
     * @param crypt
     *            desirable operation, encrypt or decrypt
     * 
     * @return the message encrypted or decrypted
     * 
     * @throws InvalidCipherTextException
     */
    private DeviceMessage processCipher(DeviceMessage message, boolean crypt) throws InvalidCipherTextException {
	cipher.reset();
	KeyParameter keyParam = new KeyParameter(this.keyBytes);
	CipherParameters params = new ParametersWithIV(keyParam, Base64.decode(message.getIV()));
	cipher.init(crypt, params);

	String method = null;
	String msg = null;
	if (crypt) {
	    if (message.getMethod() != null)
		method = Base64.toBase64String(this.process(message.getMethod().getBytes()));
	    if (message.getMessage() != null)
		msg = Base64.toBase64String(this.process(message.getMessage().getBytes()));
	} else {
	    // We have to add .trim() because of the strange behavior of the
	    // string
	    if (message.getMethod() != null)
		method = new String(this.process(Base64.decode(message.getMethod()))).trim();
	    if (message.getMessage() != null)
		msg = new String(this.process(Base64.decode(message.getMessage()))).trim();
	}
	message.setMethod(method);
	message.setMessage(msg);
	return message;
    }

    /**
     * Do the encrypt or decrypt operation depends on the initialization of the
     * cipher
     * 
     * @param data
     *            The data to be processed
     * @return the processed data
     * 
     * @throws InvalidCipherTextException
     */
    private byte[] process(byte[] data) throws InvalidCipherTextException {
	byte[] buf = new byte[cipher.getOutputSize(data.length)];
	int len = cipher.processBytes(data, 0, data.length, buf, 0);
	len += cipher.doFinal(buf, len);
	return buf;
    }

    public DeviceMessage sendMessageToModule(DeviceMessage message) throws DeviceException, InvalidCipherTextException {
	DeviceMessage decryptedMessage = this.decrypt(message);
	DeviceMessage response = module.incomingMessage(decryptedMessage);
	DeviceMessage encryptedResponse = this.encrypt(response);
	lastUse = System.currentTimeMillis();
	return encryptedResponse;
    }

    public Certificate generateCertificate() throws CertificateException {

	ByteArrayInputStream byteArrIStream = new ByteArrayInputStream(certificate.getBytes());
	CertificateFactory cf = CertificateFactory.getInstance("X.509");
	return cf.generateCertificate(byteArrIStream);
    }

    @Override
    public String toString() {
	return module.getName() + " - " + id;
    }

    public long getLastUse() {
	return lastUse;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

}
