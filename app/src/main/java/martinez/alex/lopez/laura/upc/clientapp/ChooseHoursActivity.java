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

import java.util.ArrayList;
import java.util.List;

public class ChooseHoursActivity extends AppCompatActivity {

    class Turn {
        String hour;
        boolean checked[];

        public Turn(String hour) {
            this.hour = hour;
            this.checked = new boolean[4];
        }
    }

    private Adapter adapter;
    private Reserva reserva;
    private int time;

    private int checkedturns=0;
    private List<Turn> reservedHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_hours);

        reservedHours = new ArrayList<>();

        String[] array = getResources().getStringArray(R.array.hours);

        for (int i = 0; i < array.length; i++) {
            reservedHours.add(new Turn(array[i]));
        }

        //reservedHours.get(1).checked[2] = true;

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        String[] time = reserva.getTime().split(" ");
        this.time = Integer.parseInt(time[0]);

        RecyclerView hourListView = findViewById(R.id.HourListView);

        hourListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        hourListView.setAdapter(adapter);
    }

    public void onClickChooseTurn(int pos, int turn) {

        boolean prevValue = reservedHours.get(pos).checked[turn];

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
        }

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

            Intent intent = new Intent(this,DetailsActivity.class);
            intent.putExtra("reserva", reserva);
            startActivityForResult(intent,0);

        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t00Button;
        private TextView t15Button;
        private TextView t30Button;
        private TextView t45Button;
        private TextView HourLabel;

        public ViewHolder(View HourView) {
            super(HourView);

            HourLabel = HourView.findViewById(R.id.HourLabel);
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
            View HourView = getLayoutInflater().inflate(R.layout.view_choose_hours,parent,false);
            return new ViewHolder(HourView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Turn turn = reservedHours.get(position);
            holder.HourLabel.setText(turn.hour);
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


