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

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            Log.e("FontsOverride", e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("FontsOverride", e.getMessage());
        }
    }

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }
}