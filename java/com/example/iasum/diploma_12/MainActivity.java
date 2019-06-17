package com.example.iasum.diploma_12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String LOG_TAG = "my_log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new CategoryTask().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void closeModal(View view) {
        LinearLayout modalLL = (LinearLayout) findViewById(R.id.modal_product);
        modalLL.setVisibility(View.GONE);

        LinearLayout mainLL = (LinearLayout) findViewById(R.id.main_layout);
        mainLL.setVisibility(View.VISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    public void productAction(View view) {
        Button productAction = (Button) findViewById(R.id.product_action);
        JSONObject tempValues = new JSONObject();
        String productID = String.valueOf(view.getId());

        try {
            tempValues.put("id", productID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EditText productNameET = (EditText) findViewById(R.id.product_name);
        String productName = productNameET.getText().toString();
        try {
            tempValues.put("name", productName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EditText productCodeET = (EditText) findViewById(R.id.product_code);
        String productCode = productCodeET.getText().toString();
        try {
            tempValues.put("code", productCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            String testStr = tempValues.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EditText productPriceET = (EditText) findViewById(R.id.product_price);
        String productPrice = productPriceET.getText().toString();
        try {
            tempValues.put("price", productPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout propertiesLL = (LinearLayout) findViewById(R.id.properties);
        JSONArray properties = new JSONArray();

        int k = 0;
        Log.d(LOG_TAG, String.valueOf(propertiesLL.getChildCount()));

        for (int i = 0; i < propertiesLL.getChildCount(); i++) {
            View v = propertiesLL.getChildAt(i);
            LinearLayout wrapLL = (LinearLayout) v;
            Spinner prop = (Spinner) wrapLL.getChildAt(1);

            try {
                properties.put(k++, String.valueOf(prop.getSelectedItemPosition()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            tempValues.put("properties", properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EditText productDescriptionET = (EditText) findViewById(R.id.product_description);
        String productDescription = productDescriptionET.getText().toString();
        try {
            tempValues.put("description", productDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, tempValues.toString());
        try {
            tempValues.put("photo", "photo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView categoryID = (TextView) findViewById(R.id.category_id);

        try {
            tempValues.put("category_id", categoryID.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (productAction.getText().toString().equals("Добавить")) {
            new AddProductsTask().execute(tempValues);
        } else {
            new EditProductsTask().execute(tempValues);
        }
    }


    /* @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            new CategoryTask().execute();
        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_adverts) {
            new AdvertsTask().execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AdvertsTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://deuxbit.ru/android/adverts/");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            String id = "";
            String text = "";
            String status = "";

            try {
                dataJsonObj = new JSONObject(strJson);

                JSONArray adverts = dataJsonObj.getJSONArray("adverts");
                LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
                layout.removeAllViews();

                for (int i = 0; i < adverts.length(); i++) {
                    TextView idTitleTextView = new TextView(getApplicationContext());
                    idTitleTextView.setText("ID: ");
                    idTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);

                    TextView textTitleTextView = new TextView(getApplicationContext());
                    textTitleTextView.setText("Текст: ");
                    textTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);

                    TextView statusTitleTextView = new TextView(getApplicationContext());
                    statusTitleTextView.setText("Статус: ");
                    statusTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);

                    LinearLayout tempLL = new LinearLayout(getApplicationContext());
                    tempLL.setOrientation(LinearLayout.VERTICAL);


                    LinearLayout idLL = new LinearLayout(getApplicationContext());
                    idLL.setOrientation(LinearLayout.HORIZONTAL);

                    TextView idTextView = new TextView(getApplicationContext());

                    LinearLayout textLL = new LinearLayout(getApplicationContext());
                    textLL.setOrientation(LinearLayout.HORIZONTAL);

                    TextView textTextView = new TextView(getApplicationContext());

                    LinearLayout statusLL = new LinearLayout(getApplicationContext());
                    statusLL.setOrientation(LinearLayout.HORIZONTAL);
                    TextView statusTextView = new TextView(getApplicationContext());

                    JSONObject advert = adverts.getJSONObject(i);

                    id = advert.getString("id");
                    Log.d(LOG_TAG, "ID: " + id);

                    idTextView.setText(id);

                    text = advert.getString("text");
                    Log.d(LOG_TAG, "text: " + text);
                    textTextView.setText(text);

                    status = advert.getString("status");
                    Log.d(LOG_TAG, "status: " + status);
                    statusTextView.setText(status);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 40);
                    tempLL.setLayoutParams(lp);
                    tempLL.setPadding(10, 10, 10, 10);

                    idLL.addView(idTitleTextView);
                    idLL.addView(idTextView);

                    textLL.addView(textTitleTextView);
                    textLL.addView(textTextView);

                    statusLL.addView(statusTitleTextView);
                    statusLL.addView(statusTextView);

                    tempLL.addView(idLL);
                    tempLL.addView(textLL);
                    tempLL.addView(statusLL);

                    tempLL.setBackgroundColor(Color.parseColor("#cce5ff"));
                    layout.addView(tempLL);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CategoryTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://deuxbit.ru/android/category/");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            boolean isActive;
            String name = "";
            int id;

            try {
                dataJsonObj = new JSONObject(strJson);

                JSONArray categories = dataJsonObj.getJSONArray("categories");

                LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
                layout.removeAllViews();

                for (int i = 0; i < categories.length(); i++) {
                    final Button nameButton = new Button(getApplicationContext());

                    JSONObject category = categories.getJSONObject(i);

                    isActive = category.getBoolean("isActive");
                    Log.d(LOG_TAG, "isActive: " + isActive);

                    if (isActive) {
                        name = category.getString("categoryName");
                        id = category.getInt("id");
                        Log.d(LOG_TAG, "name: " + name);
                        Log.d(LOG_TAG, "id: " + id);
                        nameButton.setText(name);
                        nameButton.setBackgroundColor(Color.parseColor("#f5f5f5"));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, 0, 0, 12);
                        nameButton.setLayoutParams(lp);
                        layout.addView(nameButton);
                        final int finalId = id;
                        nameButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                new ProductsTask().execute(finalId);
                            }
                        });
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class AddProductsTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... values) {

            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL("https://deuxbit.ru/android/add_product/").openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                JSONObject product = new JSONObject();
                product.put("product", values[0]);

                wr.writeBytes(product.toString());
                wr.flush();
                wr.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    private class EditProductsTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... values) {

            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL("https://deuxbit.ru/android/update_product/").openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                JSONObject product = new JSONObject();
                product.put("product", values[0]);

                wr.writeBytes(product.toString());
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    private class DeleteProductsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... values) {

            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL("https://deuxbit.ru/android/delete_product/" + values[0]).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    private class GetProductsTask extends AsyncTask<Integer, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        int needID = -1;

        @Override
        protected String doInBackground(Integer... id) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://deuxbit.ru/android/products/" + id[0].toString());
                Log.d(LOG_TAG, "https://deuxbit.ru/android/products/" + id[0].toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            needID = id[1];
            Log.d("NeedID", id[0].toString());
            Log.d("NeedID", String.valueOf(needID));
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {

            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            Log.d(LOG_TAG, strJson);

            JSONObject dataJsonObj = null;
            boolean isActive;
            String name = "";
            String price = "";
            String description = "";
            String code = "";


            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray products = dataJsonObj.getJSONArray("products");
                Log.d(LOG_TAG, "Products: " + products);

                EditText nameEditText = (EditText) findViewById(R.id.product_name);
                EditText priceEditText = (EditText) findViewById(R.id.product_price);
                EditText descriptionEditText = (EditText) findViewById(R.id.product_description);
                EditText codeEditText = (EditText) findViewById(R.id.product_code);

                JSONObject product = products.getJSONObject(needID);
                name = product.getString("productName");
                price = product.getString("productPrice");
                description = product.getString("productDescription");
                code = product.getString("productCode");
                JSONArray properties = product.getJSONArray("productProperties");

                nameEditText.setText(name);
                priceEditText.setText(price);
                descriptionEditText.setText(description);
                codeEditText.setText(code);

                LinearLayout propertiesLL = (LinearLayout) findViewById(R.id.properties);
                for(int i = 0; i < propertiesLL.getChildCount(); i++) {
                    LinearLayout wrapProp =  (LinearLayout) propertiesLL.getChildAt(i);
                    Spinner prop = (Spinner) wrapProp.getChildAt(1);
                    prop.setSelection(properties.getInt(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ProductsTask extends AsyncTask<Integer, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Integer... id) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://deuxbit.ru/android/products/" + id[0].toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {

            super.onPostExecute(strJson);

            JSONObject dataJsonObj = null;
            boolean isActive;
            String name = "";
            String price = "";
            String description = "";
            String productPhoto = "";


            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray products = dataJsonObj.getJSONArray("products");
                Log.d(LOG_TAG, "Products: " + products);
                final LinearLayout layout = findViewById(R.id.main_layout);
                layout.removeAllViews();
                 final int categoryID;
                final FloatingActionButton fab = findViewById(R.id.fab);
                final JSONObject finalDataJsonObj1 = dataJsonObj;
                JSONArray category = null;
                category = finalDataJsonObj1.getJSONArray("cat");
                categoryID = category.getJSONObject(0).getInt("id");

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fab.setVisibility(View.GONE);
                        LinearLayout mainLL = (LinearLayout) findViewById(R.id.main_layout);
                        mainLL.setVisibility(View.GONE);

                        LinearLayout modalLL = (LinearLayout) findViewById(R.id.modal_product);
                        modalLL.setVisibility(View.VISIBLE);
                        LinearLayout propertiesLL = (LinearLayout) findViewById(R.id.properties);
                        propertiesLL.removeAllViews();
                        String[] answersString;

                        JSONArray category = null;
                        try {
                            category = finalDataJsonObj1.getJSONArray("cat");
                            TextView categoryIDTextView = (TextView) findViewById(R.id.category_id);
                            String categoryID = category.getJSONObject(0).getString("id");
                            categoryIDTextView.setText(categoryID);

                            JSONArray categoryProperties = category.getJSONObject(0).getJSONArray("categoryProperties");
                            for (int j = 0; j < categoryProperties.length(); j++) {
                                LinearLayout tempLL = new LinearLayout(getApplicationContext());
                                tempLL.setOrientation(LinearLayout.HORIZONTAL);
                                tempLL.setWeightSum(10);

                                JSONObject catProp = categoryProperties.getJSONObject(j);
                                JSONArray answers = catProp.getJSONArray("answers");
                                answersString = new String[answers.length()];
                                for (int i = 0; i < answers.length(); i++) {
                                    answersString[i] = answers.getString(i);
                                }

                                TextView nameTextView = new TextView(getApplicationContext());
                                nameTextView.setText(catProp.getString("name"));

                                Spinner property = new Spinner(getApplicationContext());
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, answersString);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                property.setAdapter(adapter);

                                LinearLayout.LayoutParams tvlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
                                LinearLayout.LayoutParams spinlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 7);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                lp.setMargins(0, 0, 0, 12);

                                nameTextView.setLayoutParams(tvlp);
                                property.setLayoutParams(spinlp);

                                tempLL.addView(nameTextView);
                                tempLL.addView(property);
                                tempLL.setLayoutParams(lp);
                                propertiesLL.addView(tempLL);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                fab.setVisibility(View.VISIBLE);

                for (int i = 0; i < products.length(); i++) {
                    LinearLayout tempLL = new LinearLayout(getApplicationContext());
                    tempLL.setOrientation(LinearLayout.VERTICAL);

                    GradientDrawable border = new GradientDrawable();
                    border.setColor(0xFFFFFFFF); //white background
                    border.setStroke(2, 0x80808000); //black border with full opacity
                    tempLL.setBackground(border);
                    tempLL.setPadding(12, 12, 12, 12);

                    ImageView tempImage = new ImageView(getApplicationContext());
                    TextView nameTextView = new TextView(getApplicationContext());
                    TextView priceTextView = new TextView(getApplicationContext());
                    TextView descriptionTextView = new TextView(getApplicationContext());


                    JSONObject product = products.getJSONObject(i);
                    name = product.getString("productName");
                    productPhoto = product.getString("productPhoto");
                    price = product.getString("productPrice");
                    description = product.getString("productDescription");
                    int id = product.getInt("id");
                    new DownloadImageTask(tempImage)
                            .execute("https://img-gorod.ru/upload/iblock/df2/df2758fe524cf4f5f077faae7417c985.jpg");

                    nameTextView.setText(name);
                    nameTextView.setTextColor(Color.parseColor("#000000"));
                    nameTextView.setTextSize(18);
                    nameTextView.setAllCaps(true);

                    priceTextView.setText(price + " руб.");
                    priceTextView.setTextColor(Color.parseColor("#28a745"));
                    priceTextView.setTextSize(16);

                    descriptionTextView.setText(description);
                    descriptionTextView.setTextColor(Color.parseColor("#000000"));
                    descriptionTextView.setTextSize(16);

                    JSONArray productProperties = product.getJSONArray("productProperties");
                    String[] productPropertiesString = new String[productProperties.length()];

                    JSONArray categoryProperties = category.getJSONObject(0).getJSONArray("categoryProperties");

                    TextView[] productPropertiesTextViews = new TextView[productPropertiesString.length];
                    tempLL.addView(tempImage);
                    tempLL.addView(nameTextView);
                    tempLL.addView(priceTextView);
                    tempLL.addView(descriptionTextView);

                    LinearLayout propertiesLL = new LinearLayout(getApplicationContext());
                    propertiesLL.setOrientation(LinearLayout.HORIZONTAL);
                    for (int j = 0; j < categoryProperties.length(); j++) {
                        JSONObject catProp = categoryProperties.getJSONObject(j);
                        JSONArray answers = catProp.getJSONArray("answers");
                        productPropertiesString[j] = answers.getString(productProperties.getInt(j));

                        productPropertiesTextViews[j] = new TextView(getApplicationContext());
                        productPropertiesTextViews[j].setText(productPropertiesString[j]);
                        productPropertiesTextViews[j].setTextColor(Color.parseColor("#6c757d"));
                        productPropertiesTextViews[j].setTextSize(16);
                        productPropertiesTextViews[j].setPadding(0, 0, 15, 0);

                        propertiesLL.addView(productPropertiesTextViews[j]);
                    }

                    tempLL.addView(propertiesLL);

                    Button editButton = new Button(getApplicationContext());
                    Button deleteButton = new Button(getApplicationContext());

                    editButton.setText("Редактировать");
                    editButton.setAllCaps(true);
                    editButton.setBackgroundColor(Color.parseColor("#0062cc"));
                    editButton.setTextColor(Color.parseColor("#ffffff"));
                    editButton.setId(id);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 12, 0, 12);
                    editButton.setLayoutParams(lp);

                    deleteButton.setText("Удалить");
                    deleteButton.setAllCaps(true);
                    deleteButton.setBackgroundColor(Color.parseColor("#dc3545"));
                    deleteButton.setTextColor(Color.parseColor("#ffffff"));
                    deleteButton.setId(id);

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            new DeleteProductsTask().execute(String.valueOf(view.getId()));
                            LinearLayout parent = (LinearLayout) view.getParent();
                            layout.removeView(parent);
                        }
                    });

                    editButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            LinearLayout mainLL = (LinearLayout) findViewById(R.id.main_layout);
                            mainLL.setVisibility(View.GONE);

                            LinearLayout modalLL = (LinearLayout) findViewById(R.id.modal_product);
                            modalLL.setVisibility(View.VISIBLE);

                            int productID = Integer.parseInt(String.valueOf(view.getId())) - 1;
                            new GetProductsTask().execute(categoryID, productID);
                            Button productAction = (Button) findViewById(R.id.product_action);
                            productAction.setText("Применить");
                            productAction(view);
                        }
                    });

                    tempLL.addView(editButton);
                    tempLL.addView(deleteButton);
                    layout.addView(tempLL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}