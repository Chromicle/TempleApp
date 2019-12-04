package org.amfoss.templeapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.amfoss.templeapp.R;
import org.amfoss.templeapp.utils.DonationUtils;
import org.amfoss.templeapp.utils.UserUtils;

public class ConfirmDetailsDonationActivity extends AppCompatActivity {
    @BindView(R.id.textViewDonationAmount)
    TextView textViewDonationAmount;

    @BindView(R.id.textViewDonationDate)
    TextView textViewDonationDate;

    @BindView(R.id.textViewDonationCause)
    TextView textViewDonationCause;

    @BindView(R.id.textViewPilgrimName)
    TextView textViewPilgrimName;

    @BindView(R.id.btnDetailsCorrect)
    ImageButton btnDetailsCorrect;

    @BindView(R.id.btnDetailsInCorrect)
    ImageButton btnDetailsInCorrect;

    DatabaseReference donationDb;

    private String donationDate, pilgrimName, donationCause, donationAmount;
    private static String DB_POOJAS_NAME = "donations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_details_donation);

        ButterKnife.bind(this);

        createFirebaseInstance();
        getPoojaDetails();
        setTextViews();
    }

    private void createFirebaseInstance() {
        UserUtils user = new UserUtils();
        String dbUserName = user.getDbUserName();
        donationDb = FirebaseDatabase.getInstance().getReference(dbUserName);
    }

    private void setTextViews() {
        textViewPilgrimName.setText(pilgrimName);
        textViewDonationAmount.setText(donationAmount);
        textViewDonationDate.setText(donationDate);
        textViewDonationCause.setText(donationCause);
    }

    private void getPoojaDetails() {

        donationDate = getIntent().getStringExtra("donationDate");
        donationCause = getIntent().getStringExtra("donationCause");
        donationAmount = getIntent().getStringExtra("donationAmount");
        pilgrimName = getIntent().getStringExtra("pilgrimName");
    }

    @OnClick(R.id.btnDetailsCorrect)
    public void uploadDonationDetails(View view) {
        String id = donationDb.push().getKey();

        DonationUtils donationDetails =
                new DonationUtils(donationDate, pilgrimName, donationCause, donationAmount);
        donationDb
                .child(DB_POOJAS_NAME)
                .child(id)
                .setValue(donationDetails)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(
                                                ConfirmDetailsDonationActivity.this, "Donation Added", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(
                                                ConfirmDetailsDonationActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
    }

    @OnClick(R.id.btnDetailsInCorrect)
    public void correctDetails() {
        finish();
        super.onBackPressed();
    }
}
