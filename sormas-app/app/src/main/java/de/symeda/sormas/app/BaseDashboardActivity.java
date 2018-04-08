package de.symeda.sormas.app;

/**
 * Created by Orson on 08/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class BaseDashboardActivity { // extends AbstractSormasActivity implements INotificationContext {

    /*private View rootView;
    private View fragmentFrame = null;
    private View statusFrame = null;
    private View applicationTitleBar = null;
    private TextView subHeadingListActivityTitle;

    private Enum pageStatus;
    private List<BaseSummaryFragment> fragments = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //initializeActivity(savedInstanceState);
        initializeActivity(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean setHomeAsUpIndicator() {
        return true;
    }

    @Override
    public void showFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFragmentView() {
        if (fragmentFrame != null)
            fragmentFrame.setVisibility(View.GONE);
    }

    protected void initializeBaseActivity(Bundle savedInstanceState) {
        rootView = findViewById(R.id.base_layout);
        subHeadingListActivityTitle = (TextView)findViewById(R.id.subHeadingListActivityTitle);

        Bundle arguments = (savedInstanceState != null)? savedInstanceState : getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA);
        initializeActivity(arguments);


        if (showTitleBar()) {
            applicationTitleBar = findViewById(R.id.applicationTitleBar);
            statusFrame = findViewById(R.id.statusFrame);
        }

        fragmentFrame = findViewById(R.id.fragment_frame);
        if (fragmentFrame != null) {
            try {
                if (savedInstanceState == null) {
                    replaceFragment(getSummaryFragments());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void replaceFragment(List<BaseSummaryFragment> fragments) {
        FragmentManager manager = getSupportFragmentManager();

        if (fragments != null) {
            this.fragments = fragments;

            for (BaseSummaryFragment f: fragments) {
                if (f.getArguments() == null)
                    f.setArguments(getIntent().getBundleExtra(ConstantHelper.ARG_NAVIGATION_CAPSULE_INTENT_DATA));

                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(f.getContainerResId(), f);
                ft.addToBackStack(null);
                ft.commit();
            }

        }
    }*/

}
