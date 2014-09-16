package connectserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

/**
 * <p>
 * This example shows how to deploy a RESTful web service
 * using a Jetty server that is created and started via code.
 * </p><p>
 * In this example, the resource classes are in the package "demo.resource".
 * Each resource class should be annotated with JAX-RS @Path("/something")
 * and the request handler methods should be annotated with @GET, @POST, etc.
 * </p>
 * <p>
 * This class creates a Jetty server on the specified port (default is PORT),
 * a ContextHandler that represents a Context containing a context path and
 * mapping of pathspecs to Servlets.
 * <p><tt>
 * handler.setContextPath("/")
 * </tt>/p><p>
 * Then the servlet holder is mapped to a path component (inside the context
 * path) using:
 * <p><tt>
 * handler.addServlet( servletHolder, "/*" );
 * </tt></p><p>
 * which means "map" everything inside this context to servletHolder.
 * In a more complex application (context), you could have many servlets
 * and map different pathspecs to different servlets.
 * <p>
 * In the case of a JAX-RS web service, each "resource" class also has
 * a @Path("/something") annotation, which can be used to map different
 * paths to different resources, so one ServletHolder can manage all your
 * resource classes.
 * </p>
 * 
 * <p>
 * I tested this with Jersey 2.12 and Jetty 9.2.  I used the following
 * JARs, referenced as libraries in Eclipse:
 * Jersey: lots of JARs! I included everything from:
 * jersey/lib directory, 
 * jersey/ext directory,
 * and
 * jersey/api/jaxrs.ws.rs-api-2.01.jar 
 * Some of these JARs probably aren't necessary, but I got runtime errors
 * about missing classes when I omitted JARs from the ext/ directory.
 * jersey/ext contains jars from other projects; this
 * may cause a problem if you have another version of the same JARs in
 * your project!  If you do, compare the JARs, or switch to a Maven
 * project so Maven will manage your dependencies. 
 * 
 * @author jim
 *
 */
public class JettyMain {
	/** The default port to listen on. Typically 80 or 8080.  
	 * On Ubuntu or MacOS if you are not root then you must use a port > 1024.
	 */
	static final int PORT = 8080;

	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * 
	 * @param args not used
	 * @throws Exception if Jetty server encounters any problem
	 */
	public static void main(String[] args) throws Exception {
		int port = PORT;  // the port the server will listen to for HTTP requests
		Server server = new Server( port );
		
		// (1) Use a ServletContextHandler to hold a "context" (our application)
		// that will be deployed on the server.
		// The parameter is a bitwise "or" of options, defined in ServletContextHandler.
		// Options are: SESSIONS, NO_SESSIONS, SECURITY, NO_SECURITY
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
		
		// (2) Define the path that server should map to our context.
		// If you use "/" it means the server root.
		context.setContextPath("/");
		
		
		// (3) Add servlets and mapping of requests to requests to servlets to the ContextHandler.
		// The ServletContextHandler class gives you several ways to do this:
		// To add a Servlet class and its pathspec:
		//    context.addServlet( Class<? extends Servlet> clazz, String pathspec )
		// To add an object (a ServletHolder):
		//    context.addServlet( ServletHolder servletHolder, String pathspec )
		
		// A Jetty ServletHolder holds a javax.servlet.Servlet instance along with a name, 
		// initialization parameters, and state information.  It implements the ServletConfig interface.
		// Here we use a ServletHolder to hold a Jersey ServletContainer.
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		
		// (4) Configure the Jersey ServletContainer so it will manage our resource
		// classes and pass HTTP requests to our resources.
		// Do this by setting initialization parameters.
		// The ServletContainer javadoc tells you what the initialization parameter are.
		// This initialization parameter tells Jersey to auto-configure all resource classes
		// in the named package(s). 
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "contact.resource");
		context.addServlet( holder, "/*" );

		// (5) Add the context (our application) to the Jetty server.
		server.setHandler( context );
		
		System.out.println("Starting Jetty server on port " + port);
		server.start();
		
		System.out.println("Server started.  Press ENTER to stop it.");
		int ch = System.in.read();
		System.out.println("Stopping server.");
		server.stop();
	}
	
}

