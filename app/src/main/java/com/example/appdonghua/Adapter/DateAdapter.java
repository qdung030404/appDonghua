package com.example.appdonghua.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdonghua.Model.Date;
import com.example.appdonghua.R;

import java.util.List;

public class DateAdapter  extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<Date> datebtns;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onDateClick(Date date, int position);
    }

    public DateAdapter(List<Date> datebtns) {this.datebtns = datebtns;}
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public DateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_button, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull DateAdapter.ViewHolder holder, int position) {
        Date date = datebtns.get(position);
        holder.bind(date, position);


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
        public void bind(Date date, int position) {
            dateButton.setText(date.getName());
            dateButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDateClick(date, position);
                }
            });
        }
    }
}
