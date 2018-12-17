package martinez.alex.lopez.laura.upc.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity {

    private Reserva reserva;
    private Client client;

    private TextView ClientName;
    private TextView ClientLastName;
    private TextView ClientReservationDate;
    private TextView ClientReservationHour;
    private TextView ClientServiceType;
    private TextView ClientProjectUse;
    private TextView ClientTotalCost;

    private int time;
    private int end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");
        client = (Client) intent.getSerializableExtra("client");

        ClientName = findViewById(R.id.ClientName);
        ClientLastName = findViewById(R.id.ClientLastName);
        ClientReservationDate = findViewById(R.id.ClientReservationDate);
        ClientReservationHour = findViewById(R.id.ClientReservationHour);
        ClientServiceType = findViewById(R.id.ClientServiceType);
        ClientProjectUse = findViewById(R.id.ClientProjectUse);
        ClientTotalCost = findViewById(R.id.ClientTotalCost);

        ClientName.setText(client.getName());
        ClientLastName.setText(client.getLastName());
        ClientReservationDate.setText(reserva.getDate());
        ClientServiceType.setText(reserva.getServiceType());
        ClientProjectUse.setText(reserva.getProjectUse());

        String[] ReservationTime = reserva.getTime().split(" ");
        this.time = Integer.parseInt(ReservationTime[0]);

        if (reserva.getProjectUse().equals(getString(R.string.Academic))) {
            ClientTotalCost.setText(getString(R.string.TotalCostAcademic));
        }
        if (reserva.getProjectUse().equals(getString(R.string.Personal))) {

            if (reserva.getServiceType().equals(getString(R.string.Autoservice))){
                int cost = (time/15)*10;
                ClientTotalCost.setText(String.valueOf(cost)+"€");
            }
            if (reserva.getServiceType().equals(getString(R.string.PROService))){
                int cost = time;
                ClientTotalCost.setText(String.valueOf(cost)+"€");
            }

        }

        String TurnStart = reserva.getReservedHours().get(0);
        String[] hour = reserva.getReservedHours().get(reserva.getReservedHours().size()-1).split(":");
        this.end = Integer.parseInt(hour[1]);
        int EndHour = end + 15;
        String TurnEnd = hour[0] + String.valueOf(EndHour);


        String ReservedTurn = TurnStart + " - " + TurnEnd;

        ClientReservationHour.setText(ReservedTurn);

    }
}
