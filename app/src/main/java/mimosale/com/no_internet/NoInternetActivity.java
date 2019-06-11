package mimosale.com.no_internet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mimosale.com.R;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.HomeActivity;
import mimosale.com.login.LoginActivity;
import mimosale.com.spalsh.SplashActivity;

public class NoInternetActivity extends AppCompatActivity {
@BindView(R.id.btn_retry)
    Button btn_retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet2);
        ButterKnife.bind(this);
        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CustomUtils.isNetworkAvailable(NoInternetActivity.this)) {
                    PrefManager.getInstance(NoInternetActivity.this);
                    Intent i = new Intent(NoInternetActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                    } else {
                    Toast.makeText(NoInternetActivity.this, ""+getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
