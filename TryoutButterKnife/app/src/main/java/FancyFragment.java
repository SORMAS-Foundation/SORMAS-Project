import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.technologybord.tryoutbutterknife.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Orson on 06/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class FancyFragment extends Fragment {
    @BindView(R.id.button1) Button button1;
    @BindView(R.id.button2) Button button2;

    private Unbinder unbinder;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fancy_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        // TODO Use fields...
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
