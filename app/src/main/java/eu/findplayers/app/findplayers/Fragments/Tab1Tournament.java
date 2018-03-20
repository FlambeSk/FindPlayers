package eu.findplayers.app.findplayers.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 20.3.2018.
 */

public class Tab1Tournament extends Fragment {
    TextView tab1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getActivity().getIntent().getExtras();

        tab1 = (TextView) getActivity().findViewById(R.id.tab1);
        tab1.setText(bundle.getString("tournamentName"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1tournament, container, false);



        return rootView;
    }
}
