package martinez.alex.lopez.laura.upc.clientapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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

    private Adapter adapter;
    private Reserva reserva;
    private int time;

    private int checkedturns=0;
    private List<Turn> reservedHours;

    //TODO: Poner un ejemplo de reserva en la base de datos para poder ver las horas ocupadas y no permitir seleccionarlas.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hours);

        reservedHours = new ArrayList<>();

        String[] array = getResources().getStringArray(R.array.hours);

        for (int i = 0; i < array.length; i++) {
            reservedHours.add(new Turn(array[i]));
        }

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        String[] ReservationTime = reserva.getTime().split(" ");
        this.time = Integer.parseInt(ReservationTime[0]);

        RecyclerView hourListView = findViewById(R.id.HourListView);

        hourListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        hourListView.setAdapter(adapter);

        // Se añade un Listener que nos avisa cada vez que la colección de Firestore (Firebase) sufre algun cambio.

        db.collection("reservas").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentSnapshot doc : documentSnapshots) {
                    //Date inicio = doc.getDate("inicio");
                    //Long minutos = doc.getLong("minutos");

                }
            }
        });
    }

    //TODO: Modificar la manera de introducir las horas a reservar. En vez de seleccionarlas individualmente, hacerlo por paquetes, segun el tiempo que se ha reservado.

    public void onClickChooseTurn(int pos, int turn) {

        /*boolean prevValue = reservedHours.get(pos).checked[turn];

        if (!prevValue && (checkedturns>=this.time/15)) {
            return;
        }

        else {
            reservedHours.get(pos).checked[turn] = !prevValue;
            if (reservedHours.get(pos).checked[turn]){
                checkedturns++;
            }
            else {
                checkedturns--;
            }
        }*/

        boolean prevValue = reservedHours.get(pos).checked[turn];
        boolean isReserved = reservedHours.get(pos).reserved[turn];

        if ((!prevValue && (checkedturns>=this.time/15)) || isReserved) {
            return;
        }
        else if (!prevValue && (checkedturns<this.time/15) && !isReserved) {

            for (int i = 0; i < this.time/15; i++) {

                if (reservedHours.get(pos + (turn+i)/4 ).reserved[(turn + i)%4]) {
                    return;
                }
                else {
                  
                    reservedHours.get(pos + (turn+i)/4).checked[(turn+i)%4] = !prevValue;
                }
            }
                checkedturns = this.time/15;
            }
        /*else if (prevValue) {
            if (!reservedHours.get(pos - 1).checked[turn]){
                for (int i = 0; i < this.time/15; i++) {
                    reservedHours.get(pos + i).checked[turn] = !prevValue;
                }
                checkedturns = 0;
            }
            else {

            }
        }*/
        adapter.notifyDataSetChanged();
    }

    public void onClickNext(View view) {

        if (checkedturns<this.time/15){
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
            if (turn.checked[0]) {
                holder.t00Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
            }
            if (!turn.checked[0]) {
                holder.t00Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
            }

            if (turn.checked[1]) {
                holder.t15Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
            }
            if (!turn.checked[1]) {
                holder.t15Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
            }

            if (turn.checked[2]) {
                holder.t30Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
            }
            if (!turn.checked[2]) {
                holder.t30Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
            }

            if (turn.checked[3]) {
                holder.t45Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_marked));
            }
            if (!turn.checked[3]) {
                holder.t45Button.setBackground(getResources().getDrawable(R.drawable.rounded_back_unmarked));
            }

        }

        @Override
        public int getItemCount() {
            return reservedHours.size();
        }
    }

}


