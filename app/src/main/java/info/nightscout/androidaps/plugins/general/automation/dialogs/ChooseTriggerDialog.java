package info.nightscout.androidaps.plugins.general.automation.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import info.nightscout.androidaps.R;
import info.nightscout.androidaps.plugins.general.automation.AutomationPlugin;
import info.nightscout.androidaps.plugins.general.automation.triggers.Trigger;

public class ChooseTriggerDialog extends DialogFragment {

    public interface OnClickListener {
        void onClick(Trigger newTriggerObject);
    }

    private Unbinder mUnbinder;
    private OnClickListener mClickListener = null;

    @BindView(R.id.automation_chooseTriggerRadioGroup)
    RadioGroup mRadioGroup;

    public static ChooseTriggerDialog newInstance() {
        Bundle args = new Bundle();

        ChooseTriggerDialog fragment = new ChooseTriggerDialog();
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.automation_dialog_choose_trigger, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        for (Trigger t : AutomationPlugin.INSTANCE.getTriggerDummyObjects()) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(t.friendlyName());
            radioButton.setTag(t);
            mRadioGroup.addView(radioButton);
        }

        // restore checked radio button
        int checkedIndex = 0;
        if (savedInstanceState != null) {
            checkedIndex = savedInstanceState.getInt("checkedIndex");
        }

        ((RadioButton) mRadioGroup.getChildAt(checkedIndex)).setChecked(true);

        return view;
    }

    private int getCheckedIndex() {
        for (int i = 0; i < mRadioGroup.getChildCount(); ++i) {
            if (((RadioButton) mRadioGroup.getChildAt(i)).isChecked())
                return i;
        }
        return -1;
    }

    private Class getTriggerClass() {
        int radioButtonID = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = mRadioGroup.findViewById(radioButtonID);
        if (radioButton != null) {
            Object tag = radioButton.getTag();
            if (tag instanceof Trigger)
                return tag.getClass();
        }
        return null;
    }

    private Trigger instantiateTrigger() {
        Class triggerClass = getTriggerClass();
        if (triggerClass != null) {
            try {
                return (Trigger) triggerClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void setOnClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public void onDestroyView() {
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.ok)
    @SuppressWarnings("unused")
    public void onButtonOk(View view) {
        if (mClickListener != null)
            mClickListener.onClick(instantiateTrigger());

        dismiss();
    }

    @OnClick(R.id.cancel)
    @SuppressWarnings("unused")
    public void onButtonCancel(View view) {
        dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("checkedIndex", getCheckedIndex());
    }
}
