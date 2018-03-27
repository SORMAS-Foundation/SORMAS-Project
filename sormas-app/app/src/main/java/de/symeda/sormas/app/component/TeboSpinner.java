package de.symeda.sormas.app.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.adapters.ListenerUtil;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.List;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Orson on 29/01/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */

public class TeboSpinner extends EditTeboPropertyField<Object> implements IControlValueRequireable, ISpinnerFieldInterface {

    protected Spinner spnControlInput;
    protected InverseBindingListener inverseBindingListener;
    private SpinnerFieldListener spinnerFieldListener = new SpinnerFieldListener();

    private TeboSpinner parent;
    private int spinnerMode;

    private Object valueOnBind;
    private OnTeboSpinnerAttachedToWindowListener mOnTeboSpinnerAttachedToWindowListener;

    //private ISpinnerInitConfig mConfig;
    private ISpinnerInitSimpleConfig mSimpleConfig;
    private TeboSpinnerAdapter spinnerAdapter;

    private boolean initialized;

    // <editor-fold defaultstate="collapsed" desc="Constructors">

    public TeboSpinner(Context context) {
        super(context);
    }

    public TeboSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeboSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overriden Control Init">

    @Override
    protected void initializeViews(Context context, AttributeSet attrs, int defStyle) {
        initialized = false;

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.TeboSpinner,
                    0, 0);

            try {
                spinnerMode = a.getInteger(R.styleable.TeboSpinner_spinnerMode, Spinner.MODE_DIALOG);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void inflateView(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_spinner_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        spnControlInput = (Spinner) this.findViewById(R.id.spnControlInput);

        spnControlInput.setNextFocusLeftId(getNextFocusLeft());
        spnControlInput.setNextFocusRightId(getNextFocusRight());
        spnControlInput.setNextFocusUpId(getNextFocusUp());
        spnControlInput.setNextFocusDownId(getNextFocusDown());
        spnControlInput.setNextFocusForwardId(getNextFocusForward());

        //spnControlInput.setImeOptions(getImeOptions());

        spinnerFieldListener.registerListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (inverseBindingListener != null) {
                    inverseBindingListener.onChange();
                }
                onValueChanged();
            }
        });
        spnControlInput.setOnItemSelectedListener(spinnerFieldListener);
        spnControlInput.setFocusable(true);
        spnControlInput.setFocusableInTouchMode(true);
        spnControlInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    /*if (getValue() == null) {
                        spnControlInput.setSelection(indexOnOpen);
                    }*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    if (spnControlInput.isShown()) {
                        // open selection slider
                        spnControlInput.performClick();
                    }
                }
            }
        });
        /*caption = (TextView) this.findViewById(R.id.spinner_caption);
        caption.setText(getCaption());
        addCaptionOnClickListener();*/
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ViewGroup.LayoutParams param = spnControlInput.getLayoutParams();

        if (isSlim()) {
            float slimControlTextSize = getContext().getResources().getDimension(R.dimen.slimControlTextSize);
            //float width = spnControlInput.getWidth();
            float height = getContext().getResources().getDimension(R.dimen.dropdownListSlimItemHeight);
            int paddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewTopPadding);
            int paddingBottom = getContext().getResources().getDimensionPixelSize(R.dimen.slimTextViewBottomPadding);
            int paddingLeft = spnControlInput.getPaddingLeft();
            int paddingRight = spnControlInput.getPaddingRight();


            param.height = (int)height;

            spnControlInput.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            //spnControlInput.setLayoutParams(param);
            //spnControlInput.setMinimumHeight(DisplayMetricsHelper.dpToPixels(getContext(), heightInPixel));
            //spnControlInput.text  .setTextSize(TypedValue.COMPLEX_UNIT_PX, slimControlTextSize);
        } else {
            param.height = getContext().getResources().getDimensionPixelSize(R.dimen.maxSpinnerHeight);
            //spnControlInput.hei .setHeight(getContext().getResources().getDimensionPixelSize(R.dimen.maxControlHeight));
            //spnControlInput.setMaxHeight(getContext().getResources().getDimensionPixelSize(R.dimen.maxControlHeight));
        }

        if (this.mOnTeboSpinnerAttachedToWindowListener != null)
            this.mOnTeboSpinnerAttachedToWindowListener.onAttachedToWindow(this);
    }

    @Override
    protected void requestFocusForContentView(View nextView) {
        ((TeboSpinner) nextView).spnControlInput.requestFocus();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters & Setters">

    @BindingAdapter("value")
    public static void setValue(TeboSpinner view, Object value) {
        if (view == null)
            return;

        view.setValue(value);
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged" /*default - can also be removed*/)
    public static Object getValue(TeboSpinner view) {
        if (view == null)
            return null;

        return view.getValue();
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(TeboSpinner view, InverseBindingListener listener) {
        view.inverseBindingListener = listener;
    }

    @Override
    public void setValue(Object value) {
        this.valueOnBind = value;
        //setSelectedItem(value);
    }

    public void setValue(Object value, boolean selectValue) {
        this.valueOnBind = value;

        if (selectValue)
            setSelectedItem(value);
    }

    @Override
    public Object getValue() {
        if (this.valueOnBind == null && spnControlInput.getSelectedItem() == null)
            return null;

        Object v = null;
        if (spnControlInput.getSelectedItem() != null)
            v = ((Item)spnControlInput.getSelectedItem()).getValue();

        return (v != null)? v : this.valueOnBind;
    }

    /*@BindingAdapter(value={"value", "enumClass"}, requireAll=true)
    public static void setValue(TeboRadioGroup view, Object value, Class c) {
        view.setEnumClass(c);
        view.setValue(value);
    }*/

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        //spnControlInput.getSelectedView().setEnabled(false);
        //spnControlInput.getRootView().setEnabled(false);

        spnControlInput.setEnabled(enabled);
        spnControlInput.setClickable(enabled);
        spnControlInput.setFocusable(enabled);

        lblControlLabel.setEnabled(enabled);
        lblControlLabel.setClickable(enabled);
        lblControlLabel.setFocusable(enabled);

        //invalidate();
        //requestLayout();
    }

    @Override
    public int getCount() {
        return spnControlInput.getCount();
    }

    @Override
    public Object getItemAtPosition(int position) {
        return spnControlInput.getItemAtPosition(position);
    }

    @Override
    public boolean isRequiredStatusValid() {
        if (!isRequired())
            return true;

        return getValue() != null;
    }

    @Override
    public void changeVisualState(VisualState state, UserRight editOrCreateUserRight) {
        int labelColor = getResources().getColor(state.getLabelColor(VisualStateControl.SPINNER));
        Drawable drawable = getResources().getDrawable(state.getBackground(VisualStateControl.SPINNER));

        if (state == VisualState.DISABLED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            spnControlInput.setEnabled(false);
            return;
        }

        if (state == VisualState.ERROR) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.FOCUSED) {
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            return;
        }

        if (state == VisualState.NORMAL || state == VisualState.ENABLED) {
            User user = ConfigProvider.getUser();
            lblControlLabel.setTextColor(labelColor);
            setBackground(drawable);
            spnControlInput.setEnabled(true && (editOrCreateUserRight != null)? user.hasUserRight(editOrCreateUserRight) : true);
            return;
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        int pl = spnControlInput.getPaddingLeft();
        int pt = spnControlInput.getPaddingTop();
        int pr = spnControlInput.getPaddingRight();
        int pb = spnControlInput.getPaddingBottom();

        spnControlInput.setBackgroundResource(resid);

        spnControlInput.setPadding(pl, pt, pr, pb);
    }

    @Override
    public void setBackground(Drawable background) {
        int pl = spnControlInput.getPaddingLeft();
        int pt = spnControlInput.getPaddingTop();
        int pr = spnControlInput.getPaddingRight();
        int pb = spnControlInput.getPaddingBottom();

        spnControlInput.setBackground(background);

        spnControlInput.setPadding(pl, pt, pr, pb);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialize">

    public void initialize(ISpinnerInitSimpleConfig config) {
        initialize(null, config);
    }

    public void initialize(TeboSpinner parent, ISpinnerInitSimpleConfig config) {
        this.parent = parent;
        this.mSimpleConfig = config;

        reload(config);

        initialized = true;
    }

    public void initialize(ISpinnerInitConfig config) {
        initialize(null, config);
    }

    public void initialize(TeboSpinner parent, ISpinnerInitConfig config) {
        this.parent = parent;
        this.mSimpleConfig = config;

        reload(config);

        AdapterView.OnItemSelectedListener newListener = new AdapterView.OnItemSelectedListener () {
            private ISpinnerInitConfig nConfig;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nConfig.onItemSelected(TeboSpinner.this, TeboSpinner.this.getValue(), position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nConfig.onNothingSelected(parent);
            }

            private AdapterView.OnItemSelectedListener init(ISpinnerInitConfig nConfig){
                this.nConfig = nConfig;

                return this;
            }
        }.init(config);

        manageOnItemSelectedListener(this, newListener);

        initialized = true;
    }

    private void reload(ISpinnerInitSimpleConfig config) {
        if (this.parent == null) {
            this.setSpinnerAdapter(config.getDataSource(null));

            Object v = null;

            if (!this.initialized) {
                v = this.valueOnBind;
            } else {
                v = getValue();
            }

            if (v != null) {
                setSelectedItem(v);
            } else {
                setSelectedItem(config.getSelectedValue());
            }

            VisualState visualState = config.getInitVisualState();
            changeVisualState(visualState == null? VisualState.NORMAL : visualState);
        }

        if (this.parent != null) {
            changeVisualState(VisualState.DISABLED);
            //this.parent.addOnItemSelected();
            manageOnItemSelectedListener(this.parent, new AdapterView.OnItemSelectedListener() {
                private ISpinnerInitSimpleConfig nConfig;
                private TeboSpinner nParent;
                private TeboSpinner nSelf;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object parentValue = nParent.getValue();
                    int nSelfItemPosition = nSelf.getPositionOf(nSelf.getSelectedItem());

                    if (parentValue == null){
                        restSpinnerAdapter();
                        nSelf.spinnerFieldListener.onItemSelected(nSelf.spnControlInput,
                                nSelf.spnControlInput.findViewById(R.id.spinnerText),
                                nSelfItemPosition, nSelfItemPosition);
                        return;
                    }

                    List<Item> result = nConfig.getDataSource(parentValue);
                    setSpinnerAdapter(result);

                    if (nSelf.getValue() != null) {
                        setSelectedItem(nSelf.getValue());
                    } else {
                        setSelectedItem(nConfig.getSelectedValue());
                    }

                    nSelf.spinnerFieldListener.onItemSelected(nSelf.spnControlInput,
                            nSelf.spnControlInput.findViewById(R.id.spinnerText),
                            nSelfItemPosition, nSelfItemPosition);

                    VisualState visualState = nConfig.getInitVisualState();
                    changeVisualState(visualState == null? VisualState.NORMAL : visualState);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

                private AdapterView.OnItemSelectedListener init(ISpinnerInitSimpleConfig nConfig, TeboSpinner nParent, TeboSpinner nSelf){
                    this.nConfig = nConfig;
                    this.nParent = nParent;
                    this.nSelf = nSelf;

                    return this;
                }
            }.init(config, this.parent, this));
        }
    }

    private void reload() {
        this.reload(mSimpleConfig);
    }

    private void manageOnItemSelectedListener(TeboSpinner teboSpinner, AdapterView.OnItemSelectedListener newListener) {
        AdapterView.OnItemSelectedListener oldListener =
                ListenerUtil.trackListener(teboSpinner, newListener,
                        R.id.teboSpinnerItemSelectedListener);


        if (oldListener != null) {
            teboSpinner.removeOnItemSelected(oldListener);
        }
        if (newListener != null) {
            teboSpinner.addOnItemSelected(newListener);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Methods">

    private void setSpinnerAdapter(List<Item> items) {
        setSpinnerAdapter(items, null);
    }

    private void restSpinnerAdapter() {
        SpinnerAdapter sa = spnControlInput.getAdapter();

        if (sa == null)
            return;

        if (spnControlInput.getAdapter() instanceof TeboSpinnerAdapter) {
            TeboSpinnerAdapter teboSpinnerAdapter = (TeboSpinnerAdapter) sa;
            teboSpinnerAdapter.clear();
            setSelectedItem(null);

            changeVisualState(VisualState.DISABLED);
        }
    }

    public void notifyDataChanged() {
        //Get the value first, else you will loose it
        Object v = null;
        if (!this.initialized) {
            v = this.valueOnBind;
        } else {
            v = getValue();
        }

        setSpinnerAdapter(mSimpleConfig.getDataSource(parent == null? null : parent.getValue()));


        if (v != null) {
            setSelectedItem(v);
        } else {
            setSelectedItem(mSimpleConfig.getSelectedValue());
        }

        /*if (spinnerAdapter != null)
            spinnerAdapter.notifyDataSetChanged();*/
    } //mSimpleConfig

    private void setSpinnerAdapter(List<Item> items, Object selectedValue) {
        if (items.size() <= 0) {
            restSpinnerAdapter();
            return;
        }

        //changeVisualState(VisualState.NORMAL);

        spinnerAdapter = new TeboSpinnerAdapter(
                this,
                getContext(),
                R.layout.control_spinner_item_layout,
                R.id.spinnerText,
                items);


        spinnerAdapter.setDropDownViewResource(R.layout.control_spinner_dropdown_item_layout);
        spnControlInput.setAdapter(spinnerAdapter);



        //setValue(selectedValue);
        /*
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(
                getContext(),
                R.layout.control_spinner_item_layout,
                items)

        */

        /*TeboSpinnerAdapter adapter = new TeboSpinnerAdapter(
                getContext(),
                R.layout.control_spinner_item_layout,
                items);*/

/*            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView textView = (TextView) v.findViewById(R.id.spinnerText);
                if (textView != null && (textView.getText() == null || textView.getText().length() == 0)) {
                    textView.setHint(getResources().getString(R.string.hint_select_entry));
                    textView.setText(getResources().getString(R.string.hint_select_entry));
                }
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.spinnerText);

                if (tv != null && (tv.getText() == null || tv.getText().length() == 0)) {
                    tv.setText(getResources().getString(R.string.hint_select_entry_leave_blank));
                }

                if (position == 0)
                    tv.setTypeface(null, Typeface.BOLD_ITALIC);
                else
                    tv.setTypeface(null, Typeface.NORMAL);

                int selectedPosition = getPositionOf(getSelectedItem());
                if (selectedPosition < 0)
                    return view;

                if (position == selectedPosition) {
                    tv.setTextColor(getResources().getColor(R.color.spinnerDropdownItemTextActive));
                    view.setBackgroundColor(getResources().getColor(R.color.spinnerDropdownItemBackgroundActive));
                } else {
                    view.setBackground(getResources().getDrawable(R.drawable.background_spinner_item));
                }

                return view;
            }
        };*/
    }

    private void setSelectedItem(Object selectedItem) {
        this.valueOnBind = selectedItem;

        if (spnControlInput == null)
            return;

        if (selectedItem == null) {
            spnControlInput.setSelection(-1);
        } else {
            SpinnerAdapter adapter = spnControlInput.getAdapter();

            if (adapter == null) {
                spnControlInput.setSelection(-1);
                return;
            }

            for (int i = 0; i < adapter.getCount(); i++) {
                Object value = ((Item) adapter.getItem(i)).getValue();
                if (selectedItem.equals(value)) {
                    spnControlInput.setSelection(i);
                    break;
                }
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Methods">

    public SpinnerAdapter getAdapter() {
        return spnControlInput.getAdapter();
    }

    private void addOnItemSelected(AdapterView.OnItemSelectedListener listener) {
        spinnerFieldListener.registerListener(listener);
    }

    private void removeOnItemSelected(AdapterView.OnItemSelectedListener listener) {
        spinnerFieldListener.unRegisterListener(listener);
    }

    public int getPositionOf(Item item) {
        if (item == null)
            return -1;

        int itemCount = spnControlInput.getAdapter().getCount();
        for (int i = 0; i < itemCount; i++) {
            Item itemAtIndex = (Item) spnControlInput.getAdapter().getItem(i);
            //String kkkk = item.getKey();

            if (item.getKey().equals(itemAtIndex.getKey())) {
                return i;
            }
        }

        return -1;
    }

    public Item getSelectedItem() {
        return (Item) spnControlInput.getSelectedItem();
    }

    public void setAdapter(SpinnerAdapter adapter) {
        spnControlInput.setAdapter(adapter);
    }

    public void setOnAttachedToWindow(OnTeboSpinnerAttachedToWindowListener listener) {
        this.mOnTeboSpinnerAttachedToWindowListener = listener;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Interfaces">

    public interface ISpinnerInitConfig extends ISpinnerInitSimpleConfig {
        Object getSelectedValue();

        List<Item> getDataSource(Object parentValue);

        VisualState getInitVisualState();

        void onItemSelected(TeboSpinner view, Object value, int position, long id);

        void onNothingSelected(AdapterView<?> parent);

    }

    public interface ISpinnerInitSimpleConfig {
        Object getSelectedValue();

        List<Item> getDataSource(Object parentValue);

        VisualState getInitVisualState();
    }

    // </editor-fold>

}
