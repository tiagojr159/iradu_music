package org.schabi.newpipe.fragments.list.search;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.schabi.newpipe.util.ServiceHelper;

public final class RockSearchFragment extends SearchFragment {
    private static final String QUERY = "melhores rock";

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
        configureQuery(ServiceHelper.getSelectedServiceId(context), QUERY, false);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUseDefaultStateSaving(false);
    }

    @Override
    public void onResume() {
        configureQuery(ServiceHelper.getSelectedServiceId(requireContext()), QUERY, false);
        super.onResume();
    }
}
