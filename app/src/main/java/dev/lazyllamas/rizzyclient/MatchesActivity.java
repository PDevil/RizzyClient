package dev.lazyllamas.rizzyclient;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import dev.lazyllamas.rizzyclient.Business.Person;


public class MatchesActivity extends Fragment {
    private CardViewAdapter mAdapter;

    private ArrayList<Person> mItems;
    private ArrayList<Person.Activities> list = new ArrayList<>( );

    public static MatchesActivity newInstance(int sectionNumber) {
        MatchesActivity fragment = new MatchesActivity();
        Bundle args = new Bundle();
        args.putInt(MainTabbedActivity.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_matches, container, false);
        list.add(Person.Activities.Cycling);
        list.add(Person.Activities.NordicWalking);
        list.add(Person.Activities.Skateboarding);

        //TODO
        mItems = new ArrayList<>(3);
        Bitmap icon = BitmapFactory.decodeResource(v.getResources(),
                R.drawable.sport);
        mItems.add(new Person("Artur", 60, "Polubił zabawy z UI", 0, 0, Person.Activities.NordicWalking, list, icon, "1a3ea0e3-76be-4281-a6e4-28d8d2094546"));
        mItems.add(new Person("Gerard", 25, "Szanuje Papieża, koszykówka", 1, 1, Person.Activities.Cycling, list, icon, "55571649-899f-4253-9651-4690eaa1dec0"));
        mItems.add(new Person("Paweł", 25, "Wkurwiony na Jave i OAuth2", 25.55, 44.22, Person.Activities.Skateboarding, list, icon, "f72ba1d1-4bf0-4955-905b-9bb3a87c5d03"));


        OnItemTouchListener itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
               /* Snackbar snack = Snackbar.make(getView(), R.string.swipeText, Snackbar.LENGTH_LONG);
                TextView tv = snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();*/

                Bundle b = new Bundle();
                b.putParcelable("dev.lazyllamas.rizzyclient", mItems.get(position));

                Intent i = new Intent(getContext(), PersonActivity.class);
                i.putExtras(b);

                startActivity(i);


            }

            @Override
            public void onButton1Click(View view, int position) {

            }

            @Override
            public void onButton2Click(View view, int position) {

            }
        };


        //TODO
        mAdapter = new CardViewAdapter(mItems, itemTouchListener);


        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//
                                    mItems.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                               /* for (int position : reverseSortedPositions) {
//
                                    mItems.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                                mAdapter.notifyDataSetChanged();*/
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);


        return v;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();


    }


    public interface OnItemTouchListener {

        void onCardViewTap(View view, int position);

        void onButton1Click(View view, int position);

        void onButton2Click(View view, int position);
    }

    public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
        private List<Person> cards;
        private OnItemTouchListener onItemTouchListener;

        public CardViewAdapter(List<Person> cards, OnItemTouchListener onItemTouchListener) {
            this.cards = cards;
            this.onItemTouchListener = onItemTouchListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_layout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.name.setText(cards.get(i).getName() + ", " + cards.get(i).getAge());
            viewHolder.description.setText(cards.get(i).getDescription());
        }

        @Override
        public int getItemCount() {
            return cards == null ? 0 : cards.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private TextView description;


            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.personname);
                description = itemView.findViewById(R.id.persondescription);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemTouchListener.onCardViewTap(v, getLayoutPosition());
                    }
                });
            }
        }
    }
}