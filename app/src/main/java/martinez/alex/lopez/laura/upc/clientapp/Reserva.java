package martinez.alex.lopez.laura.upc.clientapp;

import java.util.List;

public class Reserva {

    private String Date;
    private List<String> ReservedHours;
    private Client client;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public List<String> getReservedHours() {
        return ReservedHours;
    }

    public void setReservedHours(List<String> reservedHours) {
        ReservedHours = reservedHours;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
