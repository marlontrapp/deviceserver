package br.com.trapp.deviceserver.model;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.util.encoders.Base64;

import br.com.trapp.deviceserver.api.Authorization;
import br.com.trapp.deviceserver.api.AuthorizationProccess;
import br.com.trapp.deviceserver.api.DH;
import br.com.trapp.deviceserver.api.DeviceMessage;
import br.com.trapp.deviceserver.model.exception.AuthorizationException;
import br.com.trapp.deviceserver.model.exception.ModuleManagerException;
import br.com.trapp.deviceserver.model.module.ModuleManager;
import br.com.trapp.deviceserver.model.session.Session;
import br.com.trapp.deviceserver.utils.PKIXUtils;

public class DeviceBridge {

    public static BigInteger P = new BigInteger("32055214315510634381537827598970291171729459495192300"
	    + "9765348891582450463121486245683681161749700602001883931854838946373114190690669724541171064"
	    + "2981060008420307874372256621870485864401509508975574503012064085876088718515081312663378941"
	    + "6744251610542498968893631451485209041100318598384609705087112758012553170392433014961298894"
	    + "6387085446553802574950457526965794118643934476474981815984786247184827846740861856914516695"
	    + "6346933960205868384029864862039859503281058718307980510442162777904332967499841862480070183"
	    + "2488500009594242667639002071155719251569396723263497716700430440410574382097997040868519633"
	    + "997621281861142699");
    public static BigInteger G = new BigInteger("2");
    public static int SESSION_TIMEOUT = 15;

    public Map<Integer, Session> authorizedSessions;
    public Map<Integer, Session> inAuthorizationSessions;

    private final ScheduledExecutorService scheduler;

    /**
     * Initialize the device bridge with a cleaner thread to clean old sessions
     */
    public DeviceBridge() {
	authorizedSessions = new HashMap<Integer, Session>();
	inAuthorizationSessions = new HashMap<Integer, Session>();
	scheduler = Executors.newScheduledThreadPool(1);
	Runnable cleaner = new Runnable() {
	    @Override
	    public void run() {
		long current = System.currentTimeMillis();
		Set<Integer> keys = authorizedSessions.keySet();
		for (Integer integer : keys) {
		    long lastUse = authorizedSessions.get(integer).getLastUse();
		    if (lastUse < current - (1000 * 60 * SESSION_TIMEOUT)) {
			authorizedSessions.remove(integer);
		    }
		}
		keys = inAuthorizationSessions.keySet();
		for (Integer integer : keys) {
		    long lastUse = inAuthorizationSessions.get(integer).getLastUse();
		    if (lastUse < current - (1000 * 60 * SESSION_TIMEOUT)) {
			inAuthorizationSessions.remove(integer);
		    }
		}
	    }
	};
	scheduler.scheduleAtFixedRate(cleaner, 15, 15, TimeUnit.SECONDS);
    }

    /**
     * Start session method, it starts the authorization of access to a module
     * 
     * @param module
     *            The target module to authorize
     * @param auth
     *            Authorization parameter
     * @return the authorization process with information to agree in an shared
     *         key
     * 
     * @throws CertificateException
     *             if the certificate is not valid
     * @throws AuthorizationException
     *             if the module is not loaded
     * @throws ModuleManagerException
     *             if the module
     */
    public AuthorizationProccess startSession(String module, Authorization auth)
	    throws CertificateException, AuthorizationException, ModuleManagerException {

	if (!ModuleManager.isModuleAvailable(module)) {
	    throw AuthorizationException.NOT_LOADED;
	}
	PKIXUtils pkix = new PKIXUtils();
	pkix.verifyCertificate(auth.getCertificate());

	DH dh = new DH(DeviceBridge.P, DeviceBridge.G);
	String p = DeviceBridge.P.toString();
	String g = DeviceBridge.G.toString();
	String y = dh.getY().toString();
	Random rnd = new Random(dh.getY().longValue());
	int session = rnd.nextInt();
	if (session < 0)
	    session *= -1;
	AuthorizationProccess authProccess = new AuthorizationProccess(p, g, y, session);
	inAuthorizationSessions.put(session,
		new Session(session, dh, ModuleManager.getInstanceOfModule(module), auth.getCertificate()));
	return authProccess;
    }

    public DeviceMessage sendMessage(Integer session, DeviceMessage message) throws AuthorizationException {
	if (authorizedSessions.containsKey(session)) {
	    try {
		return authorizedSessions.get(session).sendMessageToModule(message);
	    } catch (Exception e) {
		throw new AuthorizationException(e.getMessage(), e);
	    }
	} else {
	    throw new AuthorizationException("Session not initialized, please use the proper method first");
	}
    }

    /**
     * Establish a session that is in process of authorization
     * 
     * @param sessionId the session ID to establish
     * @param auth the authentication parameters
     * @return the session id if the session was correctly authenticated
     * 
     * @throws AuthorizationException if the session wasn't authenticate
     */
    public Integer establishSession(Integer sessionId, AuthorizationProccess auth) throws AuthorizationException {
	if (inAuthorizationSessions.containsKey(sessionId)) {
	    try {
		Session session = inAuthorizationSessions.get(sessionId);
		if (this.checkSignature(auth, session)) {
		    session.getDh().setOtherY(new BigInteger(auth.getY()));
		    inAuthorizationSessions.remove(session);
		    session.init();
		    authorizedSessions.put(sessionId, session);
		    return sessionId;
		} else {
		    throw new AuthorizationException(
			    "Session not initialized, the signature of parameters do not match");
		}
	    } catch (Exception e) {
		throw new AuthorizationException(e.getMessage(), e);
	    }
	} else {
	    throw new AuthorizationException("Session not initialized, please use the proper method first");
	}
    }

    /**
     * Checks if the parameters were signed by the certificate holder
     * 
     * @param auth authentication parameters
     * @param session session parameters
     * @return true if the signature is valid
     * @throws Exception if the signature is not valid
     */
    private boolean checkSignature(AuthorizationProccess auth, Session session) throws Exception {

	PublicKey pubKey;
	try {
	    pubKey = session.generateCertificate().getPublicKey();
	    Signature sig = Signature.getInstance("SHA256withRSA");
	    byte[] p = session.getDh().getP().toString().getBytes();
	    byte[] g = session.getDh().getG().toString().getBytes();
	    byte[] y = auth.getY().toString().getBytes();
	    byte[] id = session.getId().toString().getBytes();
	    sig.initVerify(pubKey);
	    sig.update(id);
	    sig.update(p);
	    sig.update(g);
	    sig.update(y);
	    return sig.verify(Base64.decode(auth.getSignature()));
	} catch (CertificateException e) {
	    throw new AuthorizationException("The certificate is not corrected encoded", e);
	} catch (NoSuchAlgorithmException e) {
	    throw e;
	} catch (InvalidKeyException e) {
	    throw e;
	} catch (SignatureException e) {
	    throw e;
	}
    }

    public Collection<Session> getSessions() {
	return authorizedSessions.values();
    }

}
