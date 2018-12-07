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

import java.util.Arrays;
import java.util.List;

public class ChooseHoursActivity extends AppCompatActivity {

    // TODO Falta redissenyar el layout per a que es mostrin les hores lliures i les ocupades.
    // TODO Falta permetre escollir l'hora a la qual iniciarà el torn de la reserva actual.

    private List<String> hours;
    private RecyclerView HourListView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hours);

        String[] array = getResources().getStringArray(R.array.hours);
        hours = Arrays.asList(array);

        Intent intent = getIntent();
        Reserva reserva = (Reserva) intent.getSerializableExtra("reserva");

        HourListView = findViewById(R.id.HourListView);

        HourListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        HourListView.setAdapter(adapter);
    }

    private void onClickChooseTurn(int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView HourLabel;

        public ViewHolder(View HourView) {
            super(HourView);
            HourLabel = HourView.findViewById(R.id.HourLabel);

            HourView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickChooseTurn(getAdapterPosition());
                }
            });
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View HourView = getLayoutInflater().inflate(R.layout.view_choose_hours,parent,false);
            return new ViewHolder(HourView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String hour = hours.get(position);
            holder.HourLabel.setText(hour);
        }

        @Override
        public int getItemCount() {
            return hours.size();
        }
    }



}


