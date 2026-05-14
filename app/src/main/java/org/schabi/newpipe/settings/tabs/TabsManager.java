package org.schabi.newpipe.settings.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import org.schabi.newpipe.R;

import java.util.ArrayList;
import java.util.List;

public final class TabsManager {
    private final SharedPreferences sharedPreferences;
    private final String savedTabsKey;
    private final Context context;
    private SavedTabsChangeListener savedTabsChangeListener;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private TabsManager(final Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.savedTabsKey = context.getString(R.string.saved_tabs_key);
    }

    public static TabsManager getManager(final Context context) {
        return new TabsManager(context);
    }

    public List<Tab> getTabs() {
        final String savedJson = sharedPreferences.getString(savedTabsKey, null);
        try {
            return ensureProfileTabFirst(TabsJsonHelper.getTabsFromJson(savedJson));
        } catch (final TabsJsonHelper.InvalidJsonException e) {
            Toast.makeText(context, R.string.saved_tabs_invalid_json, Toast.LENGTH_SHORT).show();
            return ensureProfileTabFirst(getDefaultTabs());
        }
    }

    public void saveTabs(final List<Tab> tabList) {
        final String jsonToSave = TabsJsonHelper.getJsonToSave(ensureProfileTabFirst(tabList));
        sharedPreferences.edit().putString(savedTabsKey, jsonToSave).apply();
    }

    public void resetTabs() {
        sharedPreferences.edit().remove(savedTabsKey).apply();
    }

    public List<Tab> getDefaultTabs() {
        return ensureProfileTabFirst(TabsJsonHelper.getDefaultTabs());
    }

    private List<Tab> ensureProfileTabFirst(final List<Tab> tabs) {
        final ArrayList<Tab> normalizedTabs = new ArrayList<>();
        normalizedTabs.add(Tab.Type.IRADU_PROFILE.getTab());
        normalizedTabs.add(Tab.Type.LIVE_NEWS.getTab());
        normalizedTabs.add(Tab.Type.TECH_NEWS.getTab());
        for (final Tab tab : tabs) {
            if (tab.getTabId() != Tab.IraduProfileTab.ID
                    && tab.getTabId() != Tab.LiveNewsTab.ID
                    && tab.getTabId() != Tab.TechNewsTab.ID
                    && tab.getTabId() != Tab.FeedTab.ID
                    && tab.getTabId() != Tab.SubscriptionsTab.ID
                    && tab.getTabId() != 11) {
                if (!(tab instanceof Tab.KioskTab kioskTab
                        && "live".equals(kioskTab.getKioskId()))) {
                    normalizedTabs.add(tab);
                }
            }
        }
        return normalizedTabs;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////*/

    public void setSavedTabsListener(final SavedTabsChangeListener listener) {
        if (preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
        savedTabsChangeListener = listener;
        preferenceChangeListener = getPreferenceChangeListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public void unsetSavedTabsListener() {
        if (preferenceChangeListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
        preferenceChangeListener = null;
        savedTabsChangeListener = null;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener getPreferenceChangeListener() {
        return (sp, key) -> {
            if (savedTabsKey.equals(key) && savedTabsChangeListener != null) {
                savedTabsChangeListener.onTabsChanged();
            }
        };
    }

    public interface SavedTabsChangeListener {
        void onTabsChanged();
    }
}
