package com.android.application.help;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Lot on 2017/3/12.
 */

public class MyToast {
    public static void makeText(Context c,String s) {
       Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
    }
}
