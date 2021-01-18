package com.succorfish.geofence.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.succorfish.geofence.R;
import com.succorfish.geofence.customObjects.ChattingObject;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
public class FragmentChattingAdapter extends RecyclerView.Adapter<FragmentChattingAdapter.FragmentChattingItemViewHolder> {
    private ArrayList<ChattingObject> chattingObjectList;
    private Context context;
   public FragmentChattingAdapter(ArrayList<ChattingObject> chattingObjects){
        this.chattingObjectList=chattingObjects;
    }
    @NonNull
    @Override
    public FragmentChattingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       context=parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragmentchatting_listitem, parent, false);
        return new FragmentChattingAdapter.FragmentChattingItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentChattingItemViewHolder fragmentChattingItemViewHolder, int position) {
        fragmentChattingItemViewHolder.bindDetails(chattingObjectList.get(position));
    }

    @Override
    public int getItemCount() {
        return chattingObjectList.size();
    }

    public class FragmentChattingItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       /**
         * chat item date
         */
       @BindView(R.id.chat_item_date)
       TextView item_date;
        /**
         * incomming
         */
       @BindView(R.id.text_message_incomming)
       TextView incommming_message_text;
       @BindView(R.id.text_message_incomming_time)
       TextView incommming_message_time_text;
        @BindView(R.id.incomming_message_layout)
        ConstraintLayout incommingMessage_layout;
        /**
         * outgoing
         */
       @BindView(R.id.text_message_outgoing)
       TextView outgoing_message_text;
       @BindView(R.id.text_message_outgoing_time)
       TextView outgoing_message_time_text;
       @BindView(R.id.outgoing_message_status)
       ImageView outgoing_message_status;
       @BindView(R.id.outgoing_message_layout)
       ConstraintLayout outgoingMessage_layout;


        public FragmentChattingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        void bindDetails(ChattingObject chattingObject){
            if(chattingObject.getMode().equalsIgnoreCase(context.getString(R.string.fragment_chatting_incomming_message))){
                /**
                 * UI handling.
                 */
                outgoing_message_text.setVisibility(View.INVISIBLE);
                outgoing_message_time_text.setVisibility(View.INVISIBLE);
                outgoing_message_status.setVisibility(View.INVISIBLE);
                outgoingMessage_layout.setVisibility(View.INVISIBLE);
                /**
                 * showing messageDetials.
                 */
                incommming_message_text.setText(chattingObject.getMessage());
                incommming_message_time_text.setText(chattingObject.getTime_chat());

            }else if(chattingObject.getMode().equalsIgnoreCase(context.getString(R.string.fragment_chatting_out_going_message))){
                /**
                 * UI handling.
                 */
                incommming_message_text.setVisibility(View.INVISIBLE);
                incommming_message_time_text.setVisibility(View.INVISIBLE);
                incommingMessage_layout.setVisibility(View.INVISIBLE);
                 /**
                 * showing messageDetials.
                 */
                outgoing_message_text.setText(chattingObject.getMessage());
                outgoing_message_time_text.setText(chattingObject.getTime_chat());
                if(chattingObject.getDelivery_status().equalsIgnoreCase(context.getString(R.string.fragment_chat_message_mesaage_invalid_channel_id))){
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.failed_message_icon));
                }
                else if(chattingObject.getDelivery_status().equalsIgnoreCase(context.getString(R.string.fragment_chat_message_mesaage_full_message_recieved_by_device))){
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.chata_singletick));
                }
                else if(chattingObject.getDelivery_status().equalsIgnoreCase(context.getString(R.string.fragment_chat_message_mesaage_message_sent_gsm))){
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.chat_double_tick_black));
                }
                else if(chattingObject.getDelivery_status().equalsIgnoreCase(context.getString(R.string.fragment_chat_message_mesaage_failed_message_gsm))){
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.chat_message_fail_gsm));
                }
                else if(chattingObject.getDelivery_status().equalsIgnoreCase(context.getString(R.string.fragment_chat_message_mesaage_send_to_iridium))){
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.chat_double_tick_green));
                }
                else if(chattingObject.getDelivery_status().equalsIgnoreCase(context.getString(R.string.fragment_chat_message_mesaage_server_sending_failed))){
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.chat_server_failed_message));
                }else {
                    outgoing_message_status.setImageDrawable(context.getDrawable(R.drawable.failed_message_icon));
                }
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
