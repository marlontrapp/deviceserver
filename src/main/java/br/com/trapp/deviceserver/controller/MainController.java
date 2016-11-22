package br.com.trapp.deviceserver.controller;

import java.security.cert.CertificateException;
import java.util.Set;

import br.com.trapp.deviceserver.api.Authorization;
import br.com.trapp.deviceserver.api.AuthorizationProccess;
import br.com.trapp.deviceserver.api.DeviceMessage;
import br.com.trapp.deviceserver.model.DeviceBridge;
import br.com.trapp.deviceserver.model.exception.AuthorizationException;
import br.com.trapp.deviceserver.model.exception.ModuleManagerException;
import br.com.trapp.deviceserver.model.module.ModuleInfo;
import br.com.trapp.deviceserver.model.module.ModuleManager;

public class MainController {

    private DeviceBridge deviceBridge;

    public MainController() {
	this.deviceBridge = new DeviceBridge();
    }

    public AuthorizationProccess startSession(String module, Authorization auth)
	    throws CertificateException, AuthorizationException, ModuleManagerException {

	return this.deviceBridge.startSession(module, auth);
    }

    public Set<ModuleInfo> getAvailableModules() {
	return ModuleManager.getAvailableModules();
    }

    public Integer establishSession(Integer session, AuthorizationProccess auth) throws AuthorizationException {
	session = this.deviceBridge.establishSession(session, auth);
	return session;
    }

    public DeviceMessage sendMessage(Integer session, DeviceMessage message) throws AuthorizationException {

	return this.deviceBridge.sendMessage(session, message);
    }

}
