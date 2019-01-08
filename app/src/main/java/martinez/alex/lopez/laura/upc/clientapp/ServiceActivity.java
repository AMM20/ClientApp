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

    // Declaració de les variables lligades al layout de l'activitat.
    private RadioGroup projectUseView, serviceTypeView;
    private RadioButton btn_academic, btn_personal, btn_autoservice, btn_PROservice;
    private TextView chosenMaterial, chosenTime, chosenDate;
    private EditText editThickness;
    private ImageView logoView;

    // Declaració de les variables lligades a la nova reserva.
    private Reserva reserva;

    // Declaració de les variables utilitzades per a mostrar la data actual i l'escollida al DatePickerDialog.
    private int year, month, day;
    private final Calendar calendar = Calendar.getInstance();

    // Declaració de variables booleanes per a comprobar si s'han omplert tots els camps.
    private boolean fieldsChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        // Creació d'un nou objecte de la classe Reserva.
        reserva = new Reserva();

        // S'obtenen les referències als objectes del layout.
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
        editThickness.setFilters(new InputFilter[]{new InputFilterMinMax(0,10)}); // S'afegeix un filte al camp EditThickness per a evitar que el client introdueixi un valor superior a 10 mm o un valor negatiu.

        logoView = findViewById(R.id.LogoView);

        // Es predefineixen els valors d'alguns dels camps que cal omplir.
        chosenMaterial.setText("DM");
        chosenTime.setText("30 min");
        editThickness.setText("3");

        // Es guarda la data actual dins de les variables year, month i day.
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Per tal d'actualitzar el valor de chosenDate que es mostra per pantalla, cal crear un String utilitzant les variables year, month i day, de tipus int.
        String sactualdate = Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
        chosenDate.setText(sactualdate);

        try {
            Date actualdate = new SimpleDateFormat("dd/MM/yyyy").parse(sactualdate); // Dins de la variable actualdate es guarda la data actual en format Date, utilitzant el patró dd/MM/yyyy.
            reserva.setDate(actualdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Glide.with(this)
                .load("file:///android_asset/FABLABlogo.png")
                .apply(RequestOptions.fitCenterTransform())
                .into(logoView);

    }

    public void onClickChooseHour(View view) {

        // Crida al mètode OmpleReserva()
        OmpleReserva();

        if (fieldsChecked) { // Si s'han omplert tots els camps correctament:
            Intent intent = new Intent(this,ChooseHoursActivity.class); // Es crea un nou intent que ha de cridar a l'activitat ChooseHoursActivity.
            intent.putExtra("reserva", reserva);  // S'introdueixen l'objecte reserva dins de l'intent.
            startActivityForResult(intent,0); // Es crida a l'activitat ChooseHoursActivity.
        } else {  // Si falten camps per omplir apareix un quadre de diàleg avisant al client de que cal omplir tots els camps per a poder continuar.
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

    // Aquest métode omple els camps de l'objecte reserva amb les dades entrades per l'usuari i les mostra per pantalla.
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
        reserva.setTime(String.valueOf(chosenTime.getText()));
        reserva.setThickness(String.valueOf(editThickness.getText())+" mm");
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
