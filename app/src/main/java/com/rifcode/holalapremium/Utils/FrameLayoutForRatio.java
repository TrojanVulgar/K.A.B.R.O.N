package com.rifcode.holalapremium.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class FrameLayoutForRatio extends FrameLayout
{
    private float a = 1.33F;

    public FrameLayoutForRatio(Context paramContext)
    {
        super(paramContext);
    }

    public FrameLayoutForRatio(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }


    @SuppressLint("WrongConstant")
    protected void onMeasure(int paramInt1, int paramInt2)
    {
        if (View.MeasureSpec.getMode(paramInt1) == 1073741824) {
            paramInt2 = View.MeasureSpec.makeMeasureSpec((int)(View.MeasureSpec.getSize(paramInt1) * this.a), 1073741824);
        }
        super.onMeasure(paramInt1, paramInt2);
    }

}
