package de.pseudonymisierung.mainzelliste.client;

public class Validator {

    private final MainzellisteConnection connection;

    /**
     * Communicates with the validate endpoint of the Mainzelliste
     * @param connection - the connection needed for communication with the Mainzelliste
     */
    public Validator(MainzellisteConnection connection) {
        this.connection = connection;
    }

    /**
     * This Method will validate with the if a token is valid
     * @param tokenId
     * @return
     */
    public boolean checkTokenValid(String tokenId) throws MainzellisteNetworkException {
        MainzellisteResponse mainzellisteResponse = this.connection.doRequest(MainzellisteConnection.RequestMethod.GET, this.connection.getMainzellisteURI() + "validate/token?tokenId=" + tokenId, null);
        return 0 < mainzellisteResponse.getStatusCode() && mainzellisteResponse.getStatusCode() < 300;
    }

}
