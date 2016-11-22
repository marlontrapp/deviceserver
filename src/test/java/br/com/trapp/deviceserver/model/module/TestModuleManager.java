package br.com.trapp.deviceserver.model.module;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.trapp.deviceserver.model.DeviceBridge;
import br.com.trapp.deviceserver.model.TrustedCertificates;
import br.com.trapp.deviceserver.model.exception.ModuleManagerException;

public class TestModuleManager {


    // @Test(expected = SecurityException.class)
    public void testVerifyWithNotSignedJarShouldThrowException()
	    throws ModuleManagerException, JsonParseException, JsonMappingException, IOException {

	File file = new File("src/test/resources/test-jars/echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
    }

    // @Test
    public void testVerifyWithCorrectSignedJarShouldNotThrowException() throws ModuleManagerException {

	File file = new File("src/test/resources/test-jars/signed-echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
    }

    // @Test(expected = SecurityException.class)
    public void testVerifyWithCorrectSignedButNotTrustedJarShouldNotThrowException() throws ModuleManagerException {

	File file = new File("src/test/resources/test-jars/signed-other-echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
    }

    // @Test(expected = SecurityException.class)
    public void testVerifyWithCorrectSignedJarExceptForOneFileShouldThrowException() throws ModuleManagerException {

	File file = new File("src/test/resources/test-jars/invalid-signed-echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
    }

    // @Test
    public void testVerifyWithCorrectTwiceSignedJarShouldNotThrowException() throws ModuleManagerException {

	File file = new File("src/test/resources/test-jars/twice-signed-echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
    }

    // @Test(expected = SecurityException.class)
    public void testVerifyWithCorrectTwiceSignedJarExceptForOneFileShouldThrowException()
	    throws ModuleManagerException {

	File file = new File("src/test/resources/test-jars/invalid-twice-signed-echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
    }

    // @Test
    public void testGetAvailableModulesShouldReturnEchoMode() throws ModuleManagerException {

	File file = new File("src/test/resources/test-jars/signed-echo-module-1.0.0.jar");
	ModuleManager.loadModule(file);
	Set<ModuleInfo> availableModules = ModuleManager.getAvailableModules();
	assertTrue(availableModules.iterator().next().getIdendifier().equals("echo"));
    }

}
