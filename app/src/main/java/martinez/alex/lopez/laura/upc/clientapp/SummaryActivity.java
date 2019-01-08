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

    // Declaració de les variables lligades al layout de l'activitat.
    private TextView clientName;
    private TextView clientLastName;
    private TextView clientReservationDate;
    private TextView clientReservationHour;
    private TextView clientServiceType;
    private TextView clientProjectUse;
    private TextView clientTotalCost;

    // Declaració de les variables lligades a la nova reserva.
    private Reserva reserva;
    private Client client;
    private int time;
    private String docID;
    private String reservationID;

    // Declaració d'una instància de FirebaseFirestore.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Es recuperen les dades que s'han transferit des de l'activitat DetailsActivity a partir de l'intent.
        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");
        client = (Client) intent.getSerializableExtra("client");

        // S'obtenen les referències als objectes del layout.
        clientName = findViewById(R.id.ClientName);
        clientLastName = findViewById(R.id.ClientLastName);
        clientReservationDate = findViewById(R.id.ClientReservationDate);
        clientReservationHour = findViewById(R.id.ClientReservationHour);
        clientServiceType = findViewById(R.id.ClientServiceType);
        clientProjectUse = findViewById(R.id.ClientProjectUse);
        clientTotalCost = findViewById(R.id.ClientTotalCost);

        // Es crida al métode OmpleDetallsReserva()
        OmpleDetallsReserva();

    }
    // El métode OmpleDetallsReserva() recupera les dades de la reserva actual i les mostra en els camps corresponents del layout de l'activitat.
    private void OmpleDetallsReserva() {
        clientName.setText(client.getName());
        clientLastName.setText(client.getLastName());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String chosenDate = dateFormat.format(reserva.getDate());
        String[] sdate = chosenDate.split("/");
        docID = sdate[2] + sdate[1] + sdate[0]; // ID del document que conté totes les reserves d'una data determinada, en format YYYYMMDD, per tal de facilitar el seu filtratge i ordenació a la base de dades.

        clientReservationDate.setText(chosenDate);
        clientServiceType.setText(reserva.getServiceType());
        clientProjectUse.setText(reserva.getProjectUse());

        String[] reservationTime = reserva.getTime().split(" ");
        int time = Integer.parseInt(reservationTime[0]);

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

        clientReservationHour.setText(getReservedTurn(reserva));

    }

    // Aquest mètode agafa l'Array de Stings del camp ReservedHours de l'objecte Reserva i retorna l'hora d'inici i fi en un únic String.
    private String getReservedTurn(Reserva res) {

        String turnStart = res.getReservedHours().get(0); // L'hora d'inici es correspon amb el primer element de l'Array de Strings.
        String turnEnd;
        String[] hour = res.getReservedHours().get(res.getReservedHours().size()-1).split(":"); // L'últim element de l'Array es correspon amb l'hora d'inici de l'últim torn reservat.
        int endHour = Integer.parseInt(hour[0]);
        int endMin = Integer.parseInt(hour[1]);
        // Per tal d'obtenir l'hora d'acabament de la reserva, nomès cal sumar-li un quart d'hora a endMin. Es distingeix entre dos casos.
        if (endMin + 15 == 60) {
            endHour++;
            turnEnd = String.valueOf(endHour) + ":00";
        } else {
            endMin = endMin +15;
            turnEnd = String.valueOf(endHour) + ":" + String.valueOf(endMin);
        }

        // S'agafa l'hora d'inici de la reserva com a ID del document que correspon a aquesta reserva.
        String[] IDaux = turnStart.split(":");
        reservationID = IDaux[0]+IDaux[1];

        // Es retorna un únic String
        return turnStart + " - " + turnEnd;

    }

    // Aquest métode es crida quan es clica el botó ConfirmButton. Es crea un HashMap on s'introdueixen totes les dades que es carregaran a la base de dades de Firestore.
    public void onClickUploadReservation(View view) {

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

        db.collection("reservas").document(docID).collection("turnos").document(reservationID) // Es crea un nou document a la base de dades que es correspon a la nova reserva.
                .set(dbreserva) // Es carreguen les dades en els camps del document.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { // Un cop carregada la reserva a la base de dades, es mostra un quadre de diàleg per a informar de que s'ha rebut correctament la reserva.                      Log.d("db", "DocumentSnapshot successfully written!");

                        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
                        builder.setMessage(R.string.successful_reservation_message);

                        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity(); // Es tanca l'aplicació completament. Cal tornar a iniciar-la per a fer una nova reserva.
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {// En el cas de que falli la càrrega del document, es mostra un error al Log.
                        Log.w("dbError", "Error writing document", e);
                    }
                });

    }
}
