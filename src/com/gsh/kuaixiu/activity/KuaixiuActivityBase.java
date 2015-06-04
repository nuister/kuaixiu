package com.gsh.kuaixiu.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsh.kuaixiu.R;

/**
 * @author Tan Chunmao
 */
public class KuaixiuActivityBase extends com.gsh.base.activity.ActivityBase {
    private RightAction rightAction;

    protected void setTitleBar(String title, String rightAction) {
        setTitleBar(false, title, RightAction.TEXT, rightAction);
    }

    protected void setTitleBar(String title) {
        setTitleBar(false, title, RightAction.NONE, -1);
    }

    protected void setTitleBar(boolean hideBack, String title) {
        setTitleBar(hideBack, title, RightAction.NONE, -1);
    }

    protected void setTitleBar(int titleId) {
        setTitleBar(false, titleId, RightAction.NONE, -1);
    }

    protected void setTitleBar(boolean hideBack, int title, RightAction rightAction, int actionRes) {
        setTitleBar(hideBack, getString(title), rightAction, actionRes);
    }

    protected void setTitleBar(boolean hideBack, int title, RightAction rightAction, String actionText) {
        setTitleBar(hideBack, getString(title), rightAction, actionText);
    }

    protected void setTitleBar(boolean hideBack, String title, RightAction rightAction, String actionText) {
        /*left button*/
        if (hideBack) {
            findViewById(R.id.btn_title_back).setClickable(false);
            findViewById(R.id.image_back).setVisibility(View.GONE);
            int padding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            findViewById(R.id.text_title).setPadding(padding, 0, 0, 0);
        } else {
            findViewById(R.id.image_back).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftActionPressed();
                }
            });
        }

        /*title*/
        ((TextView) findViewById(R.id.text_title)).setText(title);

        /*right button*/
        this.rightAction = rightAction;
        final View iconAction = findViewById(R.id.title_bar_icon_action);
        final View textAction = findViewById(R.id.title_bar_text_action);
        if (rightAction == RightAction.NONE) {
            iconAction.setVisibility(View.GONE);
            textAction.setVisibility(View.GONE);
        } else {
            iconAction.setVisibility(RightAction.ICON == rightAction ? View.VISIBLE : View.GONE);
            textAction.setVisibility(RightAction.TEXT == rightAction ? View.VISIBLE : View.GONE);
            if (RightAction.ICON == rightAction) {
                iconAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            } else if (RightAction.TEXT == rightAction) {
                ((TextView) findViewById(R.id.title_bar_action_text)).setText(actionText);
                textAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            }
        }
        enableRightAction(isRightActionEnabled());
    }

    public void showRightAction(boolean on) {
        findViewById(R.id.title_bar_icon_action).setVisibility(on ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.title_bar_text_action).setVisibility(on ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setTitleBar(boolean hideBack, String title, RightAction rightAction, int actionRes) {
        /*left button*/
        if (hideBack) {
            findViewById(R.id.btn_title_back).setClickable(false);
            findViewById(R.id.image_back).setVisibility(View.GONE);
            int padding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            findViewById(R.id.text_title).setPadding(padding, 0, 0, 0);
        } else {
            findViewById(R.id.image_back).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftActionPressed();
                }
            });
        }

        /*title*/
        ((TextView) findViewById(R.id.text_title)).setText(title);

        /*right button*/
        this.rightAction = rightAction;
        final View iconAction = findViewById(R.id.title_bar_icon_action);
        final View textAction = findViewById(R.id.title_bar_text_action);
        if (rightAction == RightAction.NONE) {
            iconAction.setVisibility(View.GONE);
            textAction.setVisibility(View.GONE);
        } else {
            iconAction.setVisibility(RightAction.ICON == rightAction ? View.VISIBLE : View.GONE);
            textAction.setVisibility(RightAction.TEXT == rightAction ? View.VISIBLE : View.GONE);
            if (RightAction.ICON == rightAction) {
                ((ImageView) findViewById(R.id.title_bar_action_image)).setImageResource(actionRes);
                iconAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            } else if (RightAction.TEXT == rightAction) {
                ((TextView) findViewById(R.id.title_bar_action_text)).setText(actionRes);
                textAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            }
        }
        enableRightAction(isRightActionEnabled());
    }

    protected boolean isRightActionEnabled() {
        return true;
    }

    private void enableRightAction(boolean enable) {
        if (rightAction == RightAction.TEXT) {
            findViewById(R.id.title_bar_text_action).setEnabled(enable);
            findViewById(R.id.title_bar_action_text).setEnabled(enable);
        } else if (rightAction == RightAction.ICON) {
            findViewById(R.id.title_bar_icon_action).setEnabled(enable);
            findViewById(R.id.title_bar_action_image).setEnabled(enable);
        }
    }

    protected void onLeftActionPressed() {
        onBackPressed();
    }

    protected void onRightActionPressed() {

    }

    public enum RightAction {
        ICON, TEXT, NONE
    }
}

