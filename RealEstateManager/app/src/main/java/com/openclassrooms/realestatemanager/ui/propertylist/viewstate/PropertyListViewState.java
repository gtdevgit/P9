package com.openclassrooms.realestatemanager.ui.propertylist.viewstate;

import java.util.List;

public class PropertyListViewState {

    private boolean showWarning;
    private List<RowPropertyViewState> rowPropertyViewStates;

    public PropertyListViewState(boolean showWarning, List<RowPropertyViewState> rowPropertyViewStates) {
        this.showWarning = showWarning;
        this.rowPropertyViewStates = rowPropertyViewStates;
    }

    public boolean isShowWarning() {
        return showWarning;
    }

    public List<RowPropertyViewState> getRowPropertyViewStates() {
        return rowPropertyViewStates;
    }
}
