package martinez.alex.lopez.laura.upc.clientapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChooseHoursActivity extends AppCompatActivity {

    // Creació d'una classe Turn, utilitzada per a dessignar els torns ja reservats i els que el client pot reservar.
    class Turn {
        String hour;
        boolean checked[];
        boolean reserved[];

        public Turn(String hour) { // Mètode constructor.
            this.hour = hour;
            this.checked = new boolean[4];
            this.reserved = new boolean[4];
        }
    }

    // Declaració d'una instància de FirebaseFirestore.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference turnsRef;

    // Declaració de la variable Adapter utilitzada en el Recycler View.
    private Adapter adapter;

    // Declaració de les variables lligades a la nova reserva.
    private Reserva reserva;
    private int time;
    private String docID;

    // Declaració de variables auxiliars.
    private int lastPos = -1, lastTurn = -1;

    // Declaració de Llistes.
    private List<Turn> reservedHours;
    private List<String> reservedHoursList;
    private List<String> dbReservedHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hours);

        // Es recuperen les dades que s'han transferit des de l'activitat ServiceActivity a partir de l'intent.
        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        // Creació de llistes buides.
        reservedHours = new ArrayList<>();
        dbReservedHours = new ArrayList<>();

        // Es recupera un String array que recopila els strings corresponents a les hores que es poden reservar.
        String[] array = getResources().getStringArray(R.array.hours);

        // S'inicialitza la llista de torns reservedHours amb camps buits i longitud igual a l'array.
        for (int i = 0; i < array.length; i++) {
            reservedHours.add(new Turn(array[i]));
        }

        String[] ReservationTime = reserva.getTime().split(" ");
        this.time = Integer.parseInt(ReservationTime[0]);

        docID = setDocID();

        // S'obtenen les referències als objectes del layout.
        RecyclerView hourListView = findViewById(R.id.HourListView);

        // Configuració del RecyclerView amb un LayoutManager i un Adapter.
        hourListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        hourListView.setAdapter(adapter);

        // S'afegeix un Listener amb el que obtenim les hores reservades en tots els torns d'una data de la col·leció de reserves de la base de dades.
        turnsRef= db.collection("reservas").document(docID).collection("turnos");

        turnsRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        OmpleTornsOcupats(documentSnapshots);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChooseHoursActivity.this);
                        builder.setMessage(R.string.error_reading_database_message);

                        builder.setPositiveButton(R.string.try_again, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        Log.d("dbError",e.toString());
                    }
                });
    }

    // Aquest métode retorna l'ID del document en format YYYYMMDD, per tal de facilitar el seu filtratge i ordenació a la base de dades.
    @NonNull
    private String setDocID() {
        Date date = reserva.getDate();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String[] sdate = dateFormat.format(date).split("/");
        return sdate[2] + sdate[1] + sdate[0];
    }

    @Override
    protected void onStart() {
        super.onStart();

        // S'afegeix un Listener que ens avisa cada vegada que la col·lecció de Firestore (Firebase) pateix qualsevol canvi.
        turnsRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("dbError",e.toString());
                    return;
                }
                // Es crida al mètode OmpleTornsOcupats
                OmpleTornsOcupats(documentSnapshots);

            }
        });
    }

    // Aquest mètode carrega la llista d'hores reservades amb les dades dels documents corresponents a les reserves de la data seleccionada.
    private void OmpleTornsOcupats(QuerySnapshot documentSnapshots) {
        dbReservedHours.clear();
        for (DocumentSnapshot doc : documentSnapshots) {
            reservedHoursList = (List<String>) doc.get("reservedHours");
            dbReservedHours.addAll(reservedHoursList);
        }
        // Es miren totes les hores de la llista dbReservedHours, i es posa a true el camp reserved de tots els torns (reservedHours) corresponents.
        for (int j = 0; j < dbReservedHours.size(); j++){
            String[] dbHour = dbReservedHours.get(j).split(":");
            int dbTurn = -1;

            if (dbHour[1].equals("00")) dbTurn = 0;
            else if (dbHour[1].equals("15")) dbTurn = 1;
            else if (dbHour[1].equals("30")) dbTurn = 2;
            else if (dbHour[1].equals("45")) dbTurn = 3;

            for (int k = 0; k < reservedHours.size(); k++)  {
                if ((dbHour[0]+":").equals(reservedHours.get(k).hour)) reservedHours.get(k).reserved[dbTurn] = true;
            }
        }
        adapter.notifyDataSetChanged(); // Es notifica a l'adaptador de que s'han actualitzat les dades de la llista de torns reservedHours.
    }

    private int calcCheckedTurns() {
        int count = 0;
        for (int pos = 0; pos < reservedHours.size(); pos++) {
            Turn turn = reservedHours.get(pos);
            for (int i = 0; i < turn.checked.length; i++) {
                if (turn.checked[i]) {
                    count++;
                }
            }
        }
        return count;
    }

    class Quarter {
        private int pos, i;
        Quarter(int pos, int i) {
            this.pos = pos;
            this.i = i;
        }
    }

    private void clearQuarters() {
        for (int pos = 0; pos < reservedHours.size(); pos++) {
            Turn turn = reservedHours.get(pos);
            for (int i = 0; i < turn.checked.length; i++) {
                turn.checked[i] = false;
            }
        }
    }

    private List<Quarter> quartersFrom(int pos, int index) {
        List<Quarter> result = new ArrayList<>();
        for (int i = 0; i < time/15; i++) {
            result.add(new Quarter(pos, index));
            index++;
            if (index >= 4) {
                index = 0;
                pos++;
                if (pos >= reservedHours.size()){
                    return result;
                }
            }
        }
        return result;
    }

    public void onClickChooseTurn(int pos, int turn) {
        boolean prevValue = reservedHours.get(pos).checked[turn];
        boolean isReserved = reservedHours.get(pos).reserved[turn];

        // S'ha clicat un quarter reservat.
        if (isReserved) {
            return;
        }

        clearQuarters();
        // S'ha clicat el mateix que l'anterior.
        if (pos == lastPos && turn == lastTurn && prevValue) {
            adapter.notifyDataSetChanged();
            return;
        }

        for (Quarter q : quartersFrom(pos, turn)) {
            if (reservedHours.get(q.pos).reserved[q.i]){
                clearQuarters();
                Toast.makeText(this, R.string.reserved_turns_message, Toast.LENGTH_LONG).show();
                return;
            } else {
                reservedHours.get(q.pos).checked[q.i] = true;
                adapter.notifyDataSetChanged();
            }
        }

        lastPos = pos;
        lastTurn = turn;
    }

    public void onClickNext(View view) {
        int numChecked = calcCheckedTurns();
        if (numChecked<this.time/15){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.missing_turns_message);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        else {

            OmpleReservedHours();

            Intent intent = new Intent(this,DetailsActivity.class);
            intent.putExtra("reserva", reserva);
            startActivityForResult(intent,0);

        }
    }

    private void OmpleReservedHours() {

        List<String> HourList = new ArrayList<>();

        for (int i = 0; i < reservedHours.size(); i++) {
            Turn t = reservedHours.get(i);
            for (int j = 0; j < t.checked.length; j++) {
                if (t.checked[j]) {
                    if (j==0){
                        HourList.add(t.hour + "00");
                    }
                    if (j==1){
                        HourList.add(t.hour + "15");
                    }
                    if (j==2){
                        HourList.add(t.hour + "30");
                    }
                    if (j==3){
                        HourList.add(t.hour + "45");
                    }
                }
            }
        }

        reserva.setReservedHours(HourList);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t00Button;
        private TextView t15Button;
        private TextView t30Button;
        private TextView t45Button;
        private TextView hourLabel;

        public ViewHolder(View HourView) {
            super(HourView);

            hourLabel = HourView.findViewById(R.id.HourLabel);
            t00Button = HourView.findViewById(R.id.t00Button);
            t15Button = HourView.findViewById(R.id.t15Button);
            t30Button = HourView.findViewById(R.id.t30Button);
            t45Button = HourView.findViewById(R.id.t45Button);


            t00Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    onClickChooseTurn(pos, 0);
                }
            });

            t15Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    onClickChooseTurn(pos, 1);
                }
            });

            t30Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    onClickChooseTurn(pos, 2);
                }
            });

            t45Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    onClickChooseTurn(pos, 3);
                }
            });
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View hourView = getLayoutInflater().inflate(R.layout.view_choose_hours,parent,false);
            return new ViewHolder(hourView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Turn turn = reservedHours.get(position);
            holder.hourLabel.setText(turn.hour);
            if (turn.reserved[0]) {
                holder.t00Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_reserved));
            } else {
                if (turn.checked[0]) {
                    holder.t00Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
                } else {
                    holder.t00Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
                }
            }

            if (turn.reserved[1]) {
                holder.t15Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_reserved));
            } else {
                if (turn.checked[1]) {
                    holder.t15Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
                } else {
                    holder.t15Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
                }
            }

            if (turn.reserved[2]) {
                holder.t30Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_reserved));
            } else {
                if (turn.checked[2]) {
                    holder.t30Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
                } else {
                    holder.t30Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
                }
            }

            if (turn.reserved[3]) {
                holder.t45Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_reserved));
            } else {
                if (turn.checked[3]) {
                    holder.t45Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
                } else {
                    holder.t45Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
                }
            }

        }

        @Override
        public int getItemCount() {
            return reservedHours.size();
        }
    }

}


