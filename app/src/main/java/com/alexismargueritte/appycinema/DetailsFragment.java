package com.alexismargueritte.appycinema;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_details,
        container, false);
  }

  public void setText(String item) {
    TextView view = (TextView) getView().findViewById(R.id.detailText);
    view.setText(item);
  }
}