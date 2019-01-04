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

    class Turn {
        String hour;
        boolean checked[];
        boolean reserved[];

        public Turn(String hour) {
            this.hour = hour;
            this.checked = new boolean[4];
            this.reserved = new boolean[4];
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference turnsRef;

    private Adapter adapter;
    private Reserva reserva;
    private int time;

    private int lastPos = -1, lastTurn = -1;

    private List<Turn> reservedHours;
    private List<String> reservedHoursList;
    private List<String> dbReservedHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hours);

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        reservedHours = new ArrayList<>();
        dbReservedHours = new ArrayList<>();

        String[] array = getResources().getStringArray(R.array.hours);

        for (int i = 0; i < array.length; i++) {
            reservedHours.add(new Turn(array[i]));
        }

        //reservedHours.get(4).reserved[0]=true;

        String[] ReservationTime = reserva.getTime().split(" ");
        this.time = Integer.parseInt(ReservationTime[0]);

        Date date = reserva.getDate();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String[] sdate = dateFormat.format(date).split("/");
        String docID = sdate[2] + sdate[1] + sdate[0];

        RecyclerView hourListView = findViewById(R.id.HourListView);

        hourListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        hourListView.setAdapter(adapter);

        //TODO: Conectar la base de datos de Firestore con la App y ver las horas ocupadas.

        // Se añade un Listener con el que obtenemos las horas reservadas en todos los turnos de una fecha de la colección de reservas de la base de datos.

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
                        Log.d("dbError",e.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Se añade un Listener que nos avisa cada vez que la colección de Firestore (Firebase) sufre algun cambio.
        turnsRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("dbError",e.toString());
                    return;
                }
                    OmpleTornsOcupats(documentSnapshots);

            }
        });
    }

    private void OmpleTornsOcupats(QuerySnapshot documentSnapshots) {
        dbReservedHours.clear();
        for (DocumentSnapshot doc : documentSnapshots) {
                reservedHoursList = (List<String>) doc.get("reservedHours");
                dbReservedHours.addAll(reservedHoursList);
        }
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
        adapter.notifyDataSetChanged();
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

        // He clicado un quarter reservado
        if (isReserved) {
            return;
        }

        clearQuarters();
        // El clicado el mismo que antes
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

        //adapter.notifyDataSetChanged();
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


