package br.com.trapp.deviceserver;

import java.security.cert.CertificateException;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.trapp.deviceserver.api.Authorization;
import br.com.trapp.deviceserver.api.AuthorizationProccess;
import br.com.trapp.deviceserver.api.DeviceMessage;
import br.com.trapp.deviceserver.controller.MainController;
import br.com.trapp.deviceserver.model.exception.AuthorizationException;
import br.com.trapp.deviceserver.model.exception.ModuleManagerException;
import br.com.trapp.deviceserver.model.module.ModuleInfo;

@Path("")
public class ServerResource {

    public static MainController controller;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<ModuleInfo> getAvailableDevices() {
	return controller.getAvailableModules();
    }

    @POST
    @Path("startsession/{module}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthorizationProccess startSession(@PathParam("module") String module, Authorization auth) {
	try {
	    return controller.startSession(module, auth);
	} catch (CertificateException e) {
	    e.printStackTrace();
	    throw new WebApplicationException(Response.status(500)
		    .entity("Please check if the given certificate is right encoded in PEM format").build());
	} catch (AuthorizationException e) {
	    throw new WebApplicationException(
		    Response.status(500).entity("The requested module is not loaded").build());
	} catch (ModuleManagerException e) {
	    throw new WebApplicationException(
		    Response.status(500).entity("The requested module was not recognized").build());
	} catch (Throwable e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    @POST
    @Path("establishsession/{session}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Integer establishSession(@PathParam("session") Integer session, AuthorizationProccess auth) {
	try {
	    return controller.establishSession(session, auth);
	} catch (AuthorizationException e) {
	    e.printStackTrace();
	    throw new WebApplicationException(Response.status(500).entity(e.getMessage()).build());
	}
    }

    @POST
    @Path("{session}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DeviceMessage sendMessage(@PathParam("session") Integer session, DeviceMessage message) {
	try {
	    return controller.sendMessage(session, message);
	} catch (AuthorizationException e) {
	    e.printStackTrace();
	    throw new WebApplicationException(Response.status(500).entity(e.getMessage()).build());
	}
    }

}