package com.rabbitfighter.wordsleuth.InstructionFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rabbitfighter.wordsleuth.Activities.SearchActivity;
import com.rabbitfighter.wordsleuth.R;
import com.rabbitfighter.wordsleuth.Utils.RobotoFontsHelper;

/**
 * Created by rabbitfighter on 7/14/15.
 */
public class InstructionsBlankTiles extends Fragment {

    TextView tv_title, tv_instructions, tv_page_number;
    Button btn_skip_instruction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout resource that will be returned
        View rootView = inflater.inflate(R.layout.fragment_instructions, container, false);
        Context context = rootView.getContext();
        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // InstructionsPagerAdapter
        Bundle args = getArguments();

        ((TextView) rootView.findViewById(R.id.page_number)).setText("Page " + args.getInt("page_position"));

        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        tv_instructions = (TextView) rootView.findViewById(R.id.tv_instructions);
        tv_page_number = (TextView) rootView.findViewById(R.id.page_number);
        btn_skip_instruction = (Button) rootView.findViewById(R.id.btn_skip_instruction);

        /* Set typefaces */
        tv_title.setTypeface(RobotoFontsHelper.getTypeface(context, RobotoFontsHelper.roboto_black));
        tv_instructions.setTypeface(RobotoFontsHelper.getTypeface(context, RobotoFontsHelper.roboto_condensed_light));
        tv_page_number.setTypeface(RobotoFontsHelper.getTypeface(context, RobotoFontsHelper.roboto_medium_italic));
        btn_skip_instruction.setTypeface(RobotoFontsHelper.getTypeface(context, RobotoFontsHelper.roboto_medium_italic));

        /* Set the text */
        tv_title.setText(R.string.txt_title_page5);
        tv_instructions.setText(R.string.txt_instructions_page5);

        btn_skip_instruction.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipInstructions();
                    }
                }
        );
        return rootView;
    }

    /**
     * Skip to the search activity
     */
    private void skipInstructions() {
        Intent i = new Intent(getActivity(), SearchActivity.class);
        startActivity(i);
    }
}