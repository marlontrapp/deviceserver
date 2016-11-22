package br.com.trapp.deviceserver.model.exception;

public class ModuleManagerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final ModuleManagerException NOT_A_MODULE = new ModuleManagerException(
	    "The given jar is not a valid module");

    public ModuleManagerException(String msg, Throwable throwable) {
	super(msg, throwable);
    }

    public ModuleManagerException(String msg) {
	super(msg);
    }

}
