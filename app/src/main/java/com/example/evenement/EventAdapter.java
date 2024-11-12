package com.example.evenement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final OnEventClickListener listener;

    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.nameTextView.setText(event.getName());
        holder.dateTextView.setText(event.getDateStart() + " to " + event.getDateEnd());
        holder.descriptionTextView.setText(event.getDescription());
        holder.discountTextView.setText(String.valueOf(event.getDiscount()));

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addEvent(Event event) {
        eventList.add(event);
        notifyItemInserted(eventList.size() - 1);
    }

    public void updateEvent(Event event) {
        int index = eventList.indexOf(event);
        if (index != -1) {
            eventList.set(index, event);
            notifyItemChanged(index);
        }
    }

    public void deleteEvent(Event event) {
        int index = eventList.indexOf(event);
        if (index != -1) {
            eventList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, descriptionTextView, discountTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.eventNameTextView);
            dateTextView = itemView.findViewById(R.id.eventDateTextView);
            descriptionTextView = itemView.findViewById(R.id.eventDescriptionTextView);
            discountTextView = itemView.findViewById(R.id.eventDiscountTextView);
        }
    }

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
}
