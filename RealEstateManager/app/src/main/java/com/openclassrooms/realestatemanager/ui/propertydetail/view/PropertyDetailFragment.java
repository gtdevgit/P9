package com.openclassrooms.realestatemanager.ui.propertydetail.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.ui.propertylist.view.PropertyListFragment;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.tag.Tag;
import com.openclassrooms.realestatemanager.ui.propertydetail.viewmodel.PropertyDetailViewModel;
import com.openclassrooms.realestatemanager.ui.propertydetail.viewmodelfactory.PropertyDetailViewModelFactory;
import com.openclassrooms.realestatemanager.ui.propertydetail.viewstate.PropertyDetailViewState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PropertyDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropertyDetailFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "property_id_arg";

    private long propertyId;

    TextView textViewPrice;
    TextView textViewSurface;
    TextView textViewDescription;
    TextView textViewAddress;
    TextView textViewPointOfInterest;
    TextView textViewAvailable;
    TextView textViewEntryDate;
    TextView textViewSaleDate;
    TextView textViewAgentName;
    TextView textViewAgentEmail;
    TextView textViewAgentPhone;
    TextView textViewCategory;
    TextView textViewType;
    TextView textViewPhotoLegend;

    private PropertyDetailViewModel propertyDetailViewModel;
    private void setPropertyDetailViewModel(PropertyDetailViewModel propertyDetailViewModel) {
        this.propertyDetailViewModel = propertyDetailViewModel;
    }

    public PropertyDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param propertyId Parameter 1.
     * @return A new instance of fragment PropertyDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PropertyDetailFragment newInstance(long propertyId, PropertyDetailViewModel propertyDetailViewModel) {
        PropertyDetailFragment fragment = new PropertyDetailFragment();
        fragment.setPropertyDetailViewModel(propertyDetailViewModel);
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, propertyId);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    this interface used to edit property
     */
    private OnEditPropertyListener callbackEditProperty;
    public interface OnEditPropertyListener{
        public void onEditPropertyClicked(long propertyId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
    }

    private void createCallbackToParentActivity() {
        try {
            callbackEditProperty = (OnEditPropertyListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + "must implement OnPropertyClickedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_detail, container, false);
        configureComponents(view);
        configureBottomNavigationBar(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureDetailViewModel();


        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_PARAM1)){
                this.propertyId = getArguments().getLong(ARG_PARAM1, -1);
                if (propertyId > -1) {
                    propertyDetailViewModel.load(propertyId);
                }
            }
        }
    }

    private void configureComponents(View view){
        textViewPrice = view.findViewById(R.id.property_detail_prive_value);
        textViewSurface = view.findViewById(R.id.property_detail_surface_value);
        textViewDescription = view.findViewById(R.id.property_detail_description_value);
        textViewAddress = view.findViewById(R.id.property_detail_address_value);
        textViewPointOfInterest = view.findViewById(R.id.property_detail_point_of_interest_value);
        textViewAvailable = view.findViewById(R.id.property_detail_available_value);
        textViewEntryDate = view.findViewById(R.id.property_detail_entry_date_value);
        textViewSaleDate = view.findViewById(R.id.property_detail_sale_date_value);
        textViewAgentName = view.findViewById(R.id.property_detail_agent_name_value);
        textViewAgentEmail = view.findViewById(R.id.property_detail_agent_email_value);
        textViewAgentPhone = view.findViewById(R.id.property_detail_agent_phone_value);
        textViewCategory = view.findViewById(R.id.property_detail_category_value);
        textViewType = view.findViewById(R.id.property_detail_type_value);
        textViewPhotoLegend = view.findViewById(R.id.property_detail_photo_legend_value);
    }

    private void configureBottomNavigationBar(View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.fragment_property_detail_bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return navigate(item);
            }
        });
    }

    private boolean navigate(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_propertyEditFragment:
                callbackEditProperty.onEditPropertyClicked(this.propertyId);
                return true;
        }
        return false;
    }

    private void configureDetailViewModel(){
        propertyDetailViewModel = new ViewModelProvider(
                requireActivity(), PropertyDetailViewModelFactory.getInstance())
                .get(PropertyDetailViewModel.class);

        propertyDetailViewModel.getViewState().observe(getViewLifecycleOwner(), new Observer<PropertyDetailViewState>() {
            @Override
            public void onChanged(PropertyDetailViewState propertyDetailViewState) {
                 setPrice(propertyDetailViewState.getProperty().getPrice());
                setSurface(propertyDetailViewState.getProperty().getSurface());
                setDescription(propertyDetailViewState.getProperty().getDescription());
                setAddress(propertyDetailViewState.getProperty().getAddress());
                setPointOfInterest(propertyDetailViewState.getProperty().getPointsOfInterest());
                setAvailable(propertyDetailViewState.getProperty().isAvailable());
                setEntryDate(propertyDetailViewState.getProperty().getEntryDate());
                setSaleDate(propertyDetailViewState.getProperty().getSaleDate());
                setAgentName(propertyDetailViewState.getAgent().getName());
                setAgentEmail(propertyDetailViewState.getAgent().getEmail());
                setAgentPhone(propertyDetailViewState.getAgent().getPhone());
                setCategoryName(propertyDetailViewState.getCategory().getName());
                setTypeName(propertyDetailViewState.getPropertyType().getName());
                setPhotoLegend("");
            }
        });
    }

    private void setPrice(int price){
        textViewPrice.setText(Utils.convertPriceToString(price));
    }

    private void setSurface(int surface){

        textViewSurface.setText(Utils.convertSurfaceToString(surface));
    }

    private void setDescription(String description){
        textViewDescription.setText(description);
    }

    private void setAddress(String address){
        Log.d(Tag.TAG, "setAddress() called with: address = [" + address + "]");
        textViewAddress.setText(address);
    }

    private void setPointOfInterest(String pointOfInterest){
        textViewPointOfInterest.setText(pointOfInterest);
    }

    private void setAvailable(boolean available){
        if (available) {
            textViewAvailable.setText(R.string.yes);
        } else {
            textViewAvailable.setText(R.string.no);
        };
    }

    private void setEntryDate(Date entryDate) {
        // todo logique à déplacer dans le VM
        if (entryDate == null){
            textViewEntryDate.setText(R.string.no_date);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
            String strDate = dateFormat.format(entryDate);
            textViewEntryDate.setText(strDate);
        }
    }

    private void setSaleDate(Date saleDate){
        // todo logique à déplacer dans le VM
        if (saleDate == null){
            textViewSaleDate.setText(R.string.no_date);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
            String strDate = dateFormat.format(saleDate);
            textViewSaleDate.setText(strDate);
        }
    }

    private void setAgentName(String agentName) {
        textViewAgentName.setText(agentName);
    }

    private void setAgentEmail(String agentEmail) {
        textViewAgentName.setText(agentEmail);
    }

    private void setAgentPhone(String agentPhone) {
        textViewAgentName.setText(agentPhone);
    }

    private void setCategoryName(String categoryName) {
        textViewCategory.setText(categoryName);
    }

    private void setTypeName(String typeName) {
        textViewType.setText(typeName);
    }

    private void setPhotoLegend(String photoLegend) {
        textViewPhotoLegend.setText(photoLegend);
    }
}