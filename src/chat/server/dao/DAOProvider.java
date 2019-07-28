package chat.server.dao;

/**
 * A simple provider class that returns the internally
 * specified {@link DAO} implementation.
 */
public class DAOProvider {

    /** The singleton instance. */
    private static DAOProvider INSTANCE = new DAOProvider();

    /** The DAO implementation. */
    private DAO dao = new FileDAO();

    // Private constructor to block the creation of instances.
    private DAOProvider() { }

    /**
     * @return the {@link DAOProvider} instance
     */
    public static DAOProvider getInstance() {
        return INSTANCE;
    }

    /**
     * @return the DAO implementation
     */
    public DAO getDAO() {
        return dao;
    }
}
