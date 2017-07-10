package com.hyperether.pipitit.notification.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hyperether.pipitit.R;
import com.hyperether.pipitit.data.CampaignMessage;
import com.hyperether.pipitit.notification.NotificationHandler;

/**
 * Base dialog for showing message
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 5/16/2017
 */
public class PipititBaseDialog extends Dialog {

    public PipititBaseDialog(@NonNull Context context, boolean cancel, CampaignMessage message) {
        super(context);
        setCanceledOnTouchOutside(cancel);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.base_dialog_view);
        setLayout(message);
        if (getWindow() != null)
            getWindow().setBackgroundDrawableResource(R.drawable.dlg_bckgnd);
    }

    public void setLayout(CampaignMessage message) {
        //TODO this need to be implemented if we want to have title
//        TextView textViewTitle = (TextView) findViewById(R.id.textViewSettingTitle);
//        textViewTitle.setText(message.getData().getMessage());

        TextView textViewSubtitle = (TextView) findViewById(R.id.textViewSettingSubtitle);
        textViewSubtitle.setText(message.getPayload().getMessage());

        Button buttonOk = (Button) findViewById(R.id.button);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (message.getNotificationID() > 0) {
            NotificationHandler.getInstance()
                    .removeNotification(getContext(), message.getNotificationID());
        }
    }
}
