package martinez.alex.lopez.laura.upc.clientapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ServiceActivity extends AppCompatActivity {

    private Reserva reserva;
    private Client client;

    private RadioGroup ProjectUseView, ServiceTypeView;
    private RadioButton btn_academic, btn_personal, btn_autoservice, btn_PROservice;
    private TextView ChosenMaterial, ChosenTime, ChosenDate;
    private EditText EditThickness;
    private ImageView LogoView;


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
        client = new Client();

        int PUchecked = ProjectUseView.getCheckedRadioButtonId();

        if (PUchecked == R.id.btn_academic){
            client.setProjectUse(getString(R.string.Academic));
        } else if (PUchecked == R.id.btn_personal){
            client.setProjectUse(getString(R.string.Personal));
        }

        int STchecked = ServiceTypeView.getCheckedRadioButtonId();

        if (STchecked == R.id.btn_autoservice) {
            client.setServiceType(getString(R.string.Autoservice));
        } else if (STchecked == R.id.btn_PROservice) {
            client.setServiceType(getString(R.string.PROService));
        }

        client.setMaterial(String.valueOf(ChosenMaterial.getText()));
        client.setTime(String.valueOf(ChosenTime.getText()));
        client.setThickness(String.valueOf(EditThickness.getText()));

        reserva.setDate(String.valueOf(ChosenDate.getText()));
        reserva.setClient(client);


    }

    public void onClickChooseMaterial(View view) {

    }

    public void onClickChooseTime(View view) {

    }

    public void onClickChooseDate(View view) {

    }
}
