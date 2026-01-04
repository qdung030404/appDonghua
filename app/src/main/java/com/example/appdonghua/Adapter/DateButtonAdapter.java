package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.Date;
import com.example.appdonghua.R;

import java.util.List;

public class DateButtonAdapter extends RecyclerView.Adapter<DateButtonAdapter.ViewHolder> {
    private List<Date> datebtns;
    private OnItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onDateClick(Date date, int position);
    }

    public DateButtonAdapter(List<Date> datebtns) {
        this.datebtns = datebtns;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateButtonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_button, parent, false);

        int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;
        int itemCount = datebtns.size();
        int padding = (int) (20 * parent.getContext().getResources().getDisplayMetrics().density);
        int itemWidth = (screenWidth - padding * 2) / itemCount;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            params.width = itemWidth;
        }
        view.setLayoutParams(params);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateButtonAdapter.ViewHolder holder, int position) {
        Date date = datebtns.get(position);
        holder.bind(date, position, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return datebtns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button dateButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateButton = itemView.findViewById(R.id.date_Button);
        }

        public void bind(Date date, int position, boolean isSelected) {
            dateButton.setText(date.getName());

            dateButton.setSelected(isSelected);

            dateButton.setOnClickListener(v -> {
                if (listener != null) {

                    int oldPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();

                    notifyItemChanged(oldPosition);
                    notifyItemChanged(selectedPosition);

                    listener.onDateClick(date, position);
                }
            });
        }
    }
}