package martinez.alex.lopez.laura.upc.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SummaryActivity extends AppCompatActivity {

    private Reserva reserva;
    private Client client;

    private TextView clientName;
    private TextView clientLastName;
    private TextView clientReservationDate;
    private TextView clientReservationHour;
    private TextView clientServiceType;
    private TextView clientProjectUse;
    private TextView clientTotalCost;

    private int time;
    private int end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");
        client = (Client) intent.getSerializableExtra("client");

        clientName = findViewById(R.id.ClientName);
        clientLastName = findViewById(R.id.ClientLastName);
        clientReservationDate = findViewById(R.id.ClientReservationDate);
        clientReservationHour = findViewById(R.id.ClientReservationHour);
        clientServiceType = findViewById(R.id.ClientServiceType);
        clientProjectUse = findViewById(R.id.ClientProjectUse);
        clientTotalCost = findViewById(R.id.ClientTotalCost);

        clientName.setText(client.getName());
        clientLastName.setText(client.getLastName());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String chosenDate = dateFormat.format(reserva.getDate());
        clientReservationDate.setText(chosenDate);
        clientServiceType.setText(reserva.getServiceType());
        clientProjectUse.setText(reserva.getProjectUse());

        String[] reservationTime = reserva.getTime().split(" ");
        this.time = Integer.parseInt(reservationTime[0]);

        if (reserva.getProjectUse().equals(getString(R.string.Academic))) {
            clientTotalCost.setText(getString(R.string.TotalCostAcademic));
        }
        if (reserva.getProjectUse().equals(getString(R.string.Personal))) {

            if (reserva.getServiceType().equals(getString(R.string.Autoservice))){
                int cost = (time/15)*10;
                clientTotalCost.setText(String.valueOf(cost)+"€");
            }
            if (reserva.getServiceType().equals(getString(R.string.PROService))){
                int cost = time;
                clientTotalCost.setText(String.valueOf(cost)+"€");
            }

        }

        String turnStart = reserva.getReservedHours().get(0);
        String[] hour = reserva.getReservedHours().get(reserva.getReservedHours().size()-1).split(":");
        this.end = Integer.parseInt(hour[1]);
        int endHour = end + 15;
        String turnEnd = hour[0] + String.valueOf(endHour);


        String reservedTurn = turnStart + " - " + turnEnd;

        clientReservationHour.setText(reservedTurn);

    }
}
