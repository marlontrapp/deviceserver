package br.com.trapp.deviceserver.model.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import br.com.trapp.deviceserver.api.DeviceModule;
import br.com.trapp.deviceserver.model.TrustedCertificates;
import br.com.trapp.deviceserver.model.exception.ModuleManagerException;

public class ModuleManager {

    private static Map<ModuleInfo, Class<?>> availableModules = new HashMap<ModuleInfo, Class<?>>();

    public static void loadModule(File file) throws ModuleManagerException {
	Class<?> module;
	try {
	    module = ModuleManager.verifyJarSignature(file, (X509Certificate) TrustedCertificates.getDevCert());
	} catch (CertificateException | IOException e) {
	    throw new ModuleManagerException(e.getLocalizedMessage(), e);
	}
	if (module != null) {
	    availableModules.put(ModuleManager.getModuleIdentifier(module), module);
	} else {
	    throw ModuleManagerException.NOT_A_MODULE;
	}
    }

    /**
     * Compare it to the expected X509Certificate. If everything went well and
     * the certificates are the same, no exception is thrown.
     */
    private static Class<?> verifyJarSignature(File file, X509Certificate targetCert) throws IOException {

	ClassLoader cl = URLClassLoader.newInstance(new URL[] { new URL("jar:file:" + file.getAbsolutePath() + "!/") });
	JarFile jarFile = new JarFile(file);
	Class<?> portClass = null;
	// Sanity checking
	if (targetCert == null) {
	    jarFile.close();
	    throw new SecurityException("Provider certificate is invalid");
	}

	Vector<JarEntry> entriesVec = new Vector<JarEntry>();

	// Ensure the jar has a manifest file.
	Manifest man = jarFile.getManifest();
	if (man == null) {
	    jarFile.close();
	    throw new SecurityException("The jar is not signed, missing manifest file");
	}

	// Ensure all the entries' signatures verify correctly
	byte[] buffer = new byte[8192];
	Enumeration<JarEntry> entries = jarFile.entries();

	while (entries.hasMoreElements()) {
	    JarEntry je = (JarEntry) entries.nextElement();

	    // Skip directories.
	    if (je.isDirectory())
		continue;
	    entriesVec.addElement(je);
	    InputStream is = jarFile.getInputStream(je);

	    // Read in each jar entry. A security exception will
	    // be thrown if a signature/digest check fails.
	    while (is.read(buffer, 0, buffer.length) != -1) {
		// Don't care
	    }
	    is.close();
	}

	// Get the list of signer certificates
	Enumeration<JarEntry> e = entriesVec.elements();

	while (e.hasMoreElements()) {

	    JarEntry je = e.nextElement();
	    try {

		if (je.getName().endsWith(".class")) {
		    // -6 because of .class
		    String className = je.getName().substring(0, je.getName().length() - 6);
		    className = className.replace('/', '.');
		    Class<?> clazz;
		    clazz = cl.loadClass(className);

		    if (Arrays.stream(clazz.getInterfaces()).anyMatch(claz -> claz.equals(DeviceModule.class))) {
			portClass = clazz;
		    }
		}
	    } catch (ClassNotFoundException e1) {
		// Ignore
	    }
	    // Every file must be signed except files in META-INF.
	    Certificate[] certs = je.getCertificates();
	    if ((certs == null) || (certs.length == 0)) {
		if (!je.getName().startsWith("META-INF")) {
		    jarFile.close();
		    throw new SecurityException("The jar " + "has unsigned " + "class files.");
		}
	    } else {
		// Check whether the file is signed by the expected
		// signer. The jar may be signed by multiple signers.
		// See if one of the signers is 'targetCert'.
		boolean signedAsExpected = false;

		for (int i = 0; i < certs.length; i++) {
		    if (certs[i].equals(targetCert)) {
			// Stop since one trusted signer is found.
			signedAsExpected = true;
			break;
		    }
		}

		if (!signedAsExpected) {
		    jarFile.close();
		    throw new SecurityException("The jar " + "is not signed by a " + "trusted signer");
		}
	    }
	}
	jarFile.close();
	return portClass;
    }

    private static ModuleInfo getModuleIdentifier(Class<?> module) throws ModuleManagerException {
	try {
	    DeviceModule inst = ((DeviceModule) module.getConstructor().newInstance());
	    return new ModuleInfo(inst.getName(), inst.getIdendifier(), inst.getVersion());
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
	    throw new ModuleManagerException(e.getMessage(), e);
	}
    }

    public static DeviceModule getInstanceOfModule(String identifier) throws ModuleManagerException {
	try {
	    ModuleInfo moduleInfo = new ModuleInfo();
	    moduleInfo.setIdendifier(identifier);
	    Class<?> clazz = ModuleManager.availableModules.get(moduleInfo);
	    DeviceModule moduleInstance = ((DeviceModule) clazz.getConstructor().newInstance());
	    return moduleInstance;
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
	    throw new ModuleManagerException(e.getMessage(), e);
	}
    }

    /**
     * Get the loaded modules info
     * 
     * @return a set with the info of the loaded modules
     */
    public static Set<ModuleInfo> getAvailableModules() {
	return availableModules.keySet();
    }

    public static boolean isModuleAvailable(String module) {
	return availableModules.keySet().parallelStream().anyMatch(k -> k.getIdendifier().equals(module));
    }

    public static List<String> getAvailableModulesNames() throws ModuleManagerException {
	Set<ModuleInfo> keys = availableModules.keySet();
	List<String> result = new ArrayList<String>();
	for (ModuleInfo name : keys) {
	    result.add(name.getName());
	}
	return result;
    }
}
