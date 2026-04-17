package nhom22.doctorfinder.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    protected VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateViewBinding();
        setContentView(binding.getRoot());
        initView();
        initData();
    }

    protected abstract VB inflateViewBinding();
    protected abstract void initView();
    protected abstract void initData();
}