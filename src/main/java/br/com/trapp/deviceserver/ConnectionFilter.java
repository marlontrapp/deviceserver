package br.com.trapp.deviceserver;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class ConnectionFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Filter all the requests, do nothing at the time
     */
    public void filter(ContainerRequestContext req) throws IOException {

	// DO NOT LEAVE THIS CODE HERE
	// DEBUG PURPOSE ONLY
	// IF YOU READ THE STREAM HERE THE SERVER WILL THINK THAT THE STREAM IS
	// EMPTY AND STRANGE BEHAVIOUR OCCURS
	// InputStream inStream = req.getEntityStream();
	// byte[] b = new byte[8192];
	// byte[] result = new byte[0];
	// while (inStream.read(b) != -1) {
	// result = Arrays.concatenate(result, b);
	// }
	//
	// System.out.println("-----Content-----");
	// System.out.println(new String(result));
	// System.out.println("-----End Content-----");
    }

    /**
     * Filter the responses of the server, add CORS allowing
     */
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
	    throws IOException {

	responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
	responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	responseContext.getHeaders().add("Access-Control-Allow-Headers",
		"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    }
}