package nhom22.doctorfinder.ui.auth;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import nhom22.doctorfinder.R;

public abstract class AuthFragment extends Fragment {

    protected void navigateToLogin() {
        NavController navController = NavHostFragment.findNavController(this);
        if (!navController.popBackStack(R.id.loginFragment, false)) {
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        }
    }

    protected void navigateToRegister() {
        NavController navController = NavHostFragment.findNavController(this);

        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == R.id.loginFragment) {

            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        }
    }
}
