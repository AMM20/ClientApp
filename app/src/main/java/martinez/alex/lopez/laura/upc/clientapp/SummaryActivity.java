package martinez.alex.lopez.laura.upc.clientapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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

    private String docID;
    private String reservationID;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


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
        String[] sdate = dateFormat.format(reserva.getDate()).split("/");
        docID = sdate[2] + sdate[1] + sdate[0];

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
            endMin = endMin +15;
            turnEnd = String.valueOf(endHour) + ":" + String.valueOf(endMin);
        }

        String reservedTurn = turnStart + " - " + turnEnd;

        clientReservationHour.setText(reservedTurn);

        // S'agafa l'hora d'inici de la reserva com a ID del document que correspon a aquesta reserva.
        String[] IDaux = turnStart.split(":");
        reservationID = IDaux[0]+IDaux[1];

    }

    public void onClickUploadReservation(View view) {

        // TODO: Implementar el métode que puja les dades de la reserva a Firestore.
        Map<String, Object> dbreserva = new HashMap<>();
        dbreserva.put("name", client.getName());
        dbreserva.put("lastName", client.getLastName());
        dbreserva.put("email", client.getEmail());
        dbreserva.put("phone", client.getPhone());
        dbreserva.put("notes", client.getNotes());
        dbreserva.put("projectUse", reserva.getProjectUse());
        dbreserva.put("serviceType", reserva.getServiceType());
        dbreserva.put("material", reserva.getMaterial());
        dbreserva.put("thickness", reserva.getThickness());
        dbreserva.put("totalCost", reserva.getTotalCost());
        dbreserva.put("time", reserva.getTime());
        dbreserva.put("reservedHours", reserva.getReservedHours());
        dbreserva.put("reservationID",reservationID);

        db.collection("reservas").document(docID).collection("turnos").document(reservationID)
                .set(dbreserva)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("db", "DocumentSnapshot successfully written!");

                        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
                        builder.setMessage(R.string.successful_reservation_message);

                        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("dbError", "Error writing document", e);
                    }
                });

    }
}
