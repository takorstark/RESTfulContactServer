package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.ContactXml;
import contact.service.ContactDao;

/**
 * Data access object for saving and retrieving contacts.
 * This DAO uses an in-memory list of person.
 * Use DaoFactory to get an instance of this class, such as:
 * dao = DaoFactory.getInstance().getContactDao()
 * 
 * @author jim
 */
public class MemContactDao implements ContactDao {
	private List<Contact> contacts;
	private AtomicLong nextId;
	
	public MemContactDao() {
		contacts = new ArrayList<Contact>();
		nextId = new AtomicLong(1000L);
		createTestContact(1);
		importContacts();
	}
	
	private void importContacts() {
		try {
			ContactXml inputContacts = new ContactXml();
			JAXBContext context = JAXBContext.newInstance( ContactXml.class ) ;
			File inputFile = new File( "ContactsServicePersistence.xml" );
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			inputContacts = (ContactXml) unmarshaller.unmarshal( inputFile );
			if ( inputContacts.getContacts() == null ) {
				return;
			}
			
			for ( int i=0; i<inputContacts.getContacts().size(); i++ ) {
				contacts.add(inputContacts.getContacts().get(i));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}

	/** add a single contact with given id for testing. */
	private void createTestContact(long id) {
		long id1 = 101;
		if (find(id1) == null) {
			Contact test = new Contact("Test contact", "Joe Experimental", "none@testing.com");
			test.setId(id1);
			contacts.add(test);
		}
		id1++;
		if (find(id1) == null) {
			Contact test2 = new Contact("Another test contact", "Testosterone", "testee@foo.com");
			test2.setId(id1);
			contacts.add(test2);
		}
		if (nextId.longValue() <= id1) nextId.set(id1+1);
	}

	/* (non-Javadoc)
	 * @see contact.service.ContactDao#find(long)
	 */
	public Contact find(long id) {
		for(Contact c : contacts) 
			if (c.getId() == id) return c;
		return null;
	}

	/* (non-Javadoc)
	 * @see contact.service.ContactDao#findAll()
	 */
	public List<Contact> findAll() {
		return java.util.Collections.unmodifiableList(contacts);
	}
	
	/*
	 * Find contacts whose title contains string
	 * @see contact.service.ContactDao#findByTitle(java.lang.String)
	 */
	public List<Contact> findByTitle(String match) {
		assert match != null : "Read the Javadoc for ContactDao";
		
		List<Contact> matchlist = new ArrayList<Contact>();
		// a regular expression to match part of the title. Use \b to match word boundary.
		Pattern pattern = Pattern.compile(".*"+match+".*", Pattern.CASE_INSENSITIVE);
		int size = contacts.size();
		for(int k=0; k<size; k++) {
			Contact contact = contacts.get(k);
			if ( pattern.matcher( contact.getTitle() ).matches() ) matchlist.add(contact);
		}
		return matchlist;
	}

	/* (non-Javadoc)
	 * @see contact.service.ContactDao#delete(long)
	 */
	public boolean delete(long id) {
		for(int k=0; k<contacts.size(); k++) {
			if (contacts.get(k).getId() == id) {
				contacts.remove(k);
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see contact.service.ContactDao#save(contact.entity.Contact)
	 */
	public boolean save(Contact contact) {
		if (contact.getId() == 0) {
			contact.setId( getUniqueId() );
			return contacts.add(contact);
		}
		// check if this contact is already in persistent storage
		Contact other  = find(contact.getId());
		if (other == contact) return true;
		if ( other != null ) contacts.remove(other);
		return contacts.add(contact);
	}

	/* (non-Javadoc)
	 * @see contact.service.ContactDao#update(contact.entity.Contact)
	 */
	public boolean update(Contact update) {
		Contact contact = find(update.getId());
		if (contact == null) return false;
		contact.applyUpdate(update);
		save(contact);
		return true;
	}
	
	/**
	 * Get a unique contact ID.
	 * @return unique id not in persistent storage
	 */
	private synchronized long getUniqueId() {
		long id = nextId.getAndAdd(1L);
		while( id < Long.MAX_VALUE ) {	
			if (find(id) == null) return id;
			id = nextId.getAndAdd(1L);
		}
		return id; // this should never happen
	}
}