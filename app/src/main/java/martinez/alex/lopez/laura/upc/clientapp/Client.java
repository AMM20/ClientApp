package martinez.alex.lopez.laura.upc.clientapp;

public class Client {

    private String Name, LastName, Email, Notes;
    private Integer Phone;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public void setPhone(Integer phone) {
        Phone = phone;
    }

}
