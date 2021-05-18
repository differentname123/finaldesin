package com.fb.standard.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fb.standard.R;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public abstract class BaseFragment<V extends ViewDataBinding,VM extends BaseViewModel> extends Fragment {
    private NavController controller;
    protected Context context;
    protected Activity activity;
    private int menuId = 0;
    protected V binding;
    protected VM viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(getResId(), container, false);
        controller = Navigation.findNavController(container);
        TextView title = activity.findViewById(R.id.title);
        title.setText(setTitle());
        binding = DataBindingUtil.bind(root);

        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) new ViewModelProvider(this).get(modelClass);;
        }



        initView(root);
        initData();
        return root;
    }

    protected abstract void initData();
    protected abstract void initView(View root);
    protected abstract int getResId();
    protected abstract String setTitle();

    protected void navigateTo(int id){
        controller.navigate(id);
    }

    protected void navigateTo(int id,Bundle bundle){
        controller.navigate(id,bundle);
    }

    protected void setMenuId(int menuId) {
        this.menuId = menuId;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(menuId != 0){
            inflater.inflate(menuId,menu);
        }
    }

    protected void makeToast(String text){
        Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
    }

}
