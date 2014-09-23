
package contact.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.ContactXml;
import contact.service.ContactDao;
import contact.service.DaoFactory;

/**
 * Manage instances of Data Access Objects (DAO) used in the app.
 * This enables you to change the implementation of the actual ContactDao
 * without changing the rest of your application.
 * 
 * @author jim
 */
public class MemDaoFactory extends DaoFactory {
	/** instance of the entity DAO */
	private ContactDao daoInstance;
	private static MemDaoFactory factory;
	
	public MemDaoFactory() {
		daoInstance = new MemContactDao();
	}
	
	/**
	 * Get the instance of DaoFactory.
	 * @return instance of DaoFactory.
	 */
	public static MemDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new MemDaoFactory();
		}
		return factory;
	}
	
	
	/**
	 * Get the instance of ContactDao.
	 * @return instance of ContactDao.
	 */
	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}
	
	@Override
	public void shutdown() {
		//TODO here's your chance to show your skill!
		// Use JAXB to write all your contacts to a file on disk.
		// Then recreate them the next time a MemFactoryDao and ContactDao are created.
		
		List<Contact> contacts = daoInstance.findAll();
		ContactXml exportContacts = new ContactXml();
		exportContacts.setContacts( contacts );
		
		JAXBContext ctx;
		try {
			ctx = JAXBContext.newInstance( ContactXml.class ); 
			File outputFile = new File( "/tmp/ContactsSevicePersistence.xml" );
			Marshaller marshaller = ctx.createMarshaller();	
			marshaller.marshal( exportContacts, outputFile );
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
	}
}