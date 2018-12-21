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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ServiceActivity extends AppCompatActivity {

    private Reserva reserva;

    private RadioGroup projectUseView, serviceTypeView;
    private RadioButton btn_academic, btn_personal, btn_autoservice, btn_PROservice;
    private TextView chosenMaterial, chosenTime, chosenDate;
    private EditText editThickness;
    private ImageView logoView;

    private int year, month, day;

    private boolean fieldsChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        reserva = new Reserva();

        projectUseView = findViewById(R.id.ProjectUseView);
        serviceTypeView = findViewById(R.id.ServiceTypeView);

        btn_academic = findViewById(R.id.btn_academic);
        btn_personal = findViewById(R.id.btn_personal);
        btn_autoservice = findViewById(R.id.btn_autoservice);
        btn_PROservice = findViewById(R.id.btn_PROservice);

        chosenMaterial = findViewById(R.id.ChosenMaterial);
        chosenTime = findViewById(R.id.ChosenTime);
        chosenDate = findViewById(R.id.ChosenDate);

        editThickness = findViewById(R.id.EditThickness);
        editThickness.setFilters(new InputFilter[]{new InputFilterMinMax(0,10)});

        logoView = findViewById(R.id.LogoView);

        Glide.with(this)
                .load("file:///android_asset/FABLABlogo.png")
                .apply(RequestOptions.fitCenterTransform())
                .into(logoView);

    }


    public void onClickChooseHour(View view) {

        OmpleReserva();

        if (fieldsChecked) {
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

        fieldsChecked = true;

        int pUchecked = projectUseView.getCheckedRadioButtonId();

        if (pUchecked == R.id.btn_academic){
            reserva.setProjectUse(getString(R.string.Academic));
        } else if (pUchecked == R.id.btn_personal){
            reserva.setProjectUse(getString(R.string.Personal));
        } else fieldsChecked = false;

        int sTchecked = serviceTypeView.getCheckedRadioButtonId();

        if (sTchecked == R.id.btn_autoservice) {
            reserva.setServiceType(getString(R.string.Autoservice));
        } else if (sTchecked == R.id.btn_PROservice) {
            reserva.setServiceType(getString(R.string.PROService));
        } else fieldsChecked = false;

        reserva.setMaterial(String.valueOf(chosenMaterial.getText()));
        if(reserva.getMaterial().equals("")) fieldsChecked = false;
        reserva.setTime(String.valueOf(chosenTime.getText()));
        if(reserva.getTime().equals("")) fieldsChecked = false;
        reserva.setThickness(String.valueOf(editThickness.getText())+" mm");
        if(reserva.getThickness().equals("")) fieldsChecked = false;

        if(chosenDate.getText().equals("dd/mm/yyyy")) fieldsChecked = false;

    }

    public void onClickChooseMaterial(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ChooseMaterial);

        final String[] materials = getResources().getStringArray(R.array.materials);

        builder.setItems(materials, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reserva.setMaterial(materials[which]);
                chosenMaterial.setText(materials[which]);
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
                chosenTime.setText(time[which]);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onClickChooseDate(View view) {

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String sdate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                chosenDate.setText(sdate);
                try {
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(sdate);
                    reserva.setDate(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        datePickerDialog.show();

    }

    public void onClickShowInfo(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.PRO_service_info_message)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
