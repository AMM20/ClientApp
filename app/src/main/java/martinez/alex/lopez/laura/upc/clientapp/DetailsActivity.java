package martinez.alex.lopez.laura.upc.clientapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;

public class DetailsActivity extends AppCompatActivity {

    private Reserva reserva;
    private Client client;

    private EditText EditName;
    private EditText EditLastName;
    private EditText EditEmail;
    private EditText EditPhone;
    private EditText EditNotes;

    private boolean fieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        client = new Client();


        EditName = findViewById(R.id.EditName);
        EditLastName = findViewById(R.id.EditLastName);
        EditEmail = findViewById(R.id.EditEmail);
        EditPhone = findViewById(R.id.EditPhone);
        EditNotes = findViewById(R.id.EditNotes);

    }

    public void onClickConfirm(View view) {

        OmpleClient();

        if (fieldsChecked) {
            reserva.setClient(client);
            Intent intent = new Intent(this,SummaryActivity.class);
            intent.putExtra("reserva", reserva);

            //intent.putExtra("client", (Serializable) client);

            startActivityForResult(intent,0);
        }else {
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

    private void OmpleClient() {

        fieldsChecked = true;

        client.setName(String.valueOf(EditName.getText()));
        if (client.getName().equals("")) fieldsChecked = false;

        client.setLastName(String.valueOf(EditLastName.getText()));
        if (client.getLastName().equals("")) fieldsChecked = false;

        client.setEmail(String.valueOf(EditEmail.getText()));
        if (client.getEmail().equals("")) fieldsChecked = false;

        if (String.valueOf(EditPhone.getText()).equals("")) fieldsChecked = false;
        else client.setPhone(Integer.parseInt(String.valueOf(EditPhone.getText())));

        client.setNotes(String.valueOf(EditNotes.getText()));

    }
}