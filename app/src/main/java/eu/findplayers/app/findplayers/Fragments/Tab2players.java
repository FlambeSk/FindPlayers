package eu.findplayers.app.findplayers.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.findplayers.app.findplayers.R;

/**
 * Created by DOMA on 20.3.2018.
 */

public class Tab2players extends Fragment {

    RecyclerView recyclerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getActivity().getIntent().getExtras();

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_players);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2players, container, false);

        return rootView;
    }
}