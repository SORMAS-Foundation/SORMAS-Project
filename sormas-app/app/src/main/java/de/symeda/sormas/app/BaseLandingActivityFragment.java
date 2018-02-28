package de.symeda.sormas.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import de.symeda.sormas.app.component.menu.LandingPageMenuAdapter;
import de.symeda.sormas.app.component.menu.LandingPageMenuControl;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.component.menu.LandingPageMenuParser;
import de.symeda.sormas.app.component.menu.OnLandingPageMenuClickListener;
import de.symeda.sormas.app.component.menu.OnNotificationCountChangingListener;
import de.symeda.sormas.app.core.NotImplementedException;
import de.symeda.sormas.app.core.adapter.multiview.EnumMapDataBinderAdapter;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Orson on 11/12/2017.
 */

public abstract class BaseLandingActivityFragment<E extends Enum<E>, TAdapter extends EnumMapDataBinderAdapter<E>> extends Fragment implements OnNotificationCountChangingListener, OnLandingPageMenuClickListener {

    private BaseLandingActivity baseLandingActivity;
    private RecyclerView.LayoutManager layoutManager;
    private TAdapter adapter;
    private RecyclerView recyclerView;
    private LandingPageMenuControl menuControl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getRootListLayout(), container, false);

        if (getActivity() instanceof BaseLandingActivity) {
            this.baseLandingActivity = (BaseLandingActivity) this.getActivity();
        } else {
            throw new NotImplementedException("The landing activity for fragment must implement BaseLandingActivity");
        }

        this.recyclerView = createRecyclerView(view);
        this.layoutManager = createLayoutManager();
        this.adapter = createLandingAdapter();
        this.menuControl = createMenuControl(view);

        try {
            Context menuControlContext = this.menuControl.getContext();

            this.menuControl.setOnNotificationCountChangingListener(this);
            this.menuControl.setOnLandingPageMenuClickListener(this);

            this.menuControl.setAdapter(new LandingPageMenuAdapter(menuControlContext));
            this.menuControl.setMenuParser(new LandingPageMenuParser(menuControlContext));
            this.menuControl.setMenuData(getMenuData());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public int getRootListLayout() {
        return R.layout.fragment_root_landing_layout;
    }

    public LandingPageMenuControl createMenuControl(View view) {
        return (LandingPageMenuControl)view.findViewById(R.id.landingPageMenuControl);
    }

    public RecyclerView createRecyclerView(View view) {
        return (RecyclerView) view.findViewById(R.id.recyclerview_main);
    }

    public abstract TAdapter createLandingAdapter();

    public abstract RecyclerView.LayoutManager createLayoutManager();

    public abstract String getMenuData();

    public abstract int onNotificationCountChanging(AdapterView<?> parent, LandingPageMenuItem menuItem, int position);

    public abstract boolean onLandingPageMenuClick(AdapterView<?> parent, View view, LandingPageMenuItem menuItem, int position, long id);

    public TAdapter getLandingAdapter() {
        return this.adapter;
    }

    public RecyclerView.LayoutManager getLandingLayoutManager() {
        return this.layoutManager;
    }

    public BaseLandingActivity getBaseLandingActivity() {
        return this.baseLandingActivity;
    }

    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

    public LandingPageMenuControl getMenuControl() {
        return this.menuControl;
    }

    public void configure() {
        this.recyclerView.setAdapter(this.adapter);
        this.recyclerView.setLayoutManager(this.layoutManager);
    }

    protected static <TFragment extends BaseLandingActivityFragment, TActivity extends BaseLandingActivity> TFragment newInstance(Class<TFragment> f) throws IllegalAccessException, java.lang.InstantiationException {
        TFragment fragment = f.newInstance();
        /*Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }


        fragment.setArguments(bundle);*/
        return fragment;
    }
}
