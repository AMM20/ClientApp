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

    private int Year, Month, Day;

    private boolean fieldsCheceked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        reserva = new Reserva();

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

        if (fieldsCheceked) {
            Intent intent = new Intent(this,ChooseHoursActivity.class);
            intent.putExtra("reserva", reserva);
            startActivityForResult(intent,0);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.missing_statements_message);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void OmpleReserva() {

        fieldsCheceked = true;

        int PUchecked = ProjectUseView.getCheckedRadioButtonId();

        if (PUchecked == R.id.btn_academic){
            reserva.setProjectUse(getString(R.string.Academic));
        } else if (PUchecked == R.id.btn_personal){
            reserva.setProjectUse(getString(R.string.Personal));
        } else fieldsCheceked = false;

        int STchecked = ServiceTypeView.getCheckedRadioButtonId();

        if (STchecked == R.id.btn_autoservice) {
            reserva.setServiceType(getString(R.string.Autoservice));
        } else if (STchecked == R.id.btn_PROservice) {
            reserva.setServiceType(getString(R.string.PROService));
        } else fieldsCheceked = false;

        reserva.setMaterial(String.valueOf(ChosenMaterial.getText()));
        if(reserva.getMaterial().equals("")) fieldsCheceked = false;
        reserva.setTime(String.valueOf(ChosenTime.getText()));
        if(reserva.getTime().equals("")) fieldsCheceked = false;
        reserva.setThickness(String.valueOf(EditThickness.getText()));
        if(reserva.getThickness().equals("")) fieldsCheceked = false;

        reserva.setDate(String.valueOf(ChosenDate.getText()));
        if(reserva.getDate().equals("dd/mm/yyyy")) fieldsCheceked = false;

    }

    public void onClickChooseMaterial(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ChooseMaterial);

        final String[] materials = getResources().getStringArray(R.array.materials);

        builder.setItems(materials, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reserva.setMaterial(materials[which]);
                ChosenMaterial.setText(materials[which]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onClickChooseTime(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ChooseTime);

        final String[] time = getResources().getStringArray(R.array.time);

        builder.setItems(time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reserva.setMaterial(time[which]);
                ChosenTime.setText(time[which]);
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
