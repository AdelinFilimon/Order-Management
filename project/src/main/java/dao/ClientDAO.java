package dao;

import model.Client;

/**
 * The class represents an implementation of AbstractDAO class. It provides methods for finding and deleting rows based
 * on the client`s name
 */
public class ClientDAO extends AbstractDAO<Client> {

    /**
     * Method used for finding the client with the given name
     * @param name The name of the client to be searched
     * @return Returns the client with the given name
     */
    public Client findByName(String name) {
        if(findByField("name", name).size() == 0) return null;
        return findByField("name", name).get(0);
    }

    /**
     * Method used for deleting a client with the specified name
     * @param name The name of the client to be deleted
     */
    public void deleteByName(String name) {
        deleteByField("name", name);
    }

    /**
     * Method used for creating and inserting a new Client in the table
     * @param primaryKey The primary key used for creating a new Client
     * @param name The name used for creating a new Client
     * @param address The address used for creating a new Client
     * @return Returns the created client
     */
    public Client insertAndGet(Integer primaryKey, String name, String address) {
        Client client = new Client();
        client.setName(name);
        client.setAddress(address);
        client.setPrimaryKey(primaryKey);
        insert(client);
        return client;
    }

}
