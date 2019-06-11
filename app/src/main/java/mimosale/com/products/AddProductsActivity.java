package mimosale.com.products;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mimosale.com.ImagePickerActivity;
import mimosale.com.R;
import mimosale.com.helperClass.CustomFileUtils;
import mimosale.com.helperClass.CustomPermissions;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.HomeFragment;
import mimosale.com.home.preferences.PreferenceListModel;
import mimosale.com.home.preferences.PreferencesListAdapter;
import mimosale.com.map.MapsActivity;
import mimosale.com.my_posting.product_posting.PostingImagesAdapter;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.post.SalePostingActivity;
import mimosale.com.shop.EventImagesAdapter;
import mimosale.com.shop.ImageVideoData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.iceteck.silicompressorr.SiliCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.shop.ShopPostingActivity;
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

public class AddProductsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_IMAGE = 100;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.btn_upload)
    Button btn_upload;
    @BindView(R.id.sp_select_shop)
    Spinner sp_select_shop;
    @BindView(R.id.rv_images)
    RecyclerView rv_images;
    @BindView(R.id.p_bar)
    ProgressBar p_bar;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.tl_hash_tag)
    TextInputLayout tl_hash_tag;
    @BindView(R.id.tl_product_name)
    TextInputLayout tl_product_name;
    @BindView(R.id.tl_desc)
    TextInputLayout tl_desc;
    @BindView(R.id.tl_price)
    TextInputLayout tl_price;
    @BindView(R.id.tl_discount)
    TextInputLayout tl_discount;
    @BindView(R.id.ll_cat)
    LinearLayout ll_cat;
    @BindView(R.id.sp_cat)
    Spinner sp_cat;
    @BindView(R.id.ll_sub_cat)
    LinearLayout ll_sub_cat;
    @BindView(R.id.sp_sub_cat)
    Spinner sp_sub_cat;
    @BindView(R.id.et_brand)
    EditText et_brand;
    @BindView(R.id.et_model_no)
    EditText et_model_no;
    @BindView(R.id.et_color)
    EditText et_color;
    @BindView(R.id.et_size)
    EditText et_size;
    @BindView(R.id.et_specification)
    EditText et_specification;
    @BindView(R.id.et_qty)
    EditText et_qty;
    @BindView(R.id.btn_preview)
    Button btn_preview;
    @BindView(R.id.sp_discount)
    Spinner sp_discount;
    PrefManager pref;
    List<String> shopnameList = new ArrayList<>();
    List<String> shopId = new ArrayList<>();
    public static ArrayList<ImageVideoData> image_thumbnails_product;
    ProgressDialog progressDialog;
    PostingImagesAdapter adapter;
    public static ArrayList<File> imageFiles_products;
    LinearLayoutManager llm_images;
    String shop_id = "";
    Intent i;
    String isUpdate = "";
    EditText et_product_name, et_desc, et_price, et_discount, et_hash_tag;
    String product_id = "";
    List<String> allPrefList = new ArrayList<>();
    List<String> allPrefListId = new ArrayList<>();
    String discount = "";
    String preference_id = "";
    private static final int PERMISSION_REQUEST_CODE = 200;
    @BindView(R.id.tl_coupon_title)
    TextInputLayout tl_coupon_title;
    @BindView(R.id.tl_no_claims)
    TextInputLayout tl_no_claims;
    @BindView(R.id.tl_end_date)
    TextInputLayout tl_end_date;
    @BindView(R.id.tl_start_date)
    TextInputLayout tl_start_date;
    @BindView(R.id.tl_coupon_desc)
    TextInputLayout tl_coupon_desc;
    @BindView(R.id.et_coupon_title)
    EditText et_coupon_title;
    @BindView(R.id.et_no_claims)
    EditText et_no_claims;
    @BindView(R.id.et_coupon_desc)
    EditText et_coupon_desc;

    @BindView(R.id.et_start_date)
    EditText et_start_date;
    @BindView(R.id.et_end_date)
    EditText et_end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        ButterKnife.bind(this);
        i = getIntent();
        initView();
        getUserShop();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (AddProductsActivity.this, android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.discount_array)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_discount.setAdapter(spinnerArrayAdapter);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomUtils.hideKeyboard(v, getApplicationContext());
                if (imageFiles_products.size() >= IMAGE_LIMIT) {
                    CustomUtils.showAlertDialog(AddProductsActivity.this, getString(R.string.can_not_share_more_than_five_images));
                } else {
                    if (checkPermission())
                        Dexter.withActivity(AddProductsActivity.this)
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
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductValidte("save");
            }
        });

        et_desc.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 120) {
                    et_desc.setError(getResources().getString(R.string.maximum_char_limit));
                } else {
                    tl_desc.setError(null);
                }

            }
        });
        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductValidte("preview");
            }
        });

        sp_select_shop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!sp_select_shop.getSelectedItem().equals(getResources().getString(R.string.select_shop))) {
                    shop_id = shopId.get(position);
                } else {
                    shop_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
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
                    Toast.makeText(AddProductsActivity.this, "" + discount, Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        sp_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sp_cat.getSelectedItemPosition() != 0) {
                    preference_id = allPrefListId.get(position);
                } else {
                    preference_id = "";

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);

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
                        Dexter.withActivity(AddProductsActivity.this)
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
        Intent intent = new Intent(AddProductsActivity.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(AddProductsActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    private void initView() {
        pref = PrefManager.getInstance(AddProductsActivity.this);
        et_product_name = findViewById(R.id.et_product_name);
        et_desc = findViewById(R.id.et_desc);
        et_discount = findViewById(R.id.et_discount);
        et_price = findViewById(R.id.et_price);
        et_hash_tag = findViewById(R.id.et_hash_tag);
        imageFiles_products = new ArrayList<>();
        llm_images = new LinearLayoutManager(getApplicationContext());
        llm_images.setOrientation(LinearLayoutManager.HORIZONTAL);
        image_thumbnails_product = new ArrayList<ImageVideoData>();
        adapter = new PostingImagesAdapter(AddProductsActivity.this, AddProductsActivity.this, image_thumbnails_product, "create");
        rv_images.setLayoutManager(llm_images);
        rv_images.setAdapter(adapter);
        toolbar_title.setText(getResources().getString(R.string.add_products));
        iv_back.setOnClickListener(this);
        sp_select_shop = findViewById(R.id.sp_select_shop);
        btn_upload = findViewById(R.id.btn_upload);
        toolbar_title.setText("" + getResources().getString(R.string.product_posting));
        toolbar_title.setTextColor(getResources().getColor(R.color.black));
        isUpdate = i.getStringExtra("isUpdate");
        if (isUpdate.equals("true")) {
            String shop_id = i.getStringExtra("shop_name");
            String product_name = i.getStringExtra("product_name");
            String desc = i.getStringExtra("desc");
            String price = i.getStringExtra("price");
            String discount = i.getStringExtra("discount");
            String hash_tag = i.getStringExtra("hash_tag");
            product_id = i.getStringExtra("product_id");
            et_product_name.setText(product_name);
            et_desc.setText(desc);
            et_price.setText(price);
            et_discount.setText(discount);
            et_hash_tag.setText(hash_tag);
        }
        getUserPreferences();
    }

    private void selectImage() {
        final String[] items = new String[]{
                getString(R.string.camera),
                getString(R.string.gallery)
        };

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        boolean result_camera = CustomPermissions.checkCameraPermission(AddProductsActivity.this);
                        if (result_camera) {
                            cameraImageIntent();
                        }
                        break;

                    case 1:
                        boolean result_gallery = CustomPermissions.checkPermissionForFileAccess(AddProductsActivity.this);
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
        }
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
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
                    if (imageFiles_products.size() <= CustomUtils.IMAGE_LIMIT) {
                        ArrayList<String> selected_image = new ArrayList<>();
                        selected_image.add(tempUri.toString());
                        new ImageCompressAsyncTask(this).execute(selected_image);
                    } else {
                        CustomUtils.showAlertDialog(AddProductsActivity.this, getString(R.string.can_not_share_more_than_five_images));
                    }
                    // loading profile image from local cache
                    //   loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            progressDialog = new ProgressDialog(AddProductsActivity.this);
            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCanceledOnTouchOutside(false);
            //  progressDialog.show();
        }

        @Override
        protected String doInBackground(List<String>... paths) {
            //final ArrayList<String> filePaths=
            //getImages(paths[0]);

            for (String filePaths : paths[0]) {
                //   final int finalI = i;
                String filePath = null;
                try {
                    filePath = SiliCompressor.with(AddProductsActivity.this).compress(filePaths, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/images"));
                    File imageFile = new File(filePath);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bt = bos.toByteArray();
                    String encodedImageString1 = Base64.encodeToString(bt, Base64.DEFAULT);
                    byte[] decodedString1 = Base64.decode(encodedImageString1.getBytes(), Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
                    ImageVideoData image_v = new ImageVideoData();
                    image_v.setBitmap(decodedByte);
                    image_v.setPath(filePath);
                    image_thumbnails_product.add(image_v);
                    imageFiles_products.add(imageFile);
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

    public void getUserShop() {
        try {
            Log.i("show_details", "Bearer " + PrefManager.getInstance(AddProductsActivity.this).getApiToken());

            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getUserShop(PrefManager.getInstance(AddProductsActivity.this).getUserId(), "Bearer " + PrefManager.getInstance(AddProductsActivity.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {

                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                shopnameList.add(name);
                                shopId.add(id);
                            }
                            shopnameList.add(0, "" + getResources().getString(R.string.select_shop));
                            shopId.add(0, "selct id");
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    AddProductsActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    shopnameList
                            );
                            sp_select_shop.setAdapter(adapter);
                        }


                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(AddProductsActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }

    }//getUserShopClose

    public void ProductValidte(String action) {


        if (et_product_name.getText().toString().trim().length() == 0) {

            et_product_name.requestFocus();
            tl_product_name.setError(getResources().getString(R.string.enter_product_name));
            return;
        } else {
            tl_product_name.setError(null);
        }

        if (et_desc.getText().toString().trim().length() == 0) {

            et_desc.requestFocus();
            tl_desc.setError(getResources().getString(R.string.product_desc));

            return;
        } else {
            tl_desc.setError(null);
        }
        if (et_desc.getText().toString().trim().length() > 120) {
            et_desc.requestFocus();
            tl_desc.setError(getResources().getString(R.string.maximum_char_limit));
            return;
        } else {
            tl_desc.setError(null);
        }

        if (et_price.getText().toString().trim().length() == 0) {

            et_price.requestFocus();
            tl_price.setError(getResources().getString(R.string.enter_price));

            return;
        } else {
            tl_price.setError(null);
        }

        if (sp_discount.getSelectedItemPosition()!=0)
        {
            if (et_coupon_title.getText().toString().trim().isEmpty())
            {
                et_coupon_title.requestFocus();
                tl_coupon_title.setError(getResources().getString(R.string.please_enter_coupon_title));
                return;
            }
            else if (et_coupon_desc.getText().toString().trim().isEmpty())
            {
                tl_coupon_title.setError(null);
                tl_coupon_desc.setError(getResources().getString(R.string.please_enter_coupon_desc));
                return;
            }
            else if (et_no_claims.getText().toString().trim().isEmpty())
            {
                tl_coupon_desc.setError(null);
                tl_no_claims.setError(getResources().getString(R.string.please_enter_nno_claims));
                return;
            }
            else if (!et_start_date.getText().toString().trim().isEmpty() || !et_end_date.getText().toString().trim().isEmpty())
            {
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
        /*if (et_hash_tag.getText().toString().trim().length() == 0) {

            et_hash_tag.requestFocus();
            tl_hash_tag.setError(getResources().getString(R.string.enter_hash_tag));

            return;
        } else {
            tl_hash_tag.setError(null);
        }*/
        if (shop_id.equals("")) {
            Toast.makeText(this, "" + getResources().getString(R.string.select_shop), Toast.LENGTH_SHORT).show();
            return;

        }
        if (imageFiles_products.size() <= 1) {

            Toast.makeText(AddProductsActivity.this, "" + getResources().getString(R.string.please_select_atleast_two_images), Toast.LENGTH_SHORT).show();
            return;
        }


        if (action.equals("save")) {
            SaveProductDetails();
        } else {

            Intent i = new Intent(AddProductsActivity.this, ProductPreviewActivity.class);
            i.putExtra("product_name", et_product_name.getText().toString());
            i.putExtra("desc", et_desc.getText().toString());
            i.putExtra("price", et_price.getText().toString());
            i.putExtra("discount", discount);
            i.putExtra("brand", et_brand.getText().toString());
            i.putExtra("model_number", et_model_no.getText().toString());
            i.putExtra("quantity", et_qty.getText().toString());
            i.putExtra("color", et_color.getText().toString());
            i.putExtra("size", et_size.getText().toString());
            i.putExtra("specification", et_specification.getText().toString());
            i.putExtra("hash_tag", et_hash_tag.getText().toString());
            i.putExtra("types", "update");
            /*JsonArray jsonElements = (JsonArray) new Gson().toJsonTree(image_thumbnails);
            i.putExtra("product_images", jsonElements.toString());*/
           /* JsonArray jsonElements1 = (JsonArray) new Gson().toJsonTree(imageFiles_products);
            i.putExtra("image_thumbnail", jsonElements1.toString());*/
            i.putExtra("preference_id", preference_id);
            startActivity(i);
        }


    }


    public void getUserPreferences() {

        try {

            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getUserPreferences(pref.getUserId(), "Bearer " + pref.getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                            allPrefList.clear();
                            allPrefListId.clear();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                String description = j1.getString("description");
                                String image = j1.getString("image");
                                String status1 = j1.getString("status");
                                String created_at = j1.getString("created_at");
                                String updated_at = j1.getString("updated_at");
                                allPrefList.add(name);
                                allPrefListId.add(id);
                            }
                            allPrefList.add(0, getResources().getString(R.string.select_category));
                            allPrefListId.add(0, getResources().getString(R.string.select_category));

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (AddProductsActivity.this, android.R.layout.simple_spinner_item,
                                            allPrefList); //selected item will look like a spinner set from XML
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            sp_cat.setAdapter(spinnerArrayAdapter);


                        }


                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(AddProductsActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    public void SaveProductDetails() {
        try {
            String user_id = PrefManager.getInstance(AddProductsActivity.this).getUserId();
            p_bar.setVisibility(View.VISIBLE);
            MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
            multipartTypedOutput.addPart("name", new TypedString(et_product_name.getText().toString().trim()));
            multipartTypedOutput.addPart("shop_id", new TypedString(shop_id));
            multipartTypedOutput.addPart("description", new TypedString(et_desc.getText().toString().trim()));
            multipartTypedOutput.addPart("price", new TypedString(et_price.getText().toString().trim()));
            multipartTypedOutput.addPart("user_id", new TypedString(user_id));
            multipartTypedOutput.addPart("discount", new TypedString(discount));
            multipartTypedOutput.addPart("brand", new TypedString(et_brand.getText().toString()));
            multipartTypedOutput.addPart("model_number", new TypedString(et_model_no.getText().toString()));
            multipartTypedOutput.addPart("quantity", new TypedString(et_qty.getText().toString()));
            multipartTypedOutput.addPart("specification", new TypedString(et_specification.getText().toString()));
            multipartTypedOutput.addPart("color", new TypedString(et_color.getText().toString()));
            multipartTypedOutput.addPart("size", new TypedString(et_size.getText().toString()));
            multipartTypedOutput.addPart("preference_id ", new TypedString(preference_id));
            multipartTypedOutput.addPart("coupon_title", new TypedString(et_coupon_title.getText().toString()));
            multipartTypedOutput.addPart("coupon_description", new TypedString(et_coupon_desc.getText().toString()));
            multipartTypedOutput.addPart("no_of_claims", new TypedString(et_no_claims.getText().toString()));
            if (imageFiles_products.size() > 0) {
                for (int i = 0; i < imageFiles_products.size(); i++) {
                    multipartTypedOutput.addPart("product_photos[]", new TypedFile("application/octet-stream", new File(imageFiles_products.get(i).getAbsolutePath())));
                }
            } else {
                multipartTypedOutput.addPart("product_photos", new TypedString(""));
            }
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.addProduct("Bearer " + PrefManager.getInstance(AddProductsActivity.this).getApiToken(),
                    multipartTypedOutput, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                String status = jsonObject.getString("status");
                                if (status.equals("1")) {
                                    Toast.makeText(AddProductsActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    new SweetAlertDialog(AddProductsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getResources().getString(R.string.success))
                                            .setContentText("" + jsonObject.getString("message"))
                                            .setConfirmText(getResources().getString(R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(AddProductsActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                p_bar.setVisibility(View.GONE);
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            p_bar.setVisibility(View.GONE);
                            Toast.makeText(AddProductsActivity.this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                            Log.i("fdfdfdfdfdf", "" + error.getMessage());

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }


    }//seaveProductDetailsClose
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }

    }
}
