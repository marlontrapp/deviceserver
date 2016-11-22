package br.com.trapp.deviceserver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import br.com.trapp.deviceserver.controller.MainController;
import br.com.trapp.deviceserver.model.exception.ModuleManagerException;
import br.com.trapp.deviceserver.model.module.ModuleManager;

//public class Main extends Application {
public class Main {
    private static final String MODULES_FOLDER = "/modules/";
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:9977/deviceserver/";
    // private static MainWindow mainWindow;
    private static MainController mainContoller;

    /**
     * Main method.
     * 
     * @param args
     * @throws IOException
     * @throws ModuleManagerException
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws IOException, ModuleManagerException, URISyntaxException {

	Path path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
	String dsJarFile = path.toString();
	String modulesFolderPath = dsJarFile.substring(0, dsJarFile.lastIndexOf(File.separator)) + MODULES_FOLDER;
	File modulesDir = new File(modulesFolderPath);
	if (modulesDir.exists()) {
	    Security.addProvider(new BouncyCastleProvider());
	    // Create server
	    final HttpServer server = startServer();
	    FilenameFilter filenameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
		    return name.endsWith(".jar");
		}
	    };
	    String[] modules = modulesDir.list(filenameFilter);
	    for (String moduleFilename : modules) {
		System.out.println("Loading module: " + moduleFilename);
		File module = new File(modulesFolderPath + File.separator + moduleFilename);
		ModuleManager.loadModule(module);
		System.out.println("Module loaded: " + moduleFilename);
	    }

	    System.out.println(String.format(
		    "Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...",
		    BASE_URI));

	    mainContoller = new MainController();
	    ServerResource.controller = mainContoller;
	    System.in.read();
	    server.shutdownNow();
	} else {
	    System.out.println("Exiting, modules folder not found");
	}
	System.exit(0);
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     * 
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

	try {
	    final ResourceConfig rc = new ResourceConfig(ServerResource.class);
	    rc.register(ConnectionFilter.class);

	    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, false);

	    // create the thread pool configuration
	    // reconfigure the thread pool
	    // Setting all thread pool with 1, because the purpose of this
	    // application
	    // is to have few connections only
	    NetworkListener listener = server.getListeners().iterator().next();
	    TCPNIOTransport transport = listener.getTransport();
	    ThreadPoolConfig workerThreadPoll = transport.getWorkerThreadPoolConfig();
	    workerThreadPoll.setCorePoolSize(1);
	    workerThreadPoll.setMaxPoolSize(2);
	    transport.setSelectorRunnersCount(1);
	    server.start();
	    return server;
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(0);
	}
	return null;
    }
}
