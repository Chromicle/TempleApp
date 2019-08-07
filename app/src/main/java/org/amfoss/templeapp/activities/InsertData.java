package org.amfoss.templeapp.activities;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.amfoss.templeapp.R;
import org.amfoss.templeapp.databinding.InsertDataBinding;
import org.amfoss.templeapp.json_api.Controller;
import org.json.JSONException;
import org.json.JSONObject;

public class InsertData extends AppCompatActivity {

    InsertDataBinding insertDataBinding;

    String id, name, poojaTyp, overall, money, paidCheck = "NOT PAID", amnt;

    int flag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_data);

        insertDataBinding = DataBindingUtil.setContentView(this, R.layout.insert_data);
        insertDataBinding.setOnclick(new Onclick());
    }

    public class Onclick {
        public void insertButton(View view) {
            id = id + insertDataBinding.editTextId.getText().toString();
            String sp = String.valueOf(insertDataBinding.spinner1.getSelectedItem());
            name = insertDataBinding.name.getText().toString();
            if (id.length() == 3) {
                insertDataBinding.editTextId.setError(getString(R.string.valid_ID));
            } else {
                if (flag == 1) {
                    if (sp.contentEquals(getString(R.string.custompooja))) {
                        String name = insertDataBinding.custompooja.getText().toString();
                        poojaTyp = name;
                        amnt = insertDataBinding.amount.getText().toString();
                    } else {
                        poojaTyp = String.valueOf(insertDataBinding.spinner1.getSelectedItem());
                        amnt = insertDataBinding.amount.getText().toString();
                    }
                    overall =
                            poojaTyp
                                    + getResources().getString(R.string.empty)
                                    + amnt
                                    + getResources().getString(R.string.empty)
                                    + name
                                    + getResources().getString(R.string.empty)
                                    + paidCheck;
                    new InsertDataActivity().execute();
                } else {
                    money = insertDataBinding.moneyDonated.getText().toString();
                    overall =
                            money
                                    + getResources().getString(R.string.empty)
                                    + name
                                    + getResources().getString(R.string.empty)
                                    + paidCheck;
                    new InsertDataActivity().execute();
                }
            }
        }

        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_donate:
                    insertDataBinding.totalView.setVisibility(View.VISIBLE);
                    insertDataBinding.spinner1.setVisibility(View.GONE);
                    insertDataBinding.moneyDonated.setVisibility(View.VISIBLE);
                    insertDataBinding.custompooja.setVisibility(View.GONE);
                    insertDataBinding.amount.setVisibility(View.GONE);
                    id = getResources().getString(R.string.DON);
                    flag = 0;
                    Toast.makeText(getBaseContext(), getString(R.string.selected_donate), Toast.LENGTH_SHORT)
                            .show();
                    break;

                case R.id.radio_pooja:
                    insertDataBinding.totalView.setVisibility(View.VISIBLE);
                    insertDataBinding.moneyDonated.setVisibility(View.GONE);
                    insertDataBinding.spinner1.setVisibility(View.VISIBLE);
                    insertDataBinding.amount.setVisibility(View.VISIBLE);
                    id = getString(R.string.REG);
                    flag = 1;
                    Toast.makeText(
                                    getBaseContext(), getString(R.string.selected_register), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }

        public void onClick(View v) {
            if (((CheckBox) v).isChecked()) {
                paidCheck = getString(R.string.PAID);
            } else {
                paidCheck = getString(R.string.NOT_PAID);
            }
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if (i == 4) {
                insertDataBinding.custompooja.setVisibility(View.VISIBLE);
                insertDataBinding.amount.setVisibility(View.VISIBLE);
            } else {
                insertDataBinding.custompooja.setVisibility(View.GONE);
                insertDataBinding.amount.setVisibility(View.VISIBLE);
            }
        }
    }

    class InsertDataActivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        String result = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(InsertData.this);
            dialog.setTitle(getString(R.string.wait));
            dialog.setMessage(getString(R.string.insert));
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = Controller.insertData(id, overall);
            Log.i(Controller.TAG, getString(R.string.json_obj));

            try {
                /* Check Whether Its NULL??? */
                if (jsonObject != null) {
                    result = jsonObject.getString(getString(R.string.result));
                }
            } catch (JSONException je) {
                Log.i(Controller.TAG, getString(R.string.exception) + je.getLocalizedMessage());
            }
            if (flag == 1) {
                id = "REG";
            } else {
                id = "DON";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
        }
    }
}
