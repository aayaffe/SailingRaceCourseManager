package com.aayaffe.sailingracecoursemanager.Input_UI_Layer;

/**
 * Created by Jonathan on 18/07/2016.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aayaffe.sailingracecoursemanager.R;

import java.text.DecimalFormat;

public class HorizontalNumberPicker extends RelativeLayout {
    private double initialN=0;
    private double steps=1;
    private double number = 0;
    private float textSize = 30;
    private int buttonsBackgroundColor;
    private int buttonsTextColor;


    private DecimalFormat df = new DecimalFormat(".##");

    private TextView numberTV;
    private Button plusB;
    private Button minusB;


    public HorizontalNumberPicker(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.horizontal_number_picker, this);
    }

    public HorizontalNumberPicker(Context context, double initialNumber, double steps) {
        super(context);
        this.initialN=initialNumber;
        this.number=initialNumber;
        this.steps=steps;
        LayoutInflater.from(context).inflate(R.layout.horizontal_number_picker, this);
    }

    public HorizontalNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.HorizontalNumberPicker, 0, 0);
        try {
            textSize = a.getFloat(R.styleable.HorizontalNumberPicker_textSize, 25);
            buttonsTextColor = a.getColor(R.styleable.HorizontalNumberPicker_buttonsTextColor, Color.BLACK);
            buttonsBackgroundColor = a.getColor(R.styleable.HorizontalNumberPicker_buttonsBackgroundColor, Color.TRANSPARENT);
            // get the text and colors specified using the names in attrs.xml
        } finally {
            a.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.horizontal_number_picker, this);

        numberTV = (TextView) this.findViewById(R.id.np_number);
        numberTV.setText(number + "");
        numberTV.setTextSize(textSize);

        plusB = (Button) this.findViewById(R.id.np_plus);
        plusB.setTextSize(textSize);
        plusB.setTextColor(buttonsTextColor);
        plusB.setBackgroundColor(buttonsBackgroundColor);
        plusB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                number = number + steps;
                setNumber(number);
            }
        });
        minusB = (Button) this.findViewById(R.id.np_minus);
        minusB.setTextSize(textSize);
        minusB.setTextColor(buttonsTextColor);
        minusB.setBackgroundColor(buttonsBackgroundColor);
        minusB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                number = number - steps;
                setNumber(number);
            }
        });
    }
    public void setTextSize(float textSize){
        this.textSize=textSize;
        numberTV.setTextSize(textSize);
        plusB.setTextSize(textSize);
        minusB.setTextSize(textSize);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setNumber(double text) {
        this.number = Double.valueOf(df.format(text));
        if(numberTV!=null){
            numberTV.setText(number+"");
        }
    }

    public void setButtonsTextColor(int buttonsTextColor) {
        this.buttonsTextColor = buttonsTextColor;
        plusB.setTextColor(buttonsTextColor);
        minusB.setTextColor(buttonsTextColor);
    }

    public void setButtonsBackgroundColor(int buttonsBackgroundColor) {
        this.buttonsBackgroundColor = buttonsBackgroundColor;
        plusB.setBackgroundColor(buttonsBackgroundColor);
        minusB.setBackgroundColor(buttonsBackgroundColor);
    }

    public void setButtonsColors(int buttonsTextColor, int buttonsBackgroundColor) {
        this.buttonsTextColor = buttonsTextColor;
        plusB.setTextColor(buttonsTextColor);
        minusB.setTextColor(buttonsTextColor);

        this.buttonsBackgroundColor = buttonsBackgroundColor;
        plusB.setBackgroundColor(buttonsBackgroundColor);
        minusB.setBackgroundColor(buttonsBackgroundColor);
    }

    public double getNumber() {
        return number;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }
    public double getSteps() {
        return steps;
    }
    public void setInitialN(double initialN) {
        this.initialN = initialN;
    }

    public void configNumbers(double initialNum, double steps){
        this.initialN=initialNum;
        this.number=initialNum;
        this.steps=steps;
        setNumber(number);
    }
}