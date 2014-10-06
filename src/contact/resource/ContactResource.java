package contact.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.mem.MemDaoFactory;

// This comment is vague. Doesn't say what this class actually does.
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
// Don't use MemDaoFactory use DaoFactory

	private ContactDao dao = MemDaoFactory.getInstance().getContactDao();
	
	public ContactResource() {
		
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
	public Response getContactById( @PathParam("id") long id ) {
        Contact contact = dao.find(id);
        if(contact != null)
        	return Response.ok(contact).build();
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
// DON'T do this. Let ContactDao perform the match which is much more efficient
// since it will create fewer objects.  
// See ContactDao.findByTitle()
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
	public Response post(JAXBElement<Contact> element, @Context UriInfo uriInfo )
	{
		Contact contact = element.getValue();
		if( dao.find(contact.getId()) != null ){
			return Response.status(Status.CONFLICT).build();
		}
		dao.save( contact );
		UriBuilder uri = uriInfo.getAbsolutePathBuilder();

		return Response.created(uri.path(contact.getId()+"").build()).build();
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
	public Response put( @PathParam("id") long id, JAXBElement<Contact> element) throws URISyntaxException
	{
		Contact contact = element.getValue();
		if(contact.getId()==id){
			dao.update(contact);
//BAD CODE. Use uriInfo
			return Response.ok(new URI("localhost:8080/contacts/"+contact.getId()).toString()).build();
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
	public Response deleteContact( @PathParam("id") long id ){
		dao.delete(id);
//LOGIC ERROR.
		if(dao.find(id)!=null){
			return Response.ok().build();
		}
		
		return Response.status(Status.NOT_FOUND).build();
	}
	
}
