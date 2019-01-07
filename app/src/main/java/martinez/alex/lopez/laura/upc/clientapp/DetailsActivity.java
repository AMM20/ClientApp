package martinez.alex.lopez.laura.upc.clientapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Pattern;

public class DetailsActivity extends AppCompatActivity {

    private Reserva reserva;
    private Client client;

    private EditText editName;
    private EditText editLastName;
    private EditText editEmail;
    private EditText editPhone;
    private EditText editNotes;

    private boolean fieldsChecked = false;
    private boolean validEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        client = new Client();

        editName = findViewById(R.id.EditName);
        editLastName = findViewById(R.id.EditLastName);
        editEmail = findViewById(R.id.EditEmail);
        editPhone = findViewById(R.id.EditPhone);
        editPhone.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
        editPhone.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editNotes = findViewById(R.id.EditNotes);

    }

    public void onClickConfirm(View view) {

        OmpleClient();

        if (!fieldsChecked) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.missing_statements_message);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!validEmail) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.invalid_email_message);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            reserva.setClient(client);
            Intent intent = new Intent(this,SummaryActivity.class);
            intent.putExtra("reserva", reserva);
            intent.putExtra("client", client);

            startActivityForResult(intent,0);
        }
    }

    private void OmpleClient() {

        fieldsChecked = true;

        client.setName(String.valueOf(editName.getText()));
        if (client.getName().equals("")) fieldsChecked = false;

        client.setLastName(String.valueOf(editLastName.getText()));
        if (client.getLastName().equals("")) fieldsChecked = false;

        client.setEmail(String.valueOf(editEmail.getText()));
        if (client.getEmail().equals("")) fieldsChecked = false;
        else {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            if (!pattern.matcher(client.getEmail()).matches()) {
                validEmail = false;
            }else validEmail = true;
        }

        if (String.valueOf(editPhone.getText()).equals("")) fieldsChecked = false;
        else client.setPhone(Integer.parseInt(String.valueOf(editPhone.getText())));

        client.setNotes(String.valueOf(editNotes.getText()));

    }
}