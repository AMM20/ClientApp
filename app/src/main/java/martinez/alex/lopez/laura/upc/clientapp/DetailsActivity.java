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

    // Declaració de les variables lligades al layout de l'activitat.
    private EditText editName, editLastName, editEmail, editPhone, editNotes;

    // Declaració de les variables lligades a la nova reserva.
    private Reserva reserva;
    private Client client;

    // Declaració de variables booleanes per a comprobar si s'han omplert correctament tots els camps.
    private boolean fieldsChecked = false, validEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Es recuperen les dades que s'han transferit des de l'activitat ChooseHoursActivity a partir de l'intent.
        Intent intent = getIntent();
        reserva = (Reserva) intent.getSerializableExtra("reserva");

        // Creació d'un objecte de la classe Client.
        client = new Client();

        // S'obtenen les referències als objectes del layout.
        editName = findViewById(R.id.EditName);
        editLastName = findViewById(R.id.EditLastName);
        editEmail = findViewById(R.id.EditEmail);
        editPhone = findViewById(R.id.EditPhone);
        editPhone.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
        editPhone.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editNotes = findViewById(R.id.EditNotes);

    }

    public void onClickConfirm(View view) {

        // Crida al mètode OmpleClient().
        OmpleClient();

        if (!fieldsChecked) { // Si falten camps per omplir apareix un quadre de diàleg avisant al client de que cal omplir tots els camps per a poder continuar.

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.missing_statements_message);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    return;
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (!validEmail) { // Si s'ha introduït un correu electrònic no vàlid, es mostra un quadre de diàleg informant d'aquest error.

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
        else { // Si s'han omplert tots els camps correctament:

            reserva.setClient(client); // Es guarda l'objecte client dins de la reserva creada a l'activitat ServiceActivity.
            Intent intent = new Intent(this,SummaryActivity.class); // Es crea un nou intent que ha de cridar a l'activitat SummaryActivity.
            intent.putExtra("reserva", reserva); // S'introdueixen l'objecte reserva dins de l'intent, juntament amb l'objecte client creat en aquesta activitat.
            intent.putExtra("client", client);
            startActivityForResult(intent,0); // Es crida a l'activitat SummaryActivity.

        }
    }

    // Aquest métode omple els camps de l'objecte client amb les dades entrades per l'usuari.
    private void OmpleClient() {

        fieldsChecked = true; // S'incicialitza fieldsChecked a true cada vegada que s'entra al mètode, Si es troba un camp que cal omplir buit, fieldsChecked passa a ser false.

        client.setName(String.valueOf(editName.getText()));
        if (client.getName().equals("")) fieldsChecked = false;

        client.setLastName(String.valueOf(editLastName.getText()));
        if (client.getLastName().equals("")) fieldsChecked = false;

        client.setEmail(String.valueOf(editEmail.getText()));
        if (client.getEmail().equals("")) fieldsChecked = false;
        else {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            if (!pattern.matcher(client.getEmail()).matches()) {// Es comprova si l'email introduït es correspon amb un patró determinat i, per tant, aquest és vàlid ( Si és vàlid -> validEmail = true).
                validEmail = false;
            }else validEmail = true;
        }

        if (String.valueOf(editPhone.getText()).equals("")) fieldsChecked = false;
        else client.setPhone(Integer.parseInt(String.valueOf(editPhone.getText()))); // Ja que el camp editPhone conté un String, aquest cal convertir-lo a Integer abans de guardar-lo a l'objecte client.

        client.setNotes(String.valueOf(editNotes.getText()));

    }
}