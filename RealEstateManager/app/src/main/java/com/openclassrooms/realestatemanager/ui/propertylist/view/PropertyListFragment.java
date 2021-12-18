package com.openclassrooms.realestatemanager.ui.propertylist.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.tag.Tag;
import com.openclassrooms.realestatemanager.ui.propertylist.listener.OnPropertySelectedListener;
import com.openclassrooms.realestatemanager.ui.propertylist.listener.OnRowPropertyClickListener;
import com.openclassrooms.realestatemanager.ui.propertylist.viewmodel.PropertyListViewModel;
import com.openclassrooms.realestatemanager.ui.propertylist.viewstate.PropertyListViewState;
import com.openclassrooms.realestatemanager.ui.view_model_factory.AppViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PropertyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropertyListFragment extends Fragment {

    private TextView textViewWarning;
    private RecyclerView recyclerView;
    PropertyListAdapter propertyListAdapter;

    /**
     * this interface is for sending propertyId to MainActivity
     */
    private OnPropertySelectedListener callbackPropertySelected;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity() {
        try {
            callbackPropertySelected = (OnPropertySelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + "must implement OnPropertyClickedListener");
        }
    }

    public PropertyListFragment() {
        // Required empty public constructor
    }

    public static PropertyListFragment newInstance() {
        PropertyListFragment fragment = new PropertyListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(Tag.TAG, "PropertyListFragment.onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(Tag.TAG, "PropertyListFragment.onCreateView() called with: container = [" + container + "]");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);
        textViewWarning = view.findViewById(R.id.fragment_property_list_text_view_warning);
        configureRecyclerView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Tag.TAG, "PropertyListFragment.onViewCreated() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
        configureViewModel();
    }

    private void configureRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.fragment_property_list_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        propertyListAdapter = new PropertyListAdapter(getContext(), new OnRowPropertyClickListener() {
            @Override
            public void onClickRowProperty(long propertyId) {
                // send property id to activity
                Log.d(Tag.TAG, "PropertyListFragment.onClickRowProperty() called with: propertyId = [" + propertyId + "]");
                if (callbackPropertySelected != null) {
                    callbackPropertySelected.onPropertySelectedClicked(propertyId);
                }
            }
        });

        recyclerView.setAdapter(propertyListAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureViewModel() {
        PropertyListViewModel propertyListViewModel = new ViewModelProvider(
                requireActivity(), AppViewModelFactory.getInstance())
                .get(PropertyListViewModel.class);
        propertyListViewModel.getViewState().observe(getViewLifecycleOwner(), new Observer<PropertyListViewState>() {
            @Override
            public void onChanged(PropertyListViewState propertyListViewState) {
                showWarning(propertyListViewState.isShowWarning());
                ((PropertyListAdapter)recyclerView.getAdapter()).updateData(propertyListViewState.getRowPropertyViewStates());
            }
        });
        propertyListViewModel.load();
    }

    private void showWarning(boolean showWarning) {
        if (showWarning) {
            textViewWarning.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewWarning.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}