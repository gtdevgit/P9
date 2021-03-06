package com.openclassrooms.realestatemanager.ui.main.view;



import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.tag.Tag;
import com.openclassrooms.realestatemanager.ui.bundle.PropertyBundle;
import com.openclassrooms.realestatemanager.ui.main.viewstate.MenuItemViewState;
import com.openclassrooms.realestatemanager.ui.main.NavigationState;
import com.openclassrooms.realestatemanager.ui.main.viewmodel.MainViewModel;
import com.openclassrooms.realestatemanager.ui.main.viewstate.MainViewState;
import com.openclassrooms.realestatemanager.ui.propertydetail.viewmodel.PropertyDetailViewModel;
import com.openclassrooms.realestatemanager.ui.propertyedit.listener.PropertyEditListener;
import com.openclassrooms.realestatemanager.ui.constantes.PropertyConst;
import com.openclassrooms.realestatemanager.ui.propertylist.listener.OnPropertySelectedListener;
import com.openclassrooms.realestatemanager.ui.propertymap.listener.OnMapListener;
import com.openclassrooms.realestatemanager.ui.propertysearch.listener.PropertySearchListener;
import com.openclassrooms.realestatemanager.ui.view_model_factory.AppViewModelFactory;
import com.openclassrooms.realestatemanager.utils.LandscapeHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnPropertySelectedListener,
                                                               PropertyEditListener,
                                                               OnMapListener,
        PropertySearchListener {

    MenuItem menuItemHome;
    MenuItem menuItemDetail;
    MenuItem menuItemEdit;
    MenuItem menuItemAdd;
    MenuItem menuItemMap;
    MenuItem menuItemSearch;

    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag.TAG, "MainActivity.onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure tool bar to display title
        Toolbar toolbar = binding.toolbar;
        // display icons and menu items in tools bar
        setSupportActionBar(toolbar);
        // Configure tool bar to display title
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_propertyListFragment_portrait)
                .build();
        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d(Tag.TAG, "MainActivity.onDestinationChanged() called with: controller = [" + controller + "], destination = [" + destination + "], arguments = [" + arguments + "]");
            findAndCallOnBackPressedInterface();
        });

        logScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Tag.TAG, "MainActivity.onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Tag.TAG, "MainActivity.onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Tag.TAG, "MainActivity.onStop() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Tag.TAG, "MainActivity.onStart() called");
    }

    /**
     * find fragment, if fragment is OnBackPressedInterface call onBackPressed
     */
    private void findAndCallOnBackPressedInterface(){
        Log.d(Tag.TAG, "MainActivity.findAndCallOnBackPressedInterface() called");
        // to clear cache when leave fragment edit property
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            List<Fragment> fragmentList = navHostFragment.getChildFragmentManager().getFragments();
            if (fragmentList.size() > 0) {
                Fragment fragment = fragmentList.get(0);
                if (fragment instanceof OnBackPressedInterface){
                    ((OnBackPressedInterface) fragment).onBackPressed();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(Tag.TAG, "MainActivity.onBackPressed() called");

        findAndCallOnBackPressedInterface();
        super.onBackPressed();
    }

    private void logScreen(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d(Tag.TAG, String.format("MainActivity.logScreen(). width = %d, height = %d", width, height));
        Log.d(Tag.TAG, String.format("MainActivity.logScreen(). density = %f", getResources().getDimension(R.dimen.density)));

        WindowManager manager = (WindowManager) this.getSystemService(Activity.WINDOW_SERVICE);
        if (manager != null && manager.getDefaultDisplay() != null) {
            int rotation = manager.getDefaultDisplay().getRotation();
            Log.d(Tag.TAG, "MainActivity.logScreen() rotation = " + rotation);
            int orientation = this.getResources().getConfiguration().orientation;
            Log.d(Tag.TAG, "MainActivity.logScreen() orientation = " + orientation);
        }
        Log.d(Tag.TAG, "MainActivity.logScreen() isLandscape = " + LandscapeHelper.isLandscape());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(Tag.TAG, "MainActivity.onCreateOptionsMenu() called with: menu = [" + menu + "]");
        Log.d(Tag.TAG, "MainActivity.onCreateOptionsMenu() isLandscape = " + LandscapeHelper.isLandscape());
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menuItemHome = menu.findItem(R.id.menu_item_toolbar_home);
        menuItemDetail = menu.findItem(R.id.menu_item_toolbar_detail);
        menuItemEdit = menu.findItem(R.id.menu_item_toolbar_edit);
        menuItemAdd = menu.findItem(R.id.menu_item_toolbar_add);
        menuItemMap = menu.findItem(R.id.menu_item_toolbar_map);
        menuItemSearch = menu.findItem(R.id.menu_item_toolbar_search);

        configureViewModel();

        return true;
    }

    private void configureViewModel(){
        mainViewModel = new ViewModelProvider(this, AppViewModelFactory.getInstance())
                .get(MainViewModel.class);
        mainViewModel.getMainViewStateLiveData().observe(this, this::setMainViewState);

        // When device rotate activity was recreated.
        // inform the ViewModel orientation changed.
        mainViewModel.getIsLandscapeMutableLiveData().setValue(LandscapeHelper.isLandscape());
    }

    private void setMainViewState(MainViewState mainViewState) {
        Log.d(Tag.TAG, "MainActivity.setMainViewState() called with: mainViewState = [" + mainViewState + "]");
        navigateTo(mainViewState.getNavigationState(), mainViewState.getPropertyId());
        setMenuItemHome(mainViewState.getHome());
        setMenuItemDetail(mainViewState.getDetail());
        setMenuItemEdit(mainViewState.getEdit());
        setMenuItemAdd(mainViewState.getAdd());
        setMenuItemMap(mainViewState.getMap());
        setMenuItemSearch(mainViewState.getSearch());
    }

    private void setMenuItemState(MenuItem menuItem, MenuItemViewState menuItemViewState){
        menuItem.setEnabled(menuItemViewState.isEnabled());
        menuItem.setVisible(menuItemViewState.isVisible());
    }

    private void setMenuItemHome(MenuItemViewState menuItemViewState) {
        setMenuItemState(menuItemHome, menuItemViewState);
    }

    private void setMenuItemDetail(MenuItemViewState menuItemViewState) {
        setMenuItemState(menuItemDetail, menuItemViewState);
    }

    private void setMenuItemEdit(MenuItemViewState menuItemViewState) {
        setMenuItemState(menuItemEdit, menuItemViewState);
    }

    private void setMenuItemAdd(MenuItemViewState menuItemViewState) {
        setMenuItemState(menuItemAdd, menuItemViewState);
    }

    private void setMenuItemMap(MenuItemViewState menuItemViewState) {
        setMenuItemState(menuItemMap, menuItemViewState);
    }

    private void setMenuItemSearch(MenuItemViewState menuItemViewState) {
        setMenuItemState(menuItemSearch, menuItemViewState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_toolbar_home:
                mainViewModel.navigateToHome();
                return true;
            case R.id.menu_item_toolbar_detail:
                mainViewModel.navigateToDetail();
                return true;
            case R.id.menu_item_toolbar_edit:
                // retrieve property id from view model
                PropertyDetailViewModel propertyDetailViewModel = new ViewModelProvider(
                        this, AppViewModelFactory.getInstance())
                        .get(PropertyDetailViewModel.class);
                long id = propertyDetailViewModel.getCurrentPropertyId();
                mainViewModel.navigateToEdit(id);
                return true;
            case R.id.menu_item_toolbar_add:
                mainViewModel.navigateToAdd();
                return true;
            case R.id.menu_item_toolbar_map:
                mainViewModel.navigateToMap();
                return true;
            case R.id.menu_item_toolbar_search:
                mainViewModel.navigateToSearch();
                return true;
            case R.id.menu_item_toolbar_loan_calculator:
                mainViewModel.navigateToLoanCalculator();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * burger menu can open drawer
     */
    @Override
    public boolean onSupportNavigateUp() {
        return false;
    }

    @Override
    public void onPropertySelectedClicked(long propertyId) {
        Log.d(Tag.TAG, "MainActivity.onPropertySelectedClicked() called with: propertyId = [" + propertyId + "] isLandscape = [" + LandscapeHelper.isLandscape() + "]");
        mainViewModel.navigateToDetail(propertyId);
    }

    @Override
    public void OnMapClicked(long propertyId) {
        Log.d(Tag.TAG, "MainActivity.OnMapClicked() called with: propertyId = [" + propertyId + "]");
        mainViewModel.navigateToDetail(propertyId);
    }

    /**
     * Cancel edit property
     */
    @Override
    public void onCancelEditProperty(long propertyId) {
        Log.d(Tag.TAG, "MainActivity.onCancelEditProperty() called with: propertyId = [" + propertyId + "]");
        mainViewModel.navigateToDetail(propertyId);
    }

    /**
     * Validate edit property
     */
    @Override
    public void onValidateEditProperty(long propertyId) {
        // close fragment call back
        Log.d(Tag.TAG, "MainActivity.onValidateEditProperty() called with: propertyId = [" + propertyId + "]");
        Toast.makeText(this, R.string.property_created, Toast.LENGTH_LONG).show();
        mainViewModel.navigateToDetail(propertyId);
    }

    @Override
    public void onApplySearch() {
        Log.d(Tag.TAG, "onApplySearch() called");
        mainViewModel.navigateToHome();
    }

    private NavController getNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    }

    // to remove backStack
    // use navController.popBackStack(R.id.fragment_apps, true);
    // or setPopUpTo(int, boolean) with the id of the NavController's graph and set inclusive to true.
    public void navigateTo(NavigationState destination, long propertyId){
        Log.d(Tag.TAG, "navigateTo() called with: destination = [" + destination + "]");
        switch (destination){
            case HOME:
                navToHome();
                return;
            case LIST:
                navToList();
                return;
            case DETAIL:
                navToDetail(propertyId);
                return;
            case ADD:
                navToAdd();
                return;
            case EDIT:
                navToEdit(propertyId);
                return;
            case MAP:
                navToMap();
                return;
            case SEARCH:
                navToSearch();
                return;
            case LOAN_CALCULATOR:
                navToLoanCalculator();
                return;
            default:
        }
    }

    private void navToHome(){
        Log.d(Tag.TAG, "navToHome() called");
        if (LandscapeHelper.isLandscape()) {
            mainViewModel.navigateToDetail();
        } else {
            navToList();
        }
    }

    private void navToList(){
        Log.d(Tag.TAG, "navToList() called");
        getNavController().navigate(R.id.nav_propertyListFragment_portrait);
    }

    private void navToDetail(long propertyId){
        getNavController().navigate(R.id.nav_propertyDetailFragment_portrait,
                PropertyBundle.createEditBundle(propertyId));
    }

    private void navToAdd(){
        Log.d(Tag.TAG, "navToAdd() called");
        getNavController().navigate(R.id.nav_propertyEditFragment_portrait,
                PropertyBundle.createEditBundle(PropertyConst.PROPERTY_ID_NOT_INITIALIZED));
    }

    private void navToEdit(long propertyId){
        Log.d(Tag.TAG, "navToEdit() called with: propertyId = [" + propertyId + "]");
        getNavController().navigate(R.id.nav_propertyEditFragment_portrait,
                PropertyBundle.createEditBundle(propertyId));
    }

    private void navToMap(){
        Log.d(Tag.TAG, "MainActivity.navToMap");
        getNavController().navigate(R.id.nav_propertyMapsFragment_portrait);
    }

    private void navToSearch() {
        Log.d(Tag.TAG, "navToSearch() called");
        getNavController().navigate(R.id.nav_propertySearchFragment_portrait);
    }

    private void navToLoanCalculator(){
        Log.d(Tag.TAG, "navToLoanCalculator() called");
        getNavController().navigate(R.id.nav_loanCalculatorFragment_portrait);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(Tag.TAG, "onPostCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }
}