package com.android.nightvision;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.serenegiant.common.UVCCameraHelper;
import com.serenegiant.usb.LogUtil;
import com.serenegiant.usb.Size;
import java.util.ArrayList;
import java.util.List;

/* compiled from: SettingsDialogFragment */
public class l extends DialogFragment implements OnSeekBarChangeListener, OnClickListener, OnCheckedChangeListener, OnTouchListener {

    /* renamed from: a reason: collision with root package name */
    private static final String f98a = "l";
    /* access modifiers changed from: private */

    /* renamed from: b reason: collision with root package name */
    public UVCCameraHelper f99b;
    private Context c;
    /* access modifiers changed from: private */
    public Activity d;
    private b e;
    private int f = -1;
    private View g;
    private SeekBar h;
    private SeekBar i;
    private Switch j;
    private Switch k;
    private TextView l;
    private TextView m;
    /* access modifiers changed from: private */
    public TextView n;
    private MBaseAdapter o;
    /* access modifiers changed from: private */
    public SharedPreferences p;
    /* access modifiers changed from: private */
    public Editor q;
    private MBroadcastReceiver mBroadcastReceiver;
    private boolean s = false;
    /* access modifiers changed from: private */
    public boolean t = false;

    /* compiled from: SettingsDialogFragment */
    class MBroadcastReceiver extends BroadcastReceiver {
        MBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("android.intent.action.BATTERY_CHANGED")) {
                boolean z = false;
                int intExtra = intent.getIntExtra("level", 0);
                l lVar = l.this;
                if (intExtra < 16) {
                    z = true;
                }
                lVar.t = z;
            }
        }
    }

    /* compiled from: SettingsDialogFragment */
    private final class MBaseAdapter extends BaseAdapter {

        /* renamed from: a reason: collision with root package name */
        private final LayoutInflater f101a;

        /* renamed from: b reason: collision with root package name */
        private final List<Size> f102b;

        MBaseAdapter(Context context, List<Size> list) {
            this.f101a = LayoutInflater.from(context);
            if (list == null) {
                list = new ArrayList<>();
            }
            this.f102b = list;
        }

        public int getCount() {
            return this.f102b.size();
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            c cVar;
            if (view == null) {
                cVar = new c(l.this, null);
                view2 = this.f101a.inflate(R.layout.preview_list_item, viewGroup, false);
                cVar.f103a = (TextView) view2.findViewById(R.id.preview_list_txt);
                view2.setTag(cVar);
            } else {
                view2 = view;
                cVar = (c) view.getTag();
            }
            cVar.f103a.setText(getItem(i).getPreviewSize());
            return view2;
        }

        public Size getItem(int i) {
            if (i < 0 || i >= this.f102b.size()) {
                return null;
            }
            return (Size) this.f102b.get(i);
        }
    }

    /* compiled from: SettingsDialogFragment */
    private class c {

        /* renamed from: a reason: collision with root package name */
        TextView f103a;

        private c() {
        }

        /* synthetic */ c(l lVar, k kVar) {
            this();
        }
    }

    private void f() {
        Builder builder = new Builder(getActivity());
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.preview_list, null);
        ListView listView = (ListView) inflate.findViewById(R.id.preview_list);
        this.o = new MBaseAdapter(this.c, this.f99b.getSupportedPreviewSizes());
        listView.setAdapter(this.o);
        builder.setView(inflate);
        AlertDialog create = builder.create();
        listView.setOnItemClickListener(new k(this, create));
        create.show();
        LayoutParams attributes = create.getWindow().getAttributes();
        attributes.width = a(this.c, 200.0f);
        create.getWindow().setAttributes(attributes);
    }

    private void g() {
        if (this.f99b.isCameraOpened()) {
            this.h.setProgress(this.f99b.getModelValue(-2147483647));
            this.i.setProgress(this.f99b.getModelValue(-2147483646));
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        int id = compoundButton.getId();
        if (id != R.id.settings_flash) {
            if (id == R.id.settings_sound) {
                Editor editor = this.q;
                if (editor != null) {
                    editor.putBoolean("camera_sound", z);
                    this.q.apply();
                }
            }
        } else if (this.e.a()) {
            this.e.a(z);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_close /*2131230838*/:
                d();
                return;
            case R.id.settings_reset /*2131230841*/:
                e();
                return;
            case R.id.settings_resolution /*2131230842*/:
                if (!this.d.getResources().getBoolean(R.bool.config_fixed_size_support) && this.f99b.getSupportedPreviewSizes().size() > 1) {
                    f();
                    return;
                }
                return;
            default:
                return;
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public Dialog onCreateDialog(Bundle bundle) {
        Builder builder = new Builder(getActivity());
        this.g = LayoutInflater.from(getActivity()).inflate(R.layout.settings_container, null);
        this.h = (SeekBar) this.g.findViewById(R.id.settings_brightness);
        this.i = (SeekBar) this.g.findViewById(R.id.settings_contrast);
        if (this.d.getResources().getBoolean(R.bool.config_property_setting_support)) { // TODO: 2020/12/14 这个资源都是在哪里设置的
            this.h.setOnSeekBarChangeListener(this);
            this.i.setOnSeekBarChangeListener(this);
        } else {
            this.h.setOnTouchListener(this);
            this.i.setOnTouchListener(this);
        }
        this.j = (Switch) this.g.findViewById(R.id.settings_flash);
        this.j.setOnCheckedChangeListener(this);
        this.k = (Switch) this.g.findViewById(R.id.settings_sound);
        this.k.setOnCheckedChangeListener(this);
        this.l = (TextView) this.g.findViewById(R.id.settings_close);
        this.l.setOnClickListener(this);
        this.m = (TextView) this.g.findViewById(R.id.settings_reset);
        this.m.setOnClickListener(this);
        this.n = (TextView) this.g.findViewById(R.id.settings_resolution);
        this.n.setOnClickListener(this);
        this.n.setText(this.p.getString("camera_pixel", "1280 x 720"));
        boolean z = false;
        if (!this.t) {
            Switch switchR = this.j;
            if (this.e.b() && this.e.c()) {
                z = true;
            }
            switchR.setChecked(z);
        } else {
            Constant.showToast(this.c, R.string.flash_low_battery_warning);
            this.j.setChecked(false);
            this.j.setEnabled(false);
        }
        this.k.setChecked(this.p.getBoolean("camera_sound", true));
        g();
        builder.setView(this.g);
        AlertDialog create = builder.create();
        create.setCancelable(true);
        create.setCanceledOnTouchOutside(true);
        return create;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void onPause() {
        super.onPause();
        d();
    }

    public void onProgressChanged(SeekBar seekBar, int i2, boolean z) {
        if (z && this.f99b.isCameraOpened() && this.f99b.checkSupportFlag(this.f)) {
            int i3 = this.f;
            switch (i3) {
                case -2147483647:
                case -2147483646:
                    this.f99b.setModelValue(i3, seekBar.getProgress());
                    return;
                default:
                    return;
            }
        }
    }

    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.0f;
        window.setAttributes(attributes);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        int id = seekBar.getId();
        if (id == R.id.settings_brightness) {
            this.f = -2147483647;
        } else if (id == R.id.settings_contrast) {
            this.f = -2147483646;
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    private void d() {
        dismiss();
        this.f = -1;
    }

    private void e() {
        if (this.f99b.isCameraOpened()) {
            Toast.makeText(this.c, R.string.settings_reset_toast, Toast.LENGTH_SHORT).show();
            this.h.setProgress(this.f99b.resetModelValue(-2147483647));
            this.i.setProgress(this.f99b.resetModelValue(-2147483646));
            this.j.setChecked(false);
            this.k.setChecked(true);
        }
        this.f = -1;
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        if (!this.s) {
            this.c.registerReceiver(this.mBroadcastReceiver, intentFilter);
            this.s = true;
        }
    }

    public void custShowDilaog() {
        try {
            if (!isVisible()) {
                show(this.d.getFragmentManager(), f98a);
            }
        } catch (IllegalStateException e2) {
            String str = f98a;
            StringBuilder sb = new StringBuilder();
            sb.append("show dialog error ");
            sb.append(e2);
            LogUtil.e(str, sb.toString());
        }
    }

    public void init(Context context, Activity activity, UVCCameraHelper uVCCameraHelper) {
        this.c = context;
        this.d = activity;
        this.f99b = uVCCameraHelper;
        this.p = this.c.getSharedPreferences("UVCCamera", 0);
        this.q = this.p.edit();
        this.mBroadcastReceiver = new MBroadcastReceiver();
        this.e = new b(this.c);
    }

    public void a() {
//        MBaseAdapter MBaseAdapterVar = this.e;
//        if (MBaseAdapterVar != null) {
//            MBaseAdapterVar.a(false);
//        } todo 重命名 导致的类错误
        b bVar = this.e;
        if (bVar != null) {
            bVar.a(false);
        }

        if (this.s) {
            this.c.unregisterReceiver(this.mBroadcastReceiver);
            this.s = false;
        }
    }

    public static int a(Context context, float f2) {
        return (int) ((f2 * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
