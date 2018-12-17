package martinez.alex.lopez.laura.upc.clientapp;

import java.io.Serializable;

public class Client implements Serializable {



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

    public String getEmail() {
        return Email;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public void setPhone(Integer phone) {
        Phone = phone;
    }

    public Integer getPhone() {
        return Phone;
    }

}
