package contact.service;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import connectserver.JettyMain;
/**
 * Test in ETag
 * 
 * @author Latthapat Tangtrustham 5510547014
 *
 */
public class EtagTest {
	private static String serviceUrl;
	private static HttpClient client;
	@BeforeClass
	public static void doFirst( ) throws Exception {
		serviceUrl = JettyMain.startServer( 8080 );
		client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
	@AfterClass
	public static void doLast( ) throws Exception {
		// stop the Jetty server after the last test
		JettyMain.stopServer();
	}
	
	@Test
	public void testETagGet() {
		try {
			ContentResponse res = client.GET(serviceUrl+"contacts/101");
			 assertEquals("Response should be 200 OK", Status.OK.getStatusCode(), res.getStatus());
			String etag = res.getHeaders().get("ETag");
			assertTrue("ETag must exist", etag != null);
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	/**
	  * Test success POST.
	  * @throws InterruptedException
	  * @throws ExecutionException
	  * @throws TimeoutException
	  */
	 @Test
	 public void testPostPass() throws InterruptedException, ExecutionException, TimeoutException {
		 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
					"<title>RoboEarth</title>" +
					"<name>Earth Name</name>" +
					"<email>earth@email</email>" +
					"<phoneNumber>0000000000</phoneNumber>"+
					"</contact>");
		 Request request = client.newRequest(serviceUrl+"contacts");
		 request.method(HttpMethod.POST);
		 request.content(content, "application/xml");
		 
		 ContentResponse res = request.send();
		
		 assertEquals("POST complete ,should response 201 Created", Status.CREATED.getStatusCode(), res.getStatus());
		 res = client.GET(serviceUrl+"contacts/123");
		 String etag = res.getHeaders().get("ETag");
		 
		 assertTrue("ETag must be existed", etag != null);
	 }
	
	
}
