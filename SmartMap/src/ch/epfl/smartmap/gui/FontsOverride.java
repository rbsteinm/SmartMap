package ch.epfl.smartmap.gui;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

/**
 * Override existing typefaces
 * 
 * @author agpmilli
 */
public final class FontsOverride {

    private static final String TAG = FontsOverride.class.getSimpleName();

    private FontsOverride() {
        // nothing to do
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "Couldn't find fields : " + e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal Access Exception : " + e);
        }
    }

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }
}