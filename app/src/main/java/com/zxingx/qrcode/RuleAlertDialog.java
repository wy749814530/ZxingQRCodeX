package com.zxingx.qrcode;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by WIN on 2017/12/1.
 */

public class RuleAlertDialog {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private TextView txt_msg;
    private Button btn_neg;
    private Button btn_pos;
    private View img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    public RuleAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public RuleAlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rule, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = (View) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.RuleAlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
       /* lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.8)
                , LayoutParams.WRAP_CONTENT));*/
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int width = display.getWidth() < display.getHeight() ? display.getWidth() : display.getHeight();
        lp.width = (int) (width * 0.8);

        return this;
    }

    public RuleAlertDialog setDismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        return this;
    }

    public RuleAlertDialog setTitle(String title) {

        if (title == null || "".equals(title)) {
            showTitle = false;
            txt_title.setText("");
        } else {
            showTitle = true;
            txt_title.setText(title);
        }
        return this;
    }

    public RuleAlertDialog setMsg(String msg) {

        if (msg == null || "".equals(msg)) {
            showMsg = false;
            txt_msg.setText("");
        } else {
            showMsg = true;
            txt_msg.setText(msg);
        }
        return this;
    }

    public RuleAlertDialog setMsg(int msgId) {
        String msg = context.getString(msgId);
        if (msg == null || "".equals(msg)) {
            showMsg = false;
            txt_msg.setText("");
        } else {
            showMsg = true;
            txt_msg.setText(msg);
        }
        return this;
    }

    //Gravity.CENTER, Gravity.LEFT, Gravity.RIGHT
    public RuleAlertDialog setMsgAlignStyle(int gravity) {
        txt_msg.setGravity(gravity);
        return this;
    }

    public RuleAlertDialog setTitleAlignStyle(int gravity) {
        txt_title.setGravity(gravity);
        return this;
    }

    public RuleAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public RuleAlertDialog setPositiveButton(CharSequence text, final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText(context.getString(R.string.label_ok));
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    public RuleAlertDialog setNegativeButton(CharSequence text, final OnClickListener listener) {
        showNegBtn = true;
        if (text == null) {
            btn_neg.setVisibility(View.GONE);
            return this;
        } else if ("".equals(text)) {
            btn_neg.setText(context.getString(R.string.label_cancel));
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    public RuleAlertDialog setNegativeBtnColor(int res) {
        btn_neg.setTextColor(res);
        return this;
    }

    public RuleAlertDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        dialog.setOnDismissListener(dismissListener);
        return this;
    }

    public void setDis() {//防止窗口泄露
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public RuleAlertDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        dialog.setOnKeyListener(onKeyListener);
        return this;
    }

    public RuleAlertDialog setCanceledOnTouchOutside(boolean b) {
        dialog.setCanceledOnTouchOutside(b);
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText(context.getString(R.string.tip_title));
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText(context.getString(R.string.label_ok));
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.ruledialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.ruledialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.ruledialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.ruledialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.ruledialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
