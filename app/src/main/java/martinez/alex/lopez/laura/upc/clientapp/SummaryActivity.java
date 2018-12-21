package martinez.alex.lopez.laura.upc.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    private int endHour;
    private int endMin;


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
            reserva.setTotalCost(getString(R.string.TotalCostAcademic));
        }
        if (reserva.getProjectUse().equals(getString(R.string.Personal))) {
            int cost = 0;
            if (reserva.getServiceType().equals(getString(R.string.Autoservice))){
                cost = (time / 15) * 10;
                clientTotalCost.setText(String.valueOf(cost) + "€");
            }
            if (reserva.getServiceType().equals(getString(R.string.PROService))){
                cost = time;
                clientTotalCost.setText(String.valueOf(cost) + "€");
            }
            reserva.setTotalCost(String.valueOf(cost) + "€");

        }


        String turnStart = reserva.getReservedHours().get(0);
        String turnEnd;
        String[] hour = reserva.getReservedHours().get(reserva.getReservedHours().size()-1).split(":");
        this.endHour = Integer.parseInt(hour[0]);

        this.endMin = Integer.parseInt(hour[1]);

        if (endMin + 15 == 60) {
            endHour++;
            turnEnd = String.valueOf(endHour) + ":00";
        } else {
            turnEnd = String.valueOf(endHour) + ":" + String.valueOf(endMin);
        }

        String reservedTurn = turnStart + " - " + turnEnd;

        clientReservationHour.setText(reservedTurn);

    }

    public void onClickUploadReservation(View view) {

        // TODO: Implementar el métode que puja les dades de la reserva a Firestore.

    }
}
