package contact.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.jpa.JpaDaoFactory;

/**
 * ContactResource provides RESTful web resources using JAX-RS
 * 
 * 
 * @author Latthapat Tangtrustham 5510547014
 * @version 2014.09.16
 *
 */
@Path("/contacts")
public class ContactResource {
	
	private ContactDao dao = JpaDaoFactory.getInstance().getContactDao();
	private CacheControl cc;
	public ContactResource() {
		cc = new CacheControl();
		cc.setMaxAge(84600);
		cc.setPrivate(true);
	}
	
	/**
	 * Get one contact by id.
	 * 
	 * @param id id of contact to get
	 * @return Response OK
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getContactById( @PathParam("id") long id, @Context Request request) {
        Contact contact = dao.find(id);
        if(contact != null){
        	EntityTag etag = new EntityTag(contact.hashCode()+"");
        	ResponseBuilder builder = request.evaluatePreconditions(etag);
        	
        	 if(builder == null){
                 builder = Response.ok(contact);
                 builder.tag(etag);
        	 }
        	
        	 builder.cacheControl(cc);
             return builder.build();
        }
        else 
        	return Response.status(Status.NOT_FOUND).build();
    }
	
	/**
	 * Get a list of all contacts
	 * 
	 * @return Response OK
	 */
	public Response getContacts(){
		List<Contact> contacts = dao.findAll();
		if(contacts != null)
			return Response.ok(new GenericEntity<List<Contact>>(contacts){}).build();
		else
			return Response.status(Status.NOT_FOUND).build();
	}
	
	/**
	 * Get contact(s) whose title contains the query string
	 * 
	 * @param title title of contact to get
	 * @return Response OK
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getContactByTitle( @QueryParam("title") String title ){
		if(title==null){
			return getContacts();
		}
		
		List<Contact> contactList1 = dao.findAll();
		List<Contact> contactList2 = new ArrayList<Contact>(); 
		for(int i=0; i<contactList1.size(); i++){
			if(contactList1.get(i).getTitle().contains(title)){
				contactList2.add(contactList1.get(i));
			}
		}
		
		if(contactList2.size()==0)
			return Response.status(Status.NOT_FOUND).build();
		
		return Response.ok(new GenericEntity<List<Contact>>(contactList2){}).build();
		
	}
	
	/**
	 * Create a new contact. 
	 * If contact id is omitted or 0, 
	 * the server will assign a unique ID and return it as the Location header.
	 * 
	 * @param element XML body
	 * @param uriInfo uri
	 * @return Response Create
	 */
	@POST
	@Consumes( MediaType.APPLICATION_XML ) 
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo, @Context Request request )
	{
		Contact contact = element.getValue();
		if( dao.find(contact.getId()) != null ){
			return Response.status(Status.CONFLICT).build();
		}
		dao.save( contact );
		UriBuilder uri = uriInfo.getAbsolutePathBuilder();

		return Response.created(uri.path(contact.getId()+"").build()).tag(new EntityTag(contact.hashCode()+"")).build();
	}
	
	/**
	 * Update a contact. 
	 * 
	 * @param id
	 * @param element
	 * @return Response OK if id exist, Response BAD REQUEST if id does not exist
	 * @throws URISyntaxException
	 */
	@PUT
	@Path("{id}")
	@Consumes( MediaType.APPLICATION_XML ) 
	public Response put( @PathParam("id") long id, JAXBElement<Contact> element, @Context Request request) throws URISyntaxException
	{
		Contact contact = element.getValue();
		if(contact.getId()==id){
			
			dao.update(contact);
			
			EntityTag etag = new EntityTag(contact.hashCode()+"");
        	ResponseBuilder builder = request.evaluatePreconditions(etag);
        	
        	 if(builder == null){
                 builder = Response.ok(new URI("localhost:8080/contacts/"+contact.getId()).toString());
                 builder.tag(etag);
        	 }
        	
        	 builder.cacheControl(cc);
             return builder.build();
             
//			return Response.ok(new URI("localhost:8080/contacts/"+contact.getId()).toString()).build();
		} else
			return Response.status(Status.BAD_REQUEST).build();
	
	}
	
	/**
	 * Delete a contact with matching id.
	 * 
	 * @param id id of contact to delete
	 */
	@DELETE
	@Path("{id}")
	public Response deleteContact( @PathParam("id") long id, @Context Request request ){
		Contact contact = dao.find(id);
		
		if(dao.find(id)!=null){
			dao.delete(id);
			
			EntityTag etag = new EntityTag(contact.hashCode()+"");
        	ResponseBuilder builder = request.evaluatePreconditions(etag);
        	
        	 if(builder == null){
                 builder = Response.ok();
        	 }
        	
             return builder.build();
             
//			return Response.ok().build();
		}
		
		return Response.status(Status.NOT_FOUND).build();
	}
	
}
