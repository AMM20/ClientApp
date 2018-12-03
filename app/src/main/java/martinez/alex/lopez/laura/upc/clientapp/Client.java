package martinez.alex.lopez.laura.upc.clientapp;

public class Client {

    private String Name, LastName, Email, Notes, ProjectUse, ServiceType, Material, Thickness, TotalCost, Time;
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

    public String getProjectUse() {
        return ProjectUse;
    }

    public void setProjectUse(String projectUse) {
        ProjectUse = projectUse;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public void setThickness(String thickness) {
        Thickness = thickness;
    }

    public String getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(String totalCost) {
        TotalCost = totalCost;
    }

    public void setPhone(Integer phone) {
        Phone = phone;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }


}
