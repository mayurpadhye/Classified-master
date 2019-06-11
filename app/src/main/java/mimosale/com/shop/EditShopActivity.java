package mimosale.com.shop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.iceteck.silicompressorr.SiliCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.ImagePickerActivity;
import mimosale.com.R;
import mimosale.com.helperClass.CustomFileUtils;
import mimosale.com.helperClass.CustomPermissions;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.fragments.AllProductPojo;
import mimosale.com.map.MapsActivity;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.shop.adapter.ShopImageDetailsAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static mimosale.com.helperClass.CustomPermissions.MY_PERMISSIONS_REQUEST_LOCATION;
import static mimosale.com.helperClass.CustomUtils.IMAGE_LIMIT;

public class EditShopActivity extends AppCompatActivity implements View.OnClickListener {
    public static ArrayList<File> imageFiles;
    Button btn_upload;
    String pref_id1 = "";
    RecyclerView rv_images;
    RadioGroup radioGroup;
    Switch switchbutton_discount, switchbutton_pincode;
    LinearLayout ll_shop_details, ll_pricing, ll_address_details, ll_others;
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    boolean ll_shop_visible = true;
    boolean ll_pricing_visible = false;
    boolean ll_address_visible = false;
    boolean ll_other_visible = false;
    TextView tv_url;
    String intet_from = "";
    ProgressDialog progressDialog;
    LinearLayout ll_discount;
  public static  ArrayList<ImageVideoData> image_thumbnails, video_thumbnails;
    EventImagesAdapter adapter;
    LinearLayoutManager llm_images;
    Button btn_preview;
    Spinner sp_category;
    TextView toolbar_title;
    ImageView iv_back;
    ProgressBar p_bar;
    Button btn_current_address, btn_add_address;
    Context context;
    List<String> allPrefList = new ArrayList<>();
    List<String> allPrefIds = new ArrayList<>();
    String pref_id = "";
    Button btn_save;
    String isUpdate = "";
    private static final int PERMISSION_REQUEST_CODE = 200;
    String lattitude = "", langitude = "";
    double lat_new = 0.0, lon_new = 0.0;
    EditText et_start_date, et_end_date, et_city, et_state, et_country;
    TextView tv_other_details, tv_address_details, tv_pricing_details, tv_shop_details;
    TextInputLayout tl_shop_name, tl_shop_desc, tl_min_price, tl_max_price, tl_pincode, tl_city, tl_address_line1;
    TextInputLayout tl_address_line2, tl_phone_no, tl_hash_tag, tl_url, tl_end_date, tl_start_date;
    EditText et_shop_name, et_shop_desc, et_min_price, et_max_price, et_pincode,
            et_address_line1, et_address_line2, et_phone, et_hash_tag, et_url;//et_min_discount,et_max_discount tl_min_discount, tl_max_discount,
    String shop_id = "";
    Spinner sp_discount;
    String discount = "";
    private String mLastUpdateTime;
    List<File> shopImagesPojoList = new ArrayList<>();
    public static final int REQUEST_IMAGE = 100;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    final Calendar myCalendar = Calendar.getInstance();
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shop);
        context = this;
        initView();
        init();
        restoreValuesFromBundle(savedInstanceState);
        getAllPrefData();
        getShopDeatils();
        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopPostingValidation("preview");


            }
        });
        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    mSettingsClient
                            .checkLocationSettings(mLocationSettingsRequest)
                            .addOnSuccessListener(EditShopActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                    Log.i("ShopPoastingMap", "All location settings are satisfied.");
                                    //noinspection MissingPermission
                                    startActivityForResult(new Intent(EditShopActivity.this, MapsActivity.class), 2);
                                }
                            })
                            .addOnFailureListener(EditShopActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    int statusCode = ((ApiException) e).getStatusCode();
                                    switch (statusCode) {
                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                            Log.i("ShopPoastingMap", "Location settings are not satisfied. Attempting to upgrade " +
                                                    "location settings ");
                                            try {
                                                // Show the dialog by calling startResolutionForResult(), and check the
                                                // result in onActivityResult().
                                                ResolvableApiException rae = (ResolvableApiException) e;
                                                rae.startResolutionForResult(EditShopActivity.this, REQUEST_CHECK_SETTINGS);
                                            } catch (IntentSender.SendIntentException sie) {
                                                Log.i("ShopPoastingMap", "PendingIntent unable to execute request.");
                                            }
                                            break;
                                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                                    "fixed here. Fix in Settings.";
                                            Log.e("ShopPoastingMap", errorMessage);

                                            Toast.makeText(EditShopActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    }

                                    updateLocationUI();
                                }
                            });


                } else
                    checkLocationPermission();

            }
        });
        btn_current_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkLocationPermission()) {
                    init();


                } else {
                    checkLocationPermission();
                }


            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopPostingValidation("update");
            }
        });
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setEndDate();
            }

        };
        et_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        et_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });


        switchbutton_discount.setChecked(false);
        switchbutton_pincode.setChecked(false);
        switchbutton_discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    ll_discount.setVisibility(View.VISIBLE);
                } else {
                    ll_discount.setVisibility(View.GONE);
                }
            }
        });
        switchbutton_pincode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    tl_pincode.setVisibility(View.VISIBLE);
                } else {
                    tl_pincode.setVisibility(View.GONE);
                }
            }
        });

        et_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditShopActivity.this, MapsActivity.class));
            }
        });

        et_pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!com.google.android.libraries.places.api.Places.isInitialized()) {
                    com.google.android.libraries.places.api.Places.initialize(EditShopActivity.this, getResources().getString(R.string.google_maps_key));
                    PlacesClient placesClient = com.google.android.libraries.places.api.Places.createClient(EditShopActivity.this);
                }
                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.REGIONS)
                        .build(EditShopActivity.this);
                intent.putExtra("address", "address");
                startActivityForResult(intent, 1);
                Log.d("sda", String.valueOf(fields));
            }
        });

        sp_discount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (sp_discount.getSelectedItemPosition() == 0) {
                    discount = "";
                } else {

                    String[] dis = sp_discount.getSelectedItem().toString().split(" ", 2);

                    discount = dis[1].substring(0, 2);
                    Toast.makeText(context, "" + discount, Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        if (!discount.equals("")) {
            switchbutton_discount.setChecked(true);
            ll_discount.setVisibility(View.VISIBLE);
        }
        adapter = new EventImagesAdapter(EditShopActivity.this, EditShopActivity.this, image_thumbnails, "update");
        rv_images.setLayoutManager(llm_images);
        rv_images.setAdapter(adapter);
    }

    public void getAllPrefData() {
        try {
            p_bar.setVisibility(View.VISIBLE);
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getAllPreferences(PrefManager.getInstance(context).getUserId(), "Bearer " + PrefManager.getInstance(context).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {

                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int k = 0; k < data.length(); k++) {
                                JSONObject j1 = data.getJSONObject(k);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                allPrefList.add(name);
                                allPrefIds.add(id);
                            }
                            allPrefList.add(0, getResources().getString(R.string.select_category));
                            allPrefIds.add(0, getResources().getString(R.string.select_category));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    EditShopActivity.this,
                                    R.layout.row_language,
                                    allPrefList
                            );


                            sp_category.setAdapter(adapter);
                            if (allPrefIds.contains(pref_id1)) {
                                sp_category.setSelection(allPrefIds.indexOf(pref_id1));
                            }


                        }

                        p_bar.setVisibility(View.GONE);
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    p_bar.setVisibility(View.GONE);
                    Toast.makeText(context, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private void updateLabel() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.JAPAN);

        et_start_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void setEndDate() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.JAPAN);

        et_end_date.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(EditShopActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }


            return false;

        } else {
            return true;
        }
    }

    public void initView() {
        Intent i = getIntent();
        isUpdate = i.getStringExtra("isUpdate");
        btn_current_address = findViewById(R.id.btn_current_address);
        btn_add_address = findViewById(R.id.btn_add_address);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        tv_url = findViewById(R.id.tv_url);
        ll_shop_details = findViewById(R.id.ll_shop_details);
        sp_discount = findViewById(R.id.sp_discount);
        et_state = findViewById(R.id.et_state);
        et_country = findViewById(R.id.et_country);
        et_city = findViewById(R.id.et_city);
        ll_pricing = findViewById(R.id.ll_pricing);
        ll_address_details = findViewById(R.id.ll_address_details);
        ll_others = findViewById(R.id.ll_others);
        tv_other_details = findViewById(R.id.tv_other_details);
        tv_shop_details = findViewById(R.id.tv_shop_details);
        tv_address_details = findViewById(R.id.tv_address_details);
        tv_pricing_details = findViewById(R.id.tv_pricing_details);
        btn_save = findViewById(R.id.btn_save);
        et_start_date = findViewById(R.id.et_start_date);
        et_end_date = findViewById(R.id.et_end_date);
        radioGroup = (RadioGroup) findViewById(R.id.rg_address);
        radioGroup.clearCheck();
        p_bar = findViewById(R.id.p_bar);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (EditShopActivity.this, android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.discount_array)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_discount.setAdapter(spinnerArrayAdapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    Toast.makeText(EditShopActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    if (rb.getText().equals(getResources().getString(R.string.search_by_pincode))) {
                        tl_pincode.setVisibility(View.VISIBLE);
                    } else {
                        tl_pincode.setVisibility(View.GONE);
                    }

                }

            }
        });

        toolbar_title = findViewById(R.id.toolbar_title);
        sp_category = findViewById(R.id.sp_category);
        iv_back = findViewById(R.id.iv_back);
        tl_pincode = findViewById(R.id.tl_pincode);
        switchbutton_pincode = findViewById(R.id.switchbutton_pincode);
        switchbutton_discount = findViewById(R.id.switchbutton_discount);
        ll_discount = findViewById(R.id.ll_discount);
        toolbar_title.setText(getResources().getString(R.string.shop_posting));
        toolbar_title.setTextColor(getResources().getColor(R.color.black));
        btn_preview = findViewById(R.id.btn_preview);
        imageFiles = new ArrayList<>();
        llm_images = new LinearLayoutManager(getApplicationContext());
        llm_images.setOrientation(LinearLayoutManager.HORIZONTAL);
        progressDialog = new ProgressDialog(EditShopActivity.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        image_thumbnails = new ArrayList<ImageVideoData>();
        adapter = new EventImagesAdapter(EditShopActivity.this, EditShopActivity.this, image_thumbnails, "update");
        video_thumbnails = new ArrayList<>();
        btn_upload = findViewById(R.id.btn_upload);
        rv_images = findViewById(R.id.rv_images);
        tl_shop_name = findViewById(R.id.tl_shop_name);
        tl_shop_desc = findViewById(R.id.tl_shop_desc);
        // tl_min_discount = findViewById(R.id.tl_min_discount);
        //  tl_max_discount = findViewById(R.id.tl_max_discount);
        tl_min_price = findViewById(R.id.tl_min_price);
        tl_max_price = findViewById(R.id.tl_max_price);
        tl_city = findViewById(R.id.tl_city);
        tl_address_line1 = findViewById(R.id.tl_address_line1);
        tl_address_line2 = findViewById(R.id.tl_address_line2);
        tl_phone_no = findViewById(R.id.tl_phone_no);
        tl_hash_tag = findViewById(R.id.tl_hash_tag);
        tl_url = findViewById(R.id.tl_url);
        et_shop_name = findViewById(R.id.et_shop_name);
        et_shop_desc = findViewById(R.id.et_shop_desc);
        //   et_min_discount = findViewById(R.id.et_min_discount);
        //  et_max_discount = findViewById(R.id.et_max_discount);
        et_min_price = findViewById(R.id.et_min_price);
        et_max_price = findViewById(R.id.et_max_price);
        et_pincode = findViewById(R.id.et_pincode);
        et_address_line1 = findViewById(R.id.et_address_line1);
        et_address_line2 = findViewById(R.id.et_address_line2);
        et_phone = findViewById(R.id.et_phone);
        et_hash_tag = findViewById(R.id.et_hash_tag);
        et_url = findViewById(R.id.et_url);
        tl_end_date = findViewById(R.id.tl_end_date);
        tl_start_date = findViewById(R.id.tl_start_date);


        btn_upload.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals(getResources().getString(R.string.select_category))) {
                    pref_id = "";
                } else {
                    pref_id = allPrefIds.get(position);
                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isUpdate.equals("update_shop")) {
            try {

                String shop_name = i.getStringExtra("shop_name");
                String shop_desc = i.getStringExtra("shop_desc");
                String shop_category = i.getStringExtra("shop_category");
                String discount = i.getStringExtra("discount");
                String start_date = i.getStringExtra("start_date");
                String end_date = i.getStringExtra("end_date");
                String min_price = i.getStringExtra("min_price");
                String max_price = i.getStringExtra("max_price");
                String pincode = i.getStringExtra("pincode");
                String city = i.getStringExtra("city");
                String state = i.getStringExtra("state");
                String country = i.getStringExtra("country");
                String address_line_1 = i.getStringExtra("address_line_1");
                String address_line_2 = i.getStringExtra("address_line_2");
                String phone_number = i.getStringExtra("phone_number");
                String hash_tag = i.getStringExtra("hash_tag");
                String web_url = i.getStringExtra("web_url");
                pref_id1 = i.getStringExtra("pref_id");
                shop_id = i.getStringExtra("shop_id");
                List<String> stringList = new ArrayList<>();
                List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.discount_array));
                if (!discount.equals("")) {
                    switchbutton_discount.setChecked(true);
                    ll_discount.setVisibility(View.VISIBLE);
                    for (int k = 0; k < Lines.size(); k++) {
                        if (Lines.get(k).contains(discount)) {
                            sp_discount.setSelection(k);
                            break;
                        }
                    }
                }
                et_shop_name.setText(shop_name);
                et_shop_desc.setText(shop_desc);
                if (!max_price.equals("null")) {
                    et_max_price.setText(max_price);
                }
                if (!min_price.equals("null")) {
                    et_min_price.setText(min_price);
                }
                et_start_date.setText(start_date);

                et_city.setText(city);
                et_address_line1.setText(address_line_1);
                if(!address_line_2.equals("null"))
                et_address_line2.setText(address_line_2);
                et_phone.setText(phone_number);
                et_hash_tag.setText(hash_tag);
                String webUrl = web_url.substring(0, 7);
                et_url.setText(webUrl);
                et_state.setText(state);
                et_country.setText(country);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void init() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                // updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        startLocationUpdates();
    }

    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("ShopPoastingMap", "All location settings are satisfied.");


                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        find_Location();
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("ShopPoastingMap", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(EditShopActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("ShopPoastingMap", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("ShopPoastingMap", errorMessage);

                                Toast.makeText(EditShopActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void shopPostingValidation(String type) {

        if (et_shop_name.getText().toString().trim().length() == 0) {
            if (!ll_shop_visible)
                slideDown(ll_shop_details);
            et_shop_name.requestFocus();
            tl_shop_name.setError(getResources().getString(R.string.enter_shop_name));
            return;
        } else {
            tl_shop_name.setError(null);
        }

        if (et_shop_desc.getText().toString().trim().length() == 0) {
            if (!ll_shop_visible)
                slideDown(ll_shop_details);
            et_shop_desc.requestFocus();
            tl_shop_desc.setError(getResources().getString(R.string.shop_desc_error));

            return;
        } else {
            tl_shop_desc.setError(null);
        }
        if (pref_id.equals(getResources().getString(R.string.select_category))) {
            if (!ll_shop_visible)
                slideDown(ll_shop_details);
            Toast.makeText(context, getResources().getString(R.string.please_select_pref_category), Toast.LENGTH_SHORT).show();
            return;
        }

        if (et_min_price.getText().toString().trim().length() == 0) {
            if (!ll_pricing_visible)
                slideDown(ll_pricing);
            et_min_price.requestFocus();
            tl_min_price.setError(getResources().getString(R.string.min_price_error));
            return;
        } else {
            tl_min_price.setError(null);
        }

        if (et_max_price.getText().toString().trim().length() == 0) {
            if (!ll_pricing_visible)
                slideDown(ll_pricing);
            et_max_price.requestFocus();
            tl_max_price.setError(getResources().getString(R.string.max_price_error));
            return;
        } else {
            tl_max_price.setError(null);
        }

        if (!et_min_price.getText().toString().trim().isEmpty() && !et_max_price.getText().toString().trim().isEmpty()) {
            if (Integer.parseInt(et_min_price.getText().toString().trim()) > Integer.parseInt(et_max_price.getText().toString().trim())) {
                showErrorDialog("Oops", getResources().getString(R.string.min_price_greater));
                return;
            } else if (Integer.parseInt(et_max_price.getText().toString().trim()) < Integer.parseInt(et_min_price.getText().toString().trim())) {
                showErrorDialog("Oops", getResources().getString(R.string.max_price_smaller));
                return;

            }
        }

        if (et_pincode.getText().toString().trim().length() == 0) {
            if (!ll_address_visible)
                slideDown(ll_address_details);
            et_pincode.requestFocus();
            tl_pincode.setError(getResources().getString(R.string.enter_pincode));
            return;

        } else {
            tl_pincode.setError(null);
        }

        if (et_city.getText().toString().trim().length() == 0) {
            et_city.requestFocus();
            tl_city.setError(getResources().getString(R.string.enter_city));
            return;
        } else {
            tl_city.setError(null);
        }

        if (et_address_line1.getText().toString().trim().length() == 0) {
            if (!ll_address_visible)
                slideDown(ll_address_details);
            et_address_line1.requestFocus();
            tl_address_line1.setError(getResources().getString(R.string.enter_address));
            return;

        } else {
            tl_address_line1.setError(null);
        }

        if (!isValidMobile(et_phone.getText().toString().trim())) {
            if (!ll_address_visible)
                slideDown(ll_address_details);
            et_phone.requestFocus();
            if (et_phone.getText().toString().trim().length() == 0) {
                tl_phone_no.setError(getResources().getString(R.string.enter_mobile_no));
            } else {
                tl_phone_no.setError(getResources().getString(R.string.mobile_error));
            }

            return;
        }



        if (imageFiles.size() <= 1) {
            if (!ll_shop_visible)
                slideDown(ll_shop_details);
            Toast.makeText(context, "" + getResources().getString(R.string.please_select_atleast_two_images), Toast.LENGTH_SHORT).show();
            return;
        }

        if (sp_discount.getSelectedItemPosition() != 0) {
            if (!ll_pricing_visible)
                slideDown(ll_pricing);
           /* if (et_min_discount.getText().toString().trim().length() > et_max_discount.getText().toString().trim().length()) {
                tl_min_discount.setError("" + getResources().getString(R.string.min_discount_error));
                return;
            } else if (et_max_discount.getText().toString().trim().length() < et_min_discount.getText().toString().trim().length()) {
                tl_max_discount.setError("" + getResources().getString(R.string.max_dis_error));
                return;
            }*/
            if (et_start_date.getText().toString().trim().length() != 0 || et_end_date.getText().toString().trim().length() != 0) {

                if (et_start_date.getText().toString().trim().length() == 0) {
                    tl_start_date.setError("" + getResources().getString(R.string.enter_start_date));
                    return;
                } else if (et_end_date.getText().toString().trim().length() == 0) {
                    tl_end_date.setError(getResources().getString(R.string.enter_end_date));
                    return;
                }

                if (!isDateAfter(et_start_date.getText().toString().trim(), et_end_date.getText().toString().trim())) {
                    tl_start_date.setError(getResources().getString(R.string.end_date_must_be_greater));
                    return;
                }
            }

        }

        if (type.equals("update")) {

            updateShopDetails();

        } else {
            Intent i = new Intent(EditShopActivity.this, ShopPostingPreviewNew.class);
            i.putExtra("shop_name", et_shop_name.getText().toString());
            i.putExtra("type", "update");
           /* JsonArray jsonElements = (JsonArray) new Gson().toJsonTree(image_thumbnails);
            i.putExtra("shop_images", jsonElements.toString());*/
            i.putExtra("shop_desc", et_shop_desc.getText().toString());
            i.putExtra("shop_category", sp_category.getSelectedItem().toString());
            i.putExtra("min_discount", "");
            i.putExtra("max_discount", "");
            if (sp_discount.getSelectedItemPosition() != 0)
                i.putExtra("discount", discount);
            else
                i.putExtra("discount", "");
            i.putExtra("start_date", et_start_date.getText().toString());
            i.putExtra("lati", ""+lat_new);
            i.putExtra("longi", ""+lon_new);
            i.putExtra("end_date", et_end_date.getText().toString());
            i.putExtra("min_price", et_min_price.getText().toString());
            i.putExtra("max_price", et_max_price.getText().toString());
            i.putExtra("pincode", et_pincode.getText().toString());
            i.putExtra("city", et_city.getText().toString());
            i.putExtra("address_line_1", et_address_line1.getText().toString());
            i.putExtra("address_line_2", et_address_line2.getText().toString());
            i.putExtra("phone_number", et_phone.getText().toString());
            i.putExtra("hash_tag", et_hash_tag.getText().toString());
            i.putExtra("web_url", et_url.getText().toString());
            i.putExtra("pref_id", pref_id);
            i.putExtra("state", et_state.getText().toString());
            i.putExtra("country", et_country.getText().toString());
            i.putExtra("lat", lattitude);
            i.putExtra("shop_id", shop_id);
            i.putExtra("lan", langitude);
           /* JsonArray jsonElements1 = (JsonArray) new Gson().toJsonTree(imageFiles);
            i.putExtra("image_thumbnail", jsonElements1.toString());*/
            startActivity(i);
        }


    }

    public void getShopDeatils() {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getShopDetails(shop_id, PrefManager.getInstance(EditShopActivity.this).getUserId(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {
                        pDialog.dismiss();


                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                String preference_id = j1.getString("preference_id");
                                String address_line1 = j1.getString("address_line1");
                                String address_line2 = j1.getString("address_line2");
                                String city = j1.getString("city");
                                String state = j1.getString("state");
                                String country = j1.getString("country");
                                String pincode = j1.getString("pincode");
                                String lat = j1.getString("lat");
                                String lon = j1.getString("lon");
                                String low_price = j1.getString("low_price");
                                String high_price = j1.getString("high_price");
                                String discount = j1.getString("discount");
                                String start_date = j1.getString("start_date");
                                String end_date = j1.getString("end_date");
                                String followStatus = j1.getString("followStatus");
                                String phone = j1.getString("phone");
                                String hash_tags = j1.getString("hash_tags");
                                String description = j1.getString("description");
                                String web_url = j1.getString("web_url");
                                JSONArray shop_images = j1.getJSONArray("shop_images");

                                et_end_date.setText(end_date);
                                et_start_date.setText(start_date);
                                et_pincode.setText(pincode);
                                shopImagesPojoList.clear();
                                 if(!lat.equals("null"))
                                 {
                                     lat_new= Double.parseDouble(lat);
                                     lon_new= Double.parseDouble(lon);
                                 }


                                ///    expTv1.setText(description);
                                ArrayList<String> selected_image = new ArrayList<>();
                                if (shop_images.length() > 0) {

                                    for (int j = 0; j < shop_images.length(); j++) {

                                        JSONObject j2 = shop_images.getJSONObject(j);
                                        String image_id = j2.getString("id");
                                        String image = j2.getString("image");

                                        selected_image.add(WebServiceURLs.SHOP_IMAGE + image);


                                    }
                                    new ImageCompressAsyncTask1(context).execute(selected_image);
                                       /* tv_photos.setText("" + shop_images.length() + " " + getResources().getString(R.string.photos));
                                        ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(EditShopActivity.this, shopImagesPojoList);
                                        mPager.setAdapter(bannerAdapter);*/


                                }

                                ll_address_details.setVisibility(View.VISIBLE);
                                et_pincode.setText(pincode);
                                et_city.setText(city);
                                et_state.setText(state);
                                et_address_line1.setText(address_line1);
                                if (!address_line2.equals("null"))
                                et_address_line2.setText(address_line2);
                                et_phone.setText(phone);


                                JSONArray products = j1.getJSONArray("products");

                                if (products.length() > 0) {
                                    for (int k = 0; k < products.length(); k++) {
                                        JSONObject j2 = products.getJSONObject(k);
                                        String p_id = j2.getString("id");
                                        String p_name = j2.getString("name");
                                        String p_shop_id = j2.getString("shop_id");
                                        String p_user_id = j2.getString("user_id");
                                        String p_description = j2.getString("description");
                                        String p_price = j2.getString("price");
                                        String p_hash_tag = j2.getString("hash_tags");
                                        String status1 = j2.getString("status");
                                        String image1 = "";
                                        String image2 = "";
                                        if (j2.has("image1")) {
                                            image1 = j2.getString("image1");
                                        }
                                        if (j2.has("image2")) {
                                            image2 = j2.getString("image2");
                                        }


                                        String image = "";


                                    }


                                }


                            }
                        }


                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Log.i("detailsException", "" + e.toString());

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(EditShopActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());


        }

    }

    public void updateShopDetails() {
        try {


            PrefManager.getInstance(context).getUserId();
            p_bar.setVisibility(View.VISIBLE);

            MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
            multipartTypedOutput.addPart("name", new TypedString(et_shop_name.getText().toString().trim()));
            multipartTypedOutput.addPart("preference_id", new TypedString(pref_id));
            multipartTypedOutput.addPart("address_line1", new TypedString(et_address_line1.getText().toString().trim()));
            multipartTypedOutput.addPart("address_line2", new TypedString(et_address_line2.getText().toString().trim()));
            multipartTypedOutput.addPart("city", new TypedString(et_city.getText().toString()));
            multipartTypedOutput.addPart("state", new TypedString(et_state.getText().toString()));
            multipartTypedOutput.addPart("country", new TypedString(et_country.getText().toString()));
            multipartTypedOutput.addPart("pincode", new TypedString(et_pincode.getText().toString()));
            multipartTypedOutput.addPart("lat", new TypedString(""+lat_new));
            multipartTypedOutput.addPart("lon", new TypedString(""+lon_new));
            multipartTypedOutput.addPart("low_price", new TypedString(et_min_price.getText().toString()));
            multipartTypedOutput.addPart("high_price", new TypedString(et_max_price.getText().toString()));
            multipartTypedOutput.addPart("min_discount", new TypedString(""));
            multipartTypedOutput.addPart("max_discount", new TypedString(""));
            multipartTypedOutput.addPart("discount", new TypedString(discount));
            multipartTypedOutput.addPart("phone", new TypedString(et_phone.getText().toString()));
            multipartTypedOutput.addPart("hash_tags", new TypedString(et_hash_tag.getText().toString()));
            multipartTypedOutput.addPart("description", new TypedString(et_shop_desc.getText().toString()));
            multipartTypedOutput.addPart("web_url", new TypedString(tv_url.getText().toString() + "" + et_url.getText().toString()));
            multipartTypedOutput.addPart("status", new TypedString("1"));
            multipartTypedOutput.addPart("user_id", new TypedString(PrefManager.getInstance(context).getUserId()));
            multipartTypedOutput.addPart("shop_id", new TypedString(shop_id));
            multipartTypedOutput.addPart("start_date", new TypedString(et_start_date.getText().toString().trim()));
            multipartTypedOutput.addPart("end_date", new TypedString(et_end_date.getText().toString().trim()));


            if (imageFiles.size() > 0) {
                for (int i = 0; i < imageFiles.size(); i++) {
                    multipartTypedOutput.addPart("shop_photos[]", new TypedFile("application/octet-stream", new File(imageFiles.get(i).getAbsolutePath())));
                }
            } else {
                multipartTypedOutput.addPart("shop_photos", new TypedString(""));
            }


            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.update_shop("Bearer " + PrefManager.getInstance(context).getApiToken(),
                    multipartTypedOutput, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            //this method call if webservice success
                            try {
                                JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                String status = jsonObject.getString("status");

                                if (status.equals("1")) {

                                    Toast.makeText(context, "Shop Successfully Updated", Toast.LENGTH_SHORT).show();

                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getResources().getString(R.string.success))
                                            .setContentText(getResources().getString(R.string.shop_posting_success))
                                            .setConfirmText(getResources().getString(R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    finish();
                                                }
                                            })
                                            .show();

                                } else {
                                    Toast.makeText(context, "Unable to update Shop", Toast.LENGTH_SHORT).show();

                                }

                                p_bar.setVisibility(View.GONE);
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            p_bar.setVisibility(View.GONE);
                            Toast.makeText(context, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                            Log.i("fdfdfdfdfdf", "" + error.getMessage());

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public class ImageCompressAsyncTask1 extends AsyncTask<List<String>, String, String> {
        Context mContext;
        Uri selectedImage;

        public ImageCompressAsyncTask1(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditShopActivity.this);
            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            //  progressDialog.show();
        }

        @Override
        protected String doInBackground(List<String>... paths) {
            //final ArrayList<String> filePaths=
            //getImages(paths[0]);
            progressDialog.dismiss();
            for (String filePaths : paths[0]) {
                //   final int finalI = i;
                String filePath = null;
                try {
                    URL url = new URL(filePaths);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);


                    ImageVideoData image_v = new ImageVideoData();
                    image_v.setBitmap(myBitmap);
                    image_v.setPath(filePaths);
                    image_thumbnails.add(image_v);
                    File file = getDownloadFile(myBitmap);

                    if (file == null)


                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();

                            // here you can get file in "file" variable.
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    imageFiles.add(file);

                 /*   filePath = SiliCompressor.with(EditShopActivity.this).compress(filePaths, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/images"));
                    File imageFile = new File(filePath);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bt = bos.toByteArray();
                    String encodedImageString1 = Base64.encodeToString(bt, Base64.DEFAULT);
                    byte[] decodedString1 = Base64.decode(encodedImageString1.getBytes(), Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
                    ImageVideoData image_v = new ImageVideoData();
                    image_v.setBitmap(decodedByte);
                    image_v.setPath(filePath);
                    image_thumbnails.add(image_v);
                    imageFiles.add(imageFile);
                    Thread.sleep(500);
                    progressDialog.dismiss();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "";
        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog.dismiss();

            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public File getDownloadFile(Bitmap bitmap1) {

        OutputStream output;

        // Retrieve the image from the res folder

        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/Save Image Tutorial/");
        dir.mkdirs();
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        // Create a name for the saved image
        File file = new File(dir, ts + ".png");

        // Show a toast message on successful save

        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return file;
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 8 || phone.length() > 15) {
                // if(phone.length() != 10) {
                check = false;
                tl_phone_no.setError(getResources().getString(R.string.not_a_valid_number));
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            String myFormatString = "yyyy/MM/dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else
                return false;
        } catch (Exception e) {

            return false;
        }
    }

    public void slideDown(View view) {

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public void find_Location() {
        Log.d("Find Location", "in find_location");

        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                //   addr = ConvertPointToLocation(latitude, longitude);
                //String temp_c = SendToUrl(addr);
            }
        }
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            lat_new = mCurrentLocation.getLatitude();
            lon_new = mCurrentLocation.getLongitude();
            getLocation(lat_new, lon_new);

            // giving a blink animation on TextView

        }


    }

    public void getLocation(double lat, double lon) {
        Geocoder geocoder = new Geocoder(EditShopActivity.this);

        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (!locality.isEmpty() && !country.isEmpty())
                    //resutText.setText(locality + "  " + country);
                    ll_address_details.setVisibility(View.VISIBLE);
                et_pincode.setText(addressList.get(0).getPostalCode());
                et_city.setText(addressList.get(0).getLocality());
                et_state.setText(addressList.get(0).getAdminArea());
                et_country.setText(addressList.get(0).getCountryName());
                et_address_line1.setText(addressList.get(0).getAddressLine(0));
                lat_new = lat;
                lon_new = lon;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    public void showErrorDialog(String title, String msg) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(msg)

                .show();
    }

    public boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED ;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);

    }

    public boolean checkLocationPer()
    {
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        return result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED ;
    }

    private void requestLocationPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                CustomUtils.hideKeyboard(v, getApplicationContext());
                if (imageFiles.size() >= IMAGE_LIMIT) {
                    CustomUtils.showAlertDialog(EditShopActivity.this, getString(R.string.can_not_share_more_than_five_images));
                } else {



                    if (checkPermission())
                        Dexter.withActivity(this)
                                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        if (report.areAllPermissionsGranted()) {
                                            showImagePickerOptions();
                                        } else {
                                            // TODO - handle permission denied case
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    else
                        requestPermission();
                }
                break;

            case R.id.btn_preview:
                startActivity(new Intent(EditShopActivity.this, ShopPreviewActivity.class));
                //SaveShopPostData();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void selectImage() {
        final String[] items = new String[]{getString(R.string.camera), getString(R.string.gallery)};

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        boolean result_camera = CustomPermissions.checkCameraPermission(EditShopActivity.this);
                        if (result_camera) {
                            cameraImageIntent();
                        }
                        break;

                    case 1:
                        boolean result_gallery = CustomPermissions.checkPermissionForFileAccess(EditShopActivity.this);
                        if (result_gallery) {
                            galleryImageIntent();
                        }
                        break;
                }
            }
        });
        ad.show();
    }

    private void galleryImageIntent() {


        try {
            //Open the specific App Info page:
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select)), CustomPermissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select)), CustomPermissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

      /*  Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select)), CustomPermissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);*/
    }

    private void cameraImageIntent() {

        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            PackageManager pm = getPackageManager();

            final ResolveInfo mInfo = pm.resolveActivity(i, 0);

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivityForResult(i, CustomPermissions.MY_PERMISSIONS_REQUEST_CAMERA);
        } catch (Exception e) {
            Log.i("launch_camera", "Unable to launch camera: " + e);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;



                    if (locationAccepted && StorageAccepted && cameraAccepted)
                        Dexter.withActivity(this)
                                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        if (report.areAllPermissionsGranted()) {
                                            showImagePickerOptions();
                                        } else {
                                            // TODO - handle permission denied case
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                }).check();
                    else {


                    }



                }


                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted)
                {
                    mSettingsClient
                            .checkLocationSettings(mLocationSettingsRequest)
                            .addOnSuccessListener(EditShopActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                                    Log.i("ShopPoastingMap", "All location settings are satisfied.");
                                    //noinspection MissingPermission
                                    startActivityForResult(new Intent(EditShopActivity.this, MapsActivity.class), 2);
                                }
                            })
                            .addOnFailureListener(EditShopActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    int statusCode = ((ApiException) e).getStatusCode();
                                    switch (statusCode) {
                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                            Log.i("ShopPoastingMap", "Location settings are not satisfied. Attempting to upgrade " +
                                                    "location settings ");
                                            try {
                                                // Show the dialog by calling startResolutionForResult(), and check the
                                                // result in onActivityResult().
                                                ResolvableApiException rae = (ResolvableApiException) e;
                                                rae.startResolutionForResult(EditShopActivity.this, REQUEST_CHECK_SETTINGS);
                                            } catch (IntentSender.SendIntentException sie) {
                                                Log.i("ShopPoastingMap", "PendingIntent unable to execute request.");
                                            }
                                            break;
                                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                                    "fixed here. Fix in Settings.";
                                            Log.e("ShopPoastingMap", errorMessage);

                                            Toast.makeText(EditShopActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    }

                                    updateLocationUI();
                                }
                            });

                }

                break;

        }
    }
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }
    private void launchCameraIntent() {
        Intent intent = new Intent(EditShopActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(EditShopActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if (requestCode ==REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    Uri tempUri = CustomUtils.getImageUri(getApplicationContext(), bitmap);
                    File finalFile = new File(CustomUtils.getRealPathFromURI(getApplicationContext(), tempUri));
                    CustomUtils.showLog("Camera File path ", finalFile.getAbsolutePath() + "");
                    if (imageFiles.size() <= CustomUtils.IMAGE_LIMIT) {
                        ArrayList<String> selected_image = new ArrayList<>();
                        selected_image.add(tempUri.toString());
                        new ImageCompressAsyncTask(this).execute(selected_image);
                    } else {
                        CustomUtils.showAlertDialog(EditShopActivity.this, getString(R.string.can_not_share_more_than_five_images));
                    }
                    // loading profile image from local cache
                    //   loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 1:

                    com.google.android.libraries.places.api.model.Place placepick = Autocomplete.getPlaceFromIntent(data);
                    LatLng piclatlng = placepick.getLatLng();

                    Geocoder geocoder = new Geocoder(EditShopActivity.this);

                    try {
                        List<Address> addressList = geocoder.getFromLocation(piclatlng.latitude, piclatlng.longitude, 1);
                        if (addressList != null && addressList.size() > 0) {
                            String locality = addressList.get(0).getAddressLine(0);
                            String country = addressList.get(0).getCountryName();
                            if (!locality.isEmpty() && !country.isEmpty())
                                //resutText.setText(locality + "  " + country);
                                et_pincode.setText(addressList.get(0).getPostalCode());
                            et_city.setText(addressList.get(0).getLocality());
                            et_state.setText(addressList.get(0).getAdminArea());
                            et_country.setText(addressList.get(0).getCountryName());
                            et_address_line1.setText(addressList.get(0).getAddressLine(0));
                            lat_new = piclatlng.latitude;
                            lon_new = piclatlng.longitude;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


            }
        }
        try {
            if (data.hasExtra("address")) {
                intet_from = data.getStringExtra("address");
                if (data.getStringExtra("address").equals("address")) {
                    Geocoder geocoder = new Geocoder(EditShopActivity.this);

                    try {
                        List<Address> addressList = geocoder.getFromLocation(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lon", 0.0), 1);
                        if (addressList != null && addressList.size() > 0) {
                            String locality = addressList.get(0).getAddressLine(0);
                            String country = addressList.get(0).getCountryName();
                            if (!locality.isEmpty() && !country.isEmpty())
                                //resutText.setText(locality + "  " + country);
                                ll_address_details.setVisibility(View.VISIBLE);
                            et_pincode.setText(addressList.get(0).getPostalCode());
                            et_city.setText(addressList.get(0).getLocality());
                            et_state.setText(addressList.get(0).getAdminArea());
                            et_country.setText(addressList.get(0).getCountryName());
                            et_address_line1.setText(addressList.get(0).getAddressLine(0));
                            lat_new = data.getDoubleExtra("lat", 0.0);
                            lon_new = data.getDoubleExtra("lon", 0.0);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class ImageCompressAsyncTask extends AsyncTask<List<String>, String, String> {
        Context mContext;
        Uri selectedImage;

        public ImageCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditShopActivity.this);
            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            //  progressDialog.show();
        }

        @Override
        protected String doInBackground(List<String>... paths) {
            //final ArrayList<String> filePaths=
            //getImages(paths[0]);
            progressDialog.dismiss();
            for (String filePaths : paths[0]) {
                //   final int finalI = i;
                String filePath = null;
                try {
                    filePath = SiliCompressor.with(EditShopActivity.this).compress(filePaths, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/images"));
                    File imageFile = new File(filePath);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bt = bos.toByteArray();
                    String encodedImageString1 = Base64.encodeToString(bt, Base64.DEFAULT);
                    byte[] decodedString1 = Base64.decode(encodedImageString1.getBytes(), Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
                    ImageVideoData image_v = new ImageVideoData();
                    image_v.setBitmap(decodedByte);
                    image_v.setPath(filePath);
                    image_thumbnails.add(image_v);
                    imageFiles.add(imageFile);
                    Thread.sleep(500);
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "";
        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog.dismiss();
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }
}
