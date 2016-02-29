import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener, ForgotPasswordFragment.OnForgotFragmentInteractionListener {

    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    LoginFragment loginFragment = new LoginFragment();
    ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, loginFragment);
        ft.commit();

    }

    @Override
    public void onForgotPasswordClick() {
        showForgotPassword();
    }

    public void showForgotPassword() {
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, forgotPasswordFragment);
        ft.addToBackStack("");
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}


