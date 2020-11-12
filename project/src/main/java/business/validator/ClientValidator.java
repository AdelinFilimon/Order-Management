package business.validator;
import dao.ClientDAO;
import model.Client;


/**
 * Validator used for checking if a client already exists in the database table
 */
public class ClientValidator implements Validator<Client> {
    /**
     * @param client The client to be checked
     * @throws IllegalArgumentException If the client already exists in the table
     */
    @Override
    /**
     * The method used for validation
     */
    public void validate(Client client) {
        ClientDAO clientDAO = new ClientDAO();
        if(clientDAO.findByName(client.getName()) != null)
            throw new IllegalArgumentException("The client with that name already exists in the database");
    }
}
