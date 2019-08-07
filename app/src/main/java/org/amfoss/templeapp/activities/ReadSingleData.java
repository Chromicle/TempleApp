package org.amfoss.templeapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.amfoss.templeapp.R;
import org.amfoss.templeapp.databinding.ReadDataBinding;
import org.amfoss.templeapp.json_api.Controller;
import org.json.JSONException;
import org.json.JSONObject;

public class ReadSingleData extends AppCompatActivity {

    ReadDataBinding binding;

    String id, name;
    View view;
    int flag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.read_data);
        binding.setRead(new Read());
        view = this.getCurrentFocus();
    }

    public class Read {

        public void readButton(View view) {
            if (flag == 1) {
                id = getString(R.string.REG) + binding.editTextId.getText().toString();
            } else {
                id = getString(R.string.DON) + binding.editTextId.getText().toString();
            }
            new ReadDataActivity().execute();
        }

        public void radioGroup(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_donate:
                    binding.totalView.setVisibility(View.VISIBLE);
                    flag = 0;
                    Toast.makeText(getBaseContext(), getString(R.string.search_donations), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case R.id.radio_pooja:
                    binding.totalView.setVisibility(View.VISIBLE);
                    flag = 1;
                    Toast.makeText(getBaseContext(), getString(R.string.search_poojas), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    }

    class ReadDataActivity extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(ReadSingleData.this);
            dialog.setTitle(getString(R.string.wait));
            dialog.setMessage(getString(R.string.fetch));
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(Controller.TAG, getString(R.string.ID_value) + id);
            JSONObject jsonObject = Controller.readData(id);
            Log.i(Controller.TAG, getString(R.string.json_obj) + jsonObject);

            try {
                /*
                Check Whether Its NULL???
                */
                if (jsonObject != null) {
                    JSONObject user = jsonObject.getJSONObject(getString(R.string.user));
                    name = user.getString(getString(R.string.fetch_name));
                }
            } catch (JSONException je) {
                Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (name != null) {
                binding.idL.setText(getString(R.string.ID));
                binding.nameL.setText(getString(R.string.name));
                int flag = 0;
                if (id.substring(0, 3).equals(getString(R.string.DON))) {
                    binding.idV.setText(id.substring(3, id.length()));
                    flag = 1;
                } else if (id.substring(0, 3).equals(getString(R.string.REG))) {
                    binding.idV.setText(id.substring(3, id.length()));
                    flag = 0;
                }

                String[] str = name.split(getString(R.string.empty));
                binding.nameV.setText(str[2]);
                binding.am.setText(getString(R.string.amount_in_rs) + str[1]);
                binding.poojaL.setText(str[0]);
                binding.paidV.setText(str[3]);
                if (flag == 0) {
                    binding.poojaV.setText(getString(R.string.type_of_pooja));
                } else {
                    binding.poojaV.setText(getString(R.string.money_donated));
                }
                binding.paidL.setText(getString(R.string.paid_status));

            } else
                Toast.makeText(getBaseContext(), getString(R.string.id_not_found), Toast.LENGTH_SHORT)
                        .show();
        }
    }
}
