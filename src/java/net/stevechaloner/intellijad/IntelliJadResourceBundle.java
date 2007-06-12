package net.stevechaloner.intellijad;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 *
 */
public class IntelliJadResourceBundle {

    /**
     * The bundle.
     */
    private static Reference<ResourceBundle> BUNDLE;

    /**
     * The basename of the bundle.
     */
    @NonNls
    private static final String BUNDLE_NAME = "net.stevechaloner.intellijad.messages-i18n";


    /**
     * Should not be instantiated.
     */
    private IntelliJadResourceBundle() {
    }

    /**
     * Gets the message for the given key with any parameters substituted in place.
     *
     * @param key    the key of the message
     * @param params parameters for the message
     * @return the formatted message
     */
    public static String message(@PropertyKey(resourceBundle = BUNDLE_NAME)String key,
                                 Object... params) {
        return CommonBundle.message(getBundle(),
                key,
                params);
    }

    /**
     * Loads the bundle.
     *
     * @return the bundle
     */
    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (BUNDLE != null) {
            bundle = BUNDLE.get();
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME);
            BUNDLE = new SoftReference<ResourceBundle>(bundle);
        }
        return bundle;
    }
}
