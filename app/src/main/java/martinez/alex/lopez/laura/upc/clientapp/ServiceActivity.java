package martinez.alex.lopez.laura.upc.clientapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

public class ServiceActivity extends AppCompatActivity {

    private Reserva reserva;

    private RadioGroup ProjectUseView, ServiceTypeView;
    private RadioButton btn_academic, btn_personal, btn_autoservice, btn_PROservice;
    private TextView ChosenMaterial, ChosenTime, ChosenDate;
    private EditText EditThickness;
    private ImageView LogoView;

    private int Year, Month, Day, Hour, Minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        ProjectUseView = findViewById(R.id.ProjectUseView);
        ServiceTypeView = findViewById(R.id.ServiceTypeView);

        btn_academic = findViewById(R.id.btn_academic);
        btn_personal = findViewById(R.id.btn_personal);
        btn_autoservice = findViewById(R.id.btn_autoservice);
        btn_PROservice = findViewById(R.id.btn_PROservice);

        ChosenMaterial = findViewById(R.id.ChosenMaterial);
        ChosenTime = findViewById(R.id.ChosenTime);
        ChosenDate = findViewById(R.id.ChosenDate);

        EditThickness = findViewById(R.id.EditThickness);
        EditThickness.setFilters(new InputFilter[]{new InputFilterMinMax(0,10)});

        LogoView = findViewById(R.id.LogoView);

        Glide.with(this)
                .load("file:///android_asset/FABLABlogo.png")
                .apply(RequestOptions.fitCenterTransform())
                .into(LogoView);

    }


    public void onClickChooseHour(View view) {

        OmpleReserva();

        //Intent intent = new Intent(this,ChooseHourActivity.class);
        //intent.putExtra("reserva",reserva);
        //intent.putExtra("client",client);
        //StartActivityForResult(intent,0);

    }

    private void OmpleReserva() {

        reserva = new Reserva();

        int PUchecked = ProjectUseView.getCheckedRadioButtonId();

        if (PUchecked == R.id.btn_academic){
            reserva.setProjectUse(getString(R.string.Academic));
        } else if (PUchecked == R.id.btn_personal){
            reserva.setProjectUse(getString(R.string.Personal));
        }

        int STchecked = ServiceTypeView.getCheckedRadioButtonId();

        if (STchecked == R.id.btn_autoservice) {
            reserva.setServiceType(getString(R.string.Autoservice));
        } else if (STchecked == R.id.btn_PROservice) {
            reserva.setServiceType(getString(R.string.PROService));
        }

        reserva.setMaterial(String.valueOf(ChosenMaterial.getText()));
        reserva.setTime(String.valueOf(ChosenTime.getText()));
        reserva.setThickness(String.valueOf(EditThickness.getText()));

        reserva.setDate(String.valueOf(ChosenDate.getText()));
    }

    public void onClickChooseMaterial(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ChooseMaterial);

        /*String[] materials = new String[Reserva.materials.length];
        for (int i = 0; i < materials.length; i++) {
            materials[i] = getString(Reserva.materials[i]);
        }

        builder.setItems(materials, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reserva.setMaterial(getString(Reserva.materials[which]));
                ChosenMaterial.setText(Reserva.materials[which]);
            }
        });*/

        String[] materials = {getString(R.string.DM),getString(R.string.Plywood),getString(R.string.Cardboard),getString(R.string.Posterboard),getString(R.string.Methacrylate), getString(R.string.Other)};

        builder.setItems(materials, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // DM
                        ChosenMaterial.setText(getString(R.string.DM));
                        break;
                    case 1: // Plywood
                        ChosenMaterial.setText(getString(R.string.Plywood));
                        break;
                    case 2: // Cardboard
                        ChosenMaterial.setText(getString(R.string.Cardboard));
                        break;
                    case 3: // Posterboard
                        ChosenMaterial.setText(getString(R.string.Posterboard));
                        break;
                    case 4: // Methacrylate
                        ChosenMaterial.setText(getString(R.string.Methacrylate));
                        break;
                    case 5: // Other
                        ChosenMaterial.setText(getString(R.string.Other));
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onClickChooseTime(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ChooseMaterial);

        String[] time = {"<30 min","30 min","45 min","60 min","75 min","90 min","105 min","120 min"};

        builder.setItems(time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // <30 min
                        ChosenTime.setText("<30 min");
                        break;
                    case 1: // 30 min
                        ChosenTime.setText("30 min");
                        break;
                    case 2: // 45 min
                        ChosenTime.setText("45 min");
                        break;
                    case 3: // 60 min
                        ChosenTime.setText("60 min");
                        break;
                    case 4: // 75 min
                        ChosenTime.setText("75 min");
                        break;
                    case 5: // 90 min
                        ChosenTime.setText("90 min");
                        break;
                    case 6: // 105 min
                        ChosenTime.setText("105 min");
                        break;
                    case 7: // 120 min
                        ChosenTime.setText("120 min");
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onClickChooseDate(View view) {

        final Calendar calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ChosenDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, Year, Month, Day);
        datePickerDialog.show();

    }
}
