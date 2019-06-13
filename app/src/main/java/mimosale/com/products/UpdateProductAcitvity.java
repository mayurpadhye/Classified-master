package mimosale.com.products;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.iceteck.silicompressorr.SiliCompressor;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.R;
import mimosale.com.helperClass.CustomFileUtils;
import mimosale.com.helperClass.CustomPermissions;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.my_posting.product_posting.PostingImagesAdapter;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.shop.EditShopActivity;
import mimosale.com.shop.EventImagesAdapter;
import mimosale.com.shop.ImageVideoData;
import mimosale.com.shop.adapter.ShopImageDetailsAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static mimosale.com.helperClass.CustomUtils.IMAGE_LIMIT;
import static mimosale.com.products.AddProductsActivity.image_thumbnails_product;

public class UpdateProductAcitvity extends AppCompatActivity implements View.OnClickListener {
    List<String> shopnameList = new ArrayList<>();
    List<String> shopId = new ArrayList<>();
    @BindView(R.id.sp_select_shop)
    Spinner sp_select_shop;
    @BindView(R.id.sp_cat)
    Spinner sp_cat;
    @BindView(R.id.sp_sub_cat)
    Spinner sp_sub_cat;
    @BindView(R.id.et_product_name)
    EditText et_product_name;
    @BindView(R.id.btn_upload)
    Button btn_upload;
    @BindView(R.id.rv_images)
    RecyclerView rv_images;
    @BindView(R.id.et_desc)
    EditText et_desc;
    @BindView(R.id.et_price)
    EditText et_price;
    @BindView(R.id.et_discount)
    EditText et_discount;
    @BindView(R.id.et_brand)
    EditText et_brand;
    @BindView(R.id.et_model_no)
    EditText et_model_no;
    @BindView(R.id.et_qty)
    EditText et_qty;
    @BindView(R.id.et_color)
    EditText et_color;
    @BindView(R.id.et_size)
    EditText et_size;
    @BindView(R.id.et_specification)
    EditText et_specification;
    @BindView(R.id.et_hash_tag)
    EditText et_hash_tag;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.btn_preview)
    Button btn_preview;
    @BindView(R.id.p_bar)
    ProgressBar p_bar;
    String product_id = "";
    PostingImagesAdapter adapter;
    @BindView(R.id.sp_discount)
    Spinner sp_discount;

    @BindView(R.id.tl_product_name)
    TextInputLayout tl_product_name;
    @BindView(R.id.tl_hash_tag)
    TextInputLayout tl_hash_tag;
    @BindView(R.id.tl_desc)
    TextInputLayout tl_desc;
    @BindView(R.id.tl_price)
    TextInputLayout tl_price;
    @BindView(R.id.tl_discount)
    TextInputLayout tl_discount;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    final Calendar myCalendar = Calendar.getInstance();
    String coupon_id="";
    public static ArrayList<File> imageFiles_product_update = new ArrayList<>();
    ;
    Context context;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    LinearLayoutManager llm_images;
    ProgressDialog progressDialog;
    List<String> allPrefList = new ArrayList<>();
    List<String> allPrefIds = new ArrayList<>();
    List<File> shopImagesPojoList = new ArrayList<>();
    String discount = "";
    String shop_id = "";
    String preference_id="";
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
   public static ArrayList<ImageVideoData> image_thumbnails_product_update = new ArrayList<ImageVideoData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_acitvity);
        ButterKnife.bind(this);
        product_id = getIntent().getStringExtra("product_id");
        shop_id = getIntent().getStringExtra("shop_id");
        context = this;
        llm_images = new LinearLayoutManager(getApplicationContext());
        llm_images.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new PostingImagesAdapter(UpdateProductAcitvity.this, UpdateProductAcitvity.this, image_thumbnails_product_update, "update");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (UpdateProductAcitvity.this, android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.discount_array)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_discount.setAdapter(spinnerArrayAdapter);
        getProductDetails();
        getUserShop();
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

        btn_preview.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
    }//onCreateViewClose
    public void getUserShop() {
        try {
            Log.i("show_details", "Bearer " + PrefManager.getInstance(UpdateProductAcitvity.this).getApiToken());

            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getUserShop(PrefManager.getInstance(UpdateProductAcitvity.this).getUserId(), "Bearer " + PrefManager.getInstance(UpdateProductAcitvity.this).getApiToken(), new Callback<JsonElement>() {
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
                                    UpdateProductAcitvity.this,
                                    android.R.layout.simple_spinner_item,
                                    shopnameList
                            );
                            sp_select_shop.setAdapter(adapter);
                            if (shopId.contains(shop_id)) {
                                sp_select_shop.setSelection(shopId.indexOf(shop_id));
                            }
                        }


                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(UpdateProductAcitvity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }

    }//getUserShopClose
    public void getProductDetails() {
        RetrofitClient retrofitClient = new RetrofitClient();
        RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
        service.getProductDetails(product_id, new Callback<JsonElement>() {
            @Override
            public void success(JsonElement jsonElement, Response response) {
                //this method call if webservice success
                try {
                    JSONObject jsonObject = new JSONObject(jsonElement.toString());
                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        JSONObject j1 = jsonObject.getJSONObject("data");
                        String id = j1.getString("id");
                        String name = j1.getString("name");
                        String description = j1.getString("description");
                        String price = j1.getString("price");
                        String hash_tag = j1.getString("hash_tags");
                        String status1 = j1.getString("status");
                        String discount = j1.getString("discount");
                        String brand = j1.getString("brand");
                        String model_number = j1.getString("model_number");
                        String quantity = j1.getString("quantity");
                        String color = j1.getString("color");
                        String size = j1.getString("size");
                        String specification = j1.getString("specification");
                        String selling_price = j1.getString("selling_price");
                        String like_status = j1.getString("like_status");
                        String like_count = j1.getString("like_count");
                        preference_id = j1.getString("preference_id");
                        toolbar_title.setText(getResources().getString(R.string.update_product));
                        et_price.setText(price);
                        et_hash_tag.setText(hash_tag);
                        et_brand.setText(brand);
                        et_model_no.setText(model_number);
                        et_qty.setText(quantity);
                        et_color.setText(color);
                        et_size.setText(size);
                        et_specification.setText(specification);
                        et_product_name.setText(name);
                        et_desc.setText(description);
                        List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.discount_array));
                        if (!discount.equals("")) {

                            for (int k = 0; k < Lines.size(); k++) {
                                if (Lines.get(k).contains(discount)) {
                                    sp_discount.setSelection(k);
                                    break;
                                }
                            }
                        } else {}




                        if (j1.has("latest_product_coupon")) {
                            JSONObject latest_shop_coupon1;
                            String latest_shop_string;

                            // JSONObject latest_shop_coupon = j1.getJSONObject("latest_shop_coupon");

                            Object latest_shop_coupon_obj = j1.get("latest_product_coupon");
                            if (latest_shop_coupon_obj instanceof JSONObject) {

                                latest_shop_coupon1 = (JSONObject) latest_shop_coupon_obj;
                                String title = latest_shop_coupon1.getString("title");
                                String coupon_id1 = latest_shop_coupon1.getString("coupon_id");
                                coupon_id=coupon_id1;
                                String description_coupon = latest_shop_coupon1.getString("description");
                                String start_date = latest_shop_coupon1.getString("start_date");
                                String end_date = latest_shop_coupon1.getString("end_date");
                                String no_of_claims = latest_shop_coupon1.getString("no_of_claims");

                                et_start_date.setText(start_date);
                                et_end_date.setText(end_date);
                                et_coupon_desc.setText(description_coupon);
                                et_no_claims.setText(no_of_claims);

                                coupon_id = coupon_id1;
                                   /* if (!start_date.equals("null") && !end_date.equals("null")) {
                                        tv_sale_duration.setText(start_date + " - " + end_date);}
                                    else {tv_sale_duration.setText(getResources().getString(R.string.not_avail));}*/

                            }
                            else{


                            }
                        }



                        image_thumbnails_product_update.clear();
                        imageFiles_product_update.clear();
                        getAllPrefData(preference_id);
                        shopImagesPojoList.clear();
                        ArrayList<String> selected_image = new ArrayList<>();
                        JSONArray product_images = j1.getJSONArray("product_images");
                        for (int j = 0; j < product_images.length(); j++) {
                            JSONObject j2 = product_images.getJSONObject(j);
                            String image_id = j2.getString("id");
                            String image = j2.getString("image");
                            selected_image.add(WebServiceURLs.SHOP_IMAGE + image);
                        }
                        new ImageCompressAsyncTask1(context).execute(selected_image);
                        /*ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(ProductDetailsActivityNew.this, shopImagesPojoList);
                        pager.setAdapter(bannerAdapter);
                        CirclePageIndicator indicator = (CirclePageIndicator)
                                findViewById(R.id.indicator);
                        indicator.setViewPager(pager);
                        final float density = getResources().getDisplayMetrics().density;
                        indicator.setRadius(5 * density);*/
                    }


                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    Log.i("fdfdfdfdfdf", "" + e.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(UpdateProductAcitvity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                Log.i("fdfdfdfdfdf", "" + error.getMessage());

            }
        });

    }//getProductDetails
    public void getAllPrefData(final String preference_id) {
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
                            for (int k = 0; k < data.length(); k++)
                            {
                                JSONObject j1 = data.getJSONObject(k);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                allPrefList.add(name);
                                allPrefIds.add(id);
                            }
                            allPrefList.add(0, getResources().getString(R.string.select_category));
                            allPrefIds.add(0, getResources().getString(R.string.select_category));
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    UpdateProductAcitvity.this,
                                    R.layout.row_language,
                                    allPrefList
                            );


                            sp_cat.setAdapter(adapter);
                            if (allPrefIds.contains(preference_id)) {
                                sp_cat.setSelection(allPrefIds.indexOf(preference_id));
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                CustomUtils.hideKeyboard(v, getApplicationContext());
                if (imageFiles_product_update.size() >= IMAGE_LIMIT) {
                    CustomUtils.showAlertDialog(UpdateProductAcitvity.this, getString(R.string.can_not_share_more_than_five_images));
                } else {
                    selectImage();
                }
                break;

            case R.id.btn_submit:
                ProductValidte("update");
                break;

            case R.id.btn_preview:
                ProductValidte("preview");
                break;


        }
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
                        boolean result_camera = CustomPermissions.checkCameraPermission(UpdateProductAcitvity.this);
                        if (result_camera) {
                            cameraImageIntent();
                        }
                        break;

                    case 1:
                        boolean result_gallery = CustomPermissions.checkPermissionForFileAccess(UpdateProductAcitvity.this);
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
    public class ImageCompressAsyncTask1 extends AsyncTask<List<String>, String, String> {
        Context mContext;
        Uri selectedImage;

        public ImageCompressAsyncTask1(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UpdateProductAcitvity.this);
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
                    image_thumbnails_product_update.add(image_v);
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
                    imageFiles_product_update.add(file);

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
            adapter = new PostingImagesAdapter(UpdateProductAcitvity.this, UpdateProductAcitvity.this, image_thumbnails_product_update, "update");
            rv_images.setLayoutManager(llm_images);
            rv_images.setAdapter(adapter);
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
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
            progressDialog = new ProgressDialog(UpdateProductAcitvity.this);
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
                    filePath = SiliCompressor.with(UpdateProductAcitvity.this).compress(filePaths, new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/media/images"));
                    File imageFile = new File(filePath);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bt = bos.toByteArray();
                    String encodedImageString1 = Base64.encodeToString(bt, Base64.DEFAULT);
                    byte[] decodedString1 = Base64.decode(encodedImageString1.getBytes(), Base64.NO_WRAP);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
                    ImageVideoData image_v = new ImageVideoData();
                    image_v.setBitmap(decodedByte);
                    image_v.setPath(filePath);
                    image_thumbnails_product_update.add(image_v);
                    imageFiles_product_update.add(imageFile);
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
        if (sp_discount.getSelectedItemPosition()==0 || et_coupon_desc.getText().toString().trim().isEmpty() || et_coupon_title.getText().toString().trim().isEmpty() || et_no_claims.getText().toString().trim().isEmpty() || et_start_date.getText().toString().trim().isEmpty() || et_end_date.getText().toString().trim().isEmpty() ) {
            CustomUtils.showToast(getResources().getString(R.string.please_select_discount), UpdateProductAcitvity.this);
            return;
        }

        if (shop_id.equals("")) {
            Toast.makeText(this, "" + getResources().getString(R.string.select_shop), Toast.LENGTH_SHORT).show();
            return;

        }
        if (imageFiles_product_update.size() <= 1) {

            Toast.makeText(UpdateProductAcitvity.this, "" + getResources().getString(R.string.please_select_atleast_two_images), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!action.equals("preview"))
            UpdateProduct();
        else {
            Intent i = new Intent(UpdateProductAcitvity.this, ProductPreviewActivity.class);
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
            i.putExtra("types","update");
            i.putExtra("shop_id",shop_id);
            i.putExtra("product_id",product_id);
            i.putExtra("preference_id",preference_id);
            i.putExtra("coupon_title",et_coupon_title.getText().toString().trim());
            i.putExtra("coupon_desc",et_coupon_desc.getText().toString().trim());
            i.putExtra("no_of_coupon",et_no_claims.getText().toString().trim());
            i.putExtra("end_date", et_end_date.getText().toString().trim());
            i.putExtra("start_date", et_start_date.getText().toString().trim());
            JsonArray jsonElements = (JsonArray) new Gson().toJsonTree(image_thumbnails_product_update);
            i.putExtra("product_images", jsonElements.toString());
            JsonArray jsonElements1 = (JsonArray) new Gson().toJsonTree(imageFiles_product_update);
            i.putExtra("image_thumbnail", jsonElements1.toString());
            startActivity(i);
        }


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

    @OnClick(R.id.iv_back)
    void onBackClick() {
        finish();
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CustomPermissions.MY_PERMISSIONS_REQUEST_CAMERA:
                    Bitmap bitmap_thumbnail = (Bitmap) data.getExtras().get("data");
                    bitmap_thumbnail.getByteCount();
                    Uri tempUri = CustomUtils.getImageUri(getApplicationContext(), bitmap_thumbnail);
                    File finalFile = new File(CustomUtils.getRealPathFromURI(getApplicationContext(), tempUri));
                    CustomUtils.showLog("Camera File path ", finalFile.getAbsolutePath() + "");
                    if (imageFiles_product_update.size() <= CustomUtils.IMAGE_LIMIT) {
                        ArrayList<String> selected_image = new ArrayList<>();
                        selected_image.add(tempUri.toString());
                        new ImageCompressAsyncTask(this).execute(selected_image);
                    } else {
                        CustomUtils.showAlertDialog(UpdateProductAcitvity.this, getString(R.string.can_not_share_more_than_five_images));
                    }
                    break;

                case CustomPermissions.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        int imageCount = imageFiles_product_update.size();
                        int selectedImages = imageCount + count;
                        if (selectedImages > IMAGE_LIMIT) {
                            CustomUtils.showAlertDialog(UpdateProductAcitvity.this, getString(R.string.can_not_share_more_than_five_images));
                            return;
                        } else {
                            ArrayList<String> images = new ArrayList<>();

                            for (int i = 0; i < count; i++) {
                                images.add(data.getClipData().getItemAt(i).getUri().toString());
                                //  CustomUtil.showLog("Image selected ", data.getClipData().getItemAt(i).getUri().toString());

                                String picturePath = CustomFileUtils.getPath(getApplicationContext(), data.getClipData().getItemAt(i).getUri());
                                File file = new File(picturePath);
                                imageFiles_product_update.add(file);
                                long lengthInMB = lengthInMB = (file.length() / 1024) / 1024;
                                ArrayList<String> image = new ArrayList<>();
                                image.add(data.getClipData().getItemAt(i).getUri().toString());

                                if (lengthInMB >= 20) {
                                    new ImageCompressAsyncTask(this).execute(image);
                                } else {

                                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                    ImageVideoData image_v = new ImageVideoData();
                                  /* image_v.setBitmap(bitmap);
                                    image_v.setPath(picturePath);
                                    image_thumbnails.add(image_v);
*/

                                    ExifInterface ei = null;
                                    try {
                                        ei = new ExifInterface(picturePath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);

                                    Bitmap rotatedBitmap = null;
                                    switch (orientation) {

                                        case ExifInterface.ORIENTATION_ROTATE_90:
                                            rotatedBitmap = rotateImage(bitmap, 90);
                                            break;

                                        case ExifInterface.ORIENTATION_ROTATE_180:
                                            rotatedBitmap = rotateImage(bitmap, 180);
                                            break;

                                        case ExifInterface.ORIENTATION_ROTATE_270:
                                            rotatedBitmap = rotateImage(bitmap, 270);
                                            break;

                                        case ExifInterface.ORIENTATION_NORMAL:
                                        default:
                                            rotatedBitmap = bitmap;
                                    }
                                    //File file1 = new File(String.valueOf(data.getData()));

                                    image_v.setBitmap(rotatedBitmap);
                                    image_v.setPath(picturePath);
                                    image_thumbnails_product_update.add(image_v);

                                       /* adapter = new EventImagesAdapter(getApplicationContext(), CreateEventActivity.this, image_thumbnails);
                                        rv_event_image.setLayoutManager(llm_images);
                                        rv_event_image.setAdapter(adapter);*/
                                }


                            }

                            //    new ImageCompressAsyncTask(CreateEventActivity.this).execute(images);
                            if (imageFiles_product_update.size() <= IMAGE_LIMIT) {
                                adapter = new PostingImagesAdapter(UpdateProductAcitvity.this, UpdateProductAcitvity.this, image_thumbnails_product_update, "create");
                                rv_images.setLayoutManager(llm_images);
                                rv_images.setAdapter(adapter);
                            } else {
                                //showToast(getString(R.string.can_not_share_more_than_five_images), CreateEventActivity.this);
                                if (imageCount <= imageFiles_product_update.size()) {
                                    for (int i = imageFiles_product_update.size(); i > imageCount; i--) {
                                        image_thumbnails_product_update.remove(i - 1);
                                        imageFiles_product_update.remove(i - 1);
                                    }

                                }
                                CustomUtils.showAlertDialog(UpdateProductAcitvity.this, getString(R.string.can_not_share_more_than_five_images));
                            }
                        }
                    } else if (data.getData() != null) {
                        Uri selectedImage = data.getData();
                        String picturePath = CustomFileUtils.getPath(getApplicationContext(), selectedImage);
                        File file = new File(picturePath);
                        imageFiles_product_update.add(file);
                        if (imageFiles_product_update.size() <= IMAGE_LIMIT) {
                            long lengthInMB = (file.length() / 1024) / 1024;
                            ArrayList<String> image = new ArrayList<>();
                            image.add(selectedImage.toString());
                            if (lengthInMB >= 20) {
                                new ImageCompressAsyncTask(this).execute(image);
                            } else {

                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                ImageVideoData image_v = new ImageVideoData();
                             /* image_v.setBitmap(bitmap);
                                image_v.setPath(picturePath);
                                image_thumbnails.add(image_v);*/

                                ExifInterface ei = null;
                                try {
                                    ei = new ExifInterface(picturePath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_UNDEFINED);

                                Bitmap rotatedBitmap = null;
                                switch (orientation) {

                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        rotatedBitmap = rotateImage(bitmap, 90);
                                        break;

                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        rotatedBitmap = rotateImage(bitmap, 180);
                                        break;

                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        rotatedBitmap = rotateImage(bitmap, 270);
                                        break;

                                    case ExifInterface.ORIENTATION_NORMAL:
                                    default:
                                        rotatedBitmap = bitmap;
                                }
                                //File file1 = new File(String.valueOf(data.getData()));

                                image_v.setBitmap(rotatedBitmap);
                                image_v.setPath(picturePath);
                                image_thumbnails_product_update.add(image_v);
                                adapter = new PostingImagesAdapter(UpdateProductAcitvity.this, UpdateProductAcitvity.this, image_thumbnails_product_update, "create");
                                rv_images.setLayoutManager(llm_images);
                                rv_images.setAdapter(adapter);
                            }
                        } else {
                            CustomUtils.showAlertDialog(UpdateProductAcitvity.this, getString(R.string.can_not_share_more_than_five_images));
                        }
                    }
                    break;


            }
        }

    }
    public void UpdateProduct() {

        try {
            String user_id = PrefManager.getInstance(UpdateProductAcitvity.this).getUserId();
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
            multipartTypedOutput.addPart("product_id", new TypedString(product_id));
            multipartTypedOutput.addPart("preference_id", new TypedString(preference_id));
            if (!discount.equals(""))
            {
                multipartTypedOutput.addPart("discount", new TypedString(discount));
                multipartTypedOutput.addPart("coupon_title", new TypedString(et_coupon_title.getText().toString()));
                multipartTypedOutput.addPart("coupon_description", new TypedString(et_coupon_desc.getText().toString()));
                multipartTypedOutput.addPart("no_of_claims", new TypedString(et_no_claims.getText().toString()));
                multipartTypedOutput.addPart("start_date", new TypedString(et_start_date.getText().toString()));
                multipartTypedOutput.addPart("end_date", new TypedString(et_end_date.getText().toString()));
            }
            if (imageFiles_product_update.size() > 0) {
                for (int i = 0; i < imageFiles_product_update.size(); i++) {
                    multipartTypedOutput.addPart("product_photos[]", new TypedFile("application/octet-stream", new File(imageFiles_product_update.get(i).getAbsolutePath())));
                }
            } else {
                multipartTypedOutput.addPart("product_photos", new TypedString(""));
            }


            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.update_product("Bearer " + PrefManager.getInstance(UpdateProductAcitvity.this).getApiToken(),
                    multipartTypedOutput, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            //this method call if webservice success
                            try {
                                JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                String status = jsonObject.getString("status");
                                p_bar.setVisibility(View.GONE);
                                if (status.equals("1")) {
                                    new SweetAlertDialog(UpdateProductAcitvity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getResources().getString(R.string.success))
                                            .setContentText("" + getResources().getString(R.string.product_updated))
                                            .setConfirmText(getResources().getString(R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(UpdateProductAcitvity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            p_bar.setVisibility(View.GONE);
                            Toast.makeText(UpdateProductAcitvity.this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                            Log.i("fdfdfdfdfdf", "" + error.getMessage());

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
