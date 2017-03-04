package com.aayaffe.sailingracecoursemanager.dialogs;

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
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.General.GeneralUtils;

import java.text.DecimalFormat;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

public class HorizontalNumberPicker extends RelativeLayout {
    private float initialN=0;
    private float steps=1;
    private float number = 0;
    private float textSize = 30;
    private int buttonsBackgroundColor;
    private int buttonsTextColor;


    private float upperBoundary = 1000;  //if Activated, CAN NOT BE this number as well.
    private boolean upperBoundaryActivation = false;
    private float lowerBoundary = -1000; //if Activated, CAN NOT BE this number as well.
    private boolean lowerBoundaryActivation = false;


    private DecimalFormat df = new DecimalFormat(".##");

    private EditText numberTV;
    private Button plusB;
    private Button minusB;


    public HorizontalNumberPicker(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.horizontal_number_picker, this);
    }

    public HorizontalNumberPicker(Context context, float initialNumber, float steps) {
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

        numberTV = (EditText) this.findViewById(R.id.np_number);
        numberTV.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);
        numberTV.setText(Float.toString(number));
        numberTV.setTextSize(textSize);

        plusB = (Button) this.findViewById(R.id.np_plus);
        plusB.setTextSize(textSize);
        plusB.setTextColor(buttonsTextColor);
        plusB.setBackgroundColor(buttonsBackgroundColor);
        plusB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                number = getNumber() + steps;
                if(number >= upperBoundary&&upperBoundaryActivation){
                    number=lowerBoundary+1+(number-upperBoundary);
                }
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
                number = getNumber() - steps;
                if(number<=lowerBoundary&&lowerBoundaryActivation){
                    number=upperBoundary-1-(lowerBoundary-number);
                }
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
        this.number = Float.valueOf(df.format(text));
        if(numberTV!=null){
            numberTV.setText(Float.toString(number));
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

    public float getNumber() {
        String s = numberTV.getText().toString();
        Float f = GeneralUtils.tryParseFloat(s);
        if (f==null)
            number = 0;
        else
            number = f;
        return number;
    }

    public void setSteps(float steps) {
        this.steps = steps;
    }
    public double getSteps() {
        return steps;
    }
    public void setInitialN(float initialN) {
        this.initialN = initialN;
    }

    public void configNumbers(float initialNum, float steps){
        this.initialN=initialNum;
        this.number=initialNum;
        this.steps=steps;
        setNumber(number);
    }

    public void configNumbers(float initialNum, float steps , float min, float max){
        this.initialN=initialNum;
        this.number=initialNum;
        this.steps=steps;
        setBoundaries(min, max);
        setNumber(number);
    }

    public void setBoundaries(float min, float max){
        upperBoundary=max;
        lowerBoundary=min;
        upperBoundaryActivation=true;
        lowerBoundaryActivation=true;
    }

    public void setUpperBoundary(float upperBoundary) {
        this.upperBoundary = upperBoundary;
    }

    public double getUpperBoundary() {
        return upperBoundary;
    }

    public boolean getUpperBoundaryActivation(){
        return upperBoundaryActivation;
    }
    public void setUpperBoundaryActivation(boolean b){
        upperBoundaryActivation = b;
    }

    public void setLowerBoundary(float lowerBoundary) {
        this.lowerBoundary = lowerBoundary;
    }

    public void setLowerBoundaryActivation(boolean lowerBounderyActivation) {
        this.lowerBoundaryActivation = lowerBounderyActivation;
    }


    public double getLowerBoundary() {
        return lowerBoundary;
    }
    public boolean getLowerBoundaryActivation() {
        return lowerBoundaryActivation;
    }


}