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

import com.succorfish.geofence.DateUtils.DateUtilsMyHelper;
import com.succorfish.geofence.R;
import com.succorfish.geofence.customObjects.ChattingObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        fragmentChattingItemViewHolder.bindDetails(chattingObjectList.get(position),position);
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
        void bindDetails(ChattingObject chattingObject,int position){
            if(chattingObject.getMode().equalsIgnoreCase(context.getString(R.string.fragment_chatting_incomming_message))){
                /**
                 * UI handling.
                 */
                incommming_message_text.setText(chattingObject.getMessage());
                incommming_message_time_text.setText(chattingObject.getTime_chat());
                incommingMessage_layout.setVisibility(View.VISIBLE);
                incommming_message_text.setVisibility(View.VISIBLE);
                incommming_message_time_text.setVisibility(View.VISIBLE);
                outgoing_message_text.setVisibility(View.INVISIBLE);
                outgoing_message_time_text.setVisibility(View.INVISIBLE);
                outgoing_message_status.setVisibility(View.INVISIBLE);
                outgoingMessage_layout.setVisibility(View.INVISIBLE);
                /**
                 * showing messageDetials.
                 */

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

               /* Date fristItem_date=new Date(Long.parseLong(chattingObject.getTimeStamp()));
                long previousTs = 0;
                Date secondItem_date=new Date(0);
                if(position>0){
                     secondItem_date=new Date(Long.parseLong(chattingObjectList.get(position-1).getTimeStamp()));
                }
                showTimeChatForEachItemIncomming_OutGoiing(fristItem_date,secondItem_date,item_date);*/



             /*   Date mDate = DateUtilsMyHelper.parse(chattingObject.getTimeStamp(), DateUtilsMyHelper.dateFormatStandard);
                long previousTs = 0;
                if (position > 0) {
                    ChattingObject chatHistoryPrevious = chattingObjectList.get(position - 1);
                    if (chatHistoryPrevious.getTimeStamp() != null && !chatHistoryPrevious.getTimeStamp().isEmpty()) {
                        Date mDatePrevious = DateUtilsMyHelper.parse(chatHistoryPrevious.getTimeStamp(), DateUtilsMyHelper.dateFormatStandard);
                        previousTs = mDatePrevious.getTime();
                    }
                }
                setTimeTextVisibility(mDate.getTime(), previousTs, item_date);*/

            }
        }

        @Override
        public void onClick(View v) {

        }

        private void showTimeChatForEachItemIncomming_OutGoiing(Date presentDate,Date previousDate,TextView itemTextViewForTimeChate){





           if(presentDate.compareTo(previousDate)==1){
               /**
                * Same Date
                */
               if(itemTextViewForTimeChate.getText().toString().length()==0){
                   itemTextViewForTimeChate.setVisibility(View.VISIBLE);
                   itemTextViewForTimeChate.setText(presentDate.toString());
               }else {
                   itemTextViewForTimeChate.setVisibility(View.INVISIBLE);
                   itemTextViewForTimeChate.setText("");
               }

           }else if(presentDate.compareTo(previousDate)==0){
               /**
                * Different Date
                */
               itemTextViewForTimeChate.setVisibility(View.VISIBLE);
               itemTextViewForTimeChate.setText(presentDate.toString());
           }
        }

        private void setTimeTextVisibility(long epochTime1, long epochTime2, TextView timeText) {
            if (epochTime2 == 0) {
                Calendar calSendTime = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
                calSendTime.setTimeInMillis(epochTime1);
                timeText.setVisibility(View.VISIBLE);
                if (now.get(Calendar.DATE) == calSendTime.get(Calendar.DATE) && now.get(Calendar.MONTH) == calSendTime.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == calSendTime.get(Calendar.DAY_OF_MONTH)) {
                    timeText.setText("Today");
                } else if (now.get(Calendar.DATE) - calSendTime.get(Calendar.DATE) == 1 && now.get(Calendar.MONTH) == calSendTime.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == calSendTime.get(Calendar.DAY_OF_MONTH)) {
                    timeText.setText("Yesterday");
                } else {
                    timeText.setText(DateUtilsMyHelper.formatDate(calSendTime.getTime(), DateUtilsMyHelper.dateFormatYearMonthDay));
                }
            } else {
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTimeInMillis(epochTime1);
                cal2.setTimeInMillis(epochTime2);

                boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                        cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

                if (sameDay) {
                    timeText.setVisibility(View.GONE);
                    timeText.setText("");
                } else {
                    timeText.setVisibility(View.VISIBLE);
//                    timeText.setText(mDateFormatDisplay.format(new Date(ts1)));
                    Calendar now = Calendar.getInstance();
                    if (now.get(Calendar.DATE) == cal1.get(Calendar.DATE) &&
                            now.get(Calendar.MONTH) == cal1.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) {
                        timeText.setText("Today");
                    } else if (now.get(Calendar.DATE) - cal1.get(Calendar.DATE) == 1 &&
                            now.get(Calendar.MONTH) == cal1.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == cal1.get(Calendar.DAY_OF_MONTH)) {
                        timeText.setText("Yesterday");
                    } else {
                        timeText.setText(DateUtilsMyHelper.formatDate(cal1.getTime(), DateUtilsMyHelper.dateFormatYearMonthDay));
                    }
                }
            }
        }
    }
}
