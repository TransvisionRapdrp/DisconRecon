package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disconrecon_library.R;
import com.example.disconrecon_library.adapter.DTCAdapter;
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient1;
import com.example.disconrecon_library.model.DTC;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.model.Result;
import com.example.disconrecon_library.values.ClassGPS;
import com.example.disconrecon_library.values.FunctionCall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.example.disconrecon_library.api.RetroClient1.Andkey;
import static com.example.disconrecon_library.values.constant.DTC_DETAILS_UPDATE_DIALOG;
import static com.example.disconrecon_library.values.constant.INSERT_FAILURE;
import static com.example.disconrecon_library.values.constant.INSERT_SUCCESS;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_SUCCESS;

public class TCReading2 extends Fragment implements View.OnClickListener, DTCAdapter.OnItemClickListener {
    private final int CAMERA_IMAGE = 2;
    private static final String IMAGE_DIRECTORY_NAME = "MyCamera";
    private static Uri fileUri; // file url to store image/video
    Toolbar toolbar;
    List<LoginDetails> loginList;
    ProgressDialog progressDialog;
    static FunctionCall functionCall;
    RecyclerView recyclerView;
    TextView tv_date, tv_calender;
    private int day, month, year;
    private Calendar mcalender;
    private String dd, date;
    DTCAdapter DTCAdapter;
    List<DTC> dtcList;
    ImageView imageView, img_dtc;
    AlertDialog alertDialog;
    LinearLayout layout;
    List<Result> resultList;
    ClassGPS classGPS;
    String gps_lat = "", gps_long = "";
    File destination;
    static String pathname = "";
    static String pathextension = "";
    static String filename = "";
    static File mediaFile;
    static String cons_ImgAdd = "", cons_imageextension = "", TC_CODE = "";

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    setHasOptionsMenu(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    setHasOptionsMenu(false);
                    recyclerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Data Not Found");
                    break;

                case INSERT_SUCCESS:
                    progressDialog.dismiss();
                    alertDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, resultList.get(0).getMessage());
                    functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
                    getDTCDetails(loginList.get(0).getMRCODE(), date);
                    break;

                case INSERT_FAILURE:
                    alertDialog.dismiss();
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "try once again");
                    break;

            }
            return false;
        }
    });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading, container, false);
        initialize(view);
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.setTitle(getResources().getString(R.string.tc_reading));
        toolbar.setTitleTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_back));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
        progressDialog = new ProgressDialog(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            loginList = (ArrayList<LoginDetails>) bundle.getSerializable("loginList");
        }
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        functionCall = new FunctionCall();
        recyclerView = view.findViewById(R.id.recycler);
        tv_date = view.findViewById(R.id.txt_date);
        tv_calender = view.findViewById(R.id.txt_calender);
        tv_calender.setOnClickListener(this);
        dtcList = new ArrayList<>();
        imageView = view.findViewById(R.id.img_no_data);
        layout = view.findViewById(R.id.lin_main);
        resultList = new ArrayList<>();
        classGPS = new ClassGPS(getActivity());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_calender) {
            DateDialog();
        }

        if (view.getId() == R.id.img_dtc) {
            captureImage();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void DateDialog() {
        mcalender = Calendar.getInstance();
        day = mcalender.get(Calendar.DAY_OF_MONTH);
        year = mcalender.get(Calendar.YEAR);
        month = mcalender.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dd = (year + "-" + (month + 1) + "-" + dayOfMonth);
            date = functionCall.Parse_Date4(dd);
            tv_date.setText(date);
            functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
            getDTCDetails(loginList.get(0).getMRCODE(), date);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
    }

    //***********************************************************************************************************************************************
    private void GPSlocation() {
        if (classGPS.canGetLocation()) {
            double latitude = classGPS.getLatitude();
            double longitude = classGPS.getLongitude();
            gps_lat = "" + latitude;
            gps_long = "" + longitude;
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater mi = Objects.requireNonNull(getActivity()).getMenuInflater();
        mi.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search...");
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        search(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DTCAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getDTCDetails(String MRCODE, String DATE) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getDTCDeatilsMR(MRCODE, DATE,Andkey).enqueue(new Callback<List<DTC>>() {
            @Override
            public void onResponse(@NonNull Call<List<DTC>> call, @NonNull Response<List<DTC>> response) {
                if (response.isSuccessful()) {
                    dtcList.clear();
                    dtcList = response.body();
                    DTCAdapter = new DTCAdapter(dtcList, getActivity());
                    DTCAdapter.setOnItemClickListener(TCReading2.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(DTCAdapter);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<DTC>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void show_dtc_details_update_dialog(int id, DTC dtc) {
        pathname = "";
        pathextension = "";
        if (id == DTC_DETAILS_UPDATE_DIALOG) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            dialog.setTitle("DTC UPDATE");
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = Objects.requireNonNull(inflater).inflate(R.layout.dtc_details_update_layout, null);
            dialog.setView(view);
            final TextView dtc_name = view.findViewById(R.id.txt_dtc_name);
            final TextView dtc_code = view.findViewById(R.id.txt_dtc_code);
            final TextView dtc_ir = view.findViewById(R.id.txt_dtc_ir);
            img_dtc = view.findViewById(R.id.img_dtc);
            img_dtc.setOnClickListener(this);
            final EditText current_reading = view.findViewById(R.id.et_current_reading);
            final Button cancel_button = view.findViewById(R.id.dialog_negative_btn);
            final Button update_button = view.findViewById(R.id.dialog_positive_btn);
            alertDialog = dialog.create();
            GPSlocation();
            TC_CODE = dtc.getTCCODE();
            dtc_name.setText(dtc.getDTCNAME());
            dtc_code.setText(TC_CODE);
            dtc_ir.setText(dtc.getTCIR());
            current_reading.setText(dtc.getTCFR());
            update_button.setOnClickListener(view1 -> {
                if (Double.parseDouble(dtc.getTCIR()) >= Double.parseDouble(current_reading.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), layout, "Current Reading should be greater than Previous Reading");
                    return;
                }
                if (TextUtils.isEmpty(pathname)) {
                    functionCall.setSnackBar(getActivity(), layout, "Please take pic & proceed");
                    return;
                }
                functionCall.showprogressdialog("Please wait to complete", "Data Inserting", progressDialog);
                dtcUpdate(loginList.get(0).getMRCODE(), TC_CODE, functionCall.Parse_Date9(date), current_reading.getText().toString(), gps_lat,
                        gps_long, pathextension, functionCall.encoded(pathname));

            });
            cancel_button.setOnClickListener(view1 -> alertDialog.dismiss());
            alertDialog.show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void dtcUpdate(String MRCODE, String TCCODE, String DATE, String TCFR, String Latitude, String Langitude, String imagename, String encodefilr) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.dtcUpdateMR(MRCODE, TCCODE, DATE, TCFR, Latitude, Langitude, imagename, encodefilr,Andkey).enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(@NonNull Call<List<Result>> call, @NonNull Response<List<Result>> response) {
                if (response.isSuccessful()) {
                    resultList.clear();
                    resultList = response.body();
                    handler.sendEmptyMessage(INSERT_SUCCESS);
                } else
                    handler.sendEmptyMessage(INSERT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Result>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(INSERT_FAILURE);
            }
        });
    }

    @TargetApi(24)
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, getActivity());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_IMAGE) {
            if (resultCode == RESULT_OK) {
                cons_ImgAdd = pathname;
                cons_imageextension = pathextension;
                Bitmap bitmap = null;
                try {
                    bitmap = functionCall.getImage(cons_ImgAdd, getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                destination = functionCall.filestorepath("TC_PICS", cons_imageextension);
                if (bitmap != null) {
                    saveExternalPrivateStorage(destination, timestampItAndSave(bitmap));
                }
                String destinationfile = functionCall.filepath("TC_PICS") + File.separator + cons_imageextension;
                Bitmap bitmap1 = null;
                try {
                    bitmap1 = functionCall.getImage(destinationfile, getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img_dtc.setImageBitmap(bitmap1);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Creating file uri to store image/video
    private Uri getOutputMediaFileUri(int type, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", Objects.requireNonNull(getOutputMediaFile(type)));
        else return Uri.fromFile(getOutputMediaFile(type));
    }


    //returning image / video
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(android.os.Environment.getExternalStorageDirectory(), functionCall.Appfoldername() + File.separator + IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + TC_CODE + "_" + timeStamp + ".jpg");
            pathname = mediaStorageDir.getPath() + File.separator + TC_CODE + "_" + timeStamp + ".jpg";
            pathextension = TC_CODE + "_" + timeStamp + ".jpg";
            filename = TC_CODE + "_" + timeStamp;
        } else {
            return null;
        }

        return mediaFile;
    }


    //**********************************Below code is for adding Watermark****************************************************
    private Bitmap timestampItAndSave(Bitmap bitmap) {
        Bitmap watermarkimage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(42);
        tPaint.setColor(Color.RED);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(bitmap, 0f, 0f, null);
        float height = tPaint.measureText("yY");
        cs.drawText(filename, 20f, height + 15f, tPaint);
        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(new File(pathname)));
            File directory = new File(pathname);
            FileInputStream watermarkimagestream = new FileInputStream(directory);
            watermarkimage = BitmapFactory.decodeStream(watermarkimagestream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return watermarkimage;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveExternalPrivateStorage(File folderDir, Bitmap bitmap) {
        if (folderDir.exists()) {
            folderDir.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(folderDir);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
