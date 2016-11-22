package br.com.trapp.deviceserver.model.exception;

public class AuthorizationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final AuthorizationException NOT_LOADED = new AuthorizationException(
	    "Module not installed or loaded");

    public AuthorizationException(String msg, Throwable throwable) {
	super(msg, throwable);
    }

    public AuthorizationException(String msg) {
	super(msg);
    }

}
