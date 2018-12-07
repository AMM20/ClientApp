package martinez.alex.lopez.laura.upc.clientapp;

import java.util.List;

public class Reserva {

    private String Date,ProjectUse, ServiceType, Material, Thickness, TotalCost, Time;
    private List<String> ReservedHours;
    private Client client;

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

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getThickness() {
        return Thickness;
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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

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
