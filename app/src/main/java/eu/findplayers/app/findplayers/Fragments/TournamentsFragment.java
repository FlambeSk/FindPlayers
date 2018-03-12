package eu.findplayers.app.findplayers.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import eu.findplayers.app.findplayers.ProfileActivity;
import eu.findplayers.app.findplayers.R;
import eu.findplayers.app.findplayers.TournamentAddActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TournamentsFragment extends Fragment {

    ImageView newTournament;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Tournaments");

        newTournament = (ImageView)getActivity().findViewById(R.id.newTournament);

        //Add new tournament
        newTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TournamentAddActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.tournament, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public TournamentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tournaments, container, false);
    }


}
