package contact.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 * 
 * @author Latthapat Tangtrustham 5510547014
 *
 */
@XmlRootElement(name="contactXml")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactXml {
	
		
		@XmlElement(name="contactXml")
	    private List<Contact> contacts;

		public List<Contact> getContacts() {
			return contacts;
		}

		public void setContacts( List<Contact> contacts ) {
			this.contacts = contacts;
		}
		
	
}
