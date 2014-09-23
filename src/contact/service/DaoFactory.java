package contact.service;

public abstract class DaoFactory {

	private static DaoFactory factory;
	protected ContactDao daoInstance;

	public static DaoFactory getInstance() {
		if (factory == null) factory = new contact.service.jpa.JpaDaoFactory();
		return factory;
	}

	/**
	 * Get an instance of a data access object for Contact objects.
	 * Subclasses of the base DaoFactory class must provide a concrete
	 * instance of this method that returns a ContactDao suitable
	 * for their persistence framework.
	 * @return instance of Contact's DAO
	 */
	public abstract ContactDao getContactDao();
	
	/**
	 * Shutdown all persistence services.
	 * This method gives the persistence framework a chance to
	 * gracefully save data and close databases before the
	 * application terminates.
	 */
	public abstract void shutdown();


}