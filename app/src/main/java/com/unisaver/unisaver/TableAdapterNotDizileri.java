package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableAdapterNotDizileri extends RecyclerView.Adapter<TableAdapterNotDizileri.TableViewHolderNotDizileri>{

    private List<GradingSystemEntity> data = new ArrayList<>();
    private int selectedPosition = -1;
    private boolean isScrolled = false;
    private int selectedSystemId = -1;
    private Context context;

    public TableAdapterNotDizileri(Context context) {
        this.context = context;
        restart();
    }

    public void restart() {
        new Thread(() -> {
            data = AppDataBase.getInstance(MainActivity.getAppContext())
                    .gradingSystemDao().getAllSystems();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isSelected) {
                    selectedSystemId = i;
                    break;
                }
            }
            if (selectedSystemId == -1) {
                data.get(0).isSelected = true;
                selectedSystemId = 0;
            }
            System.out.println(data.size());
            ((Activity) context).runOnUiThread(() -> {
                notifyDataSetChanged();
            });
        }).start();
    }

    @NonNull
    @Override
    public TableAdapterNotDizileri.TableViewHolderNotDizileri onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.harf_notu_row, parent, false);
        return new TableAdapterNotDizileri.TableViewHolderNotDizileri(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableAdapterNotDizileri.TableViewHolderNotDizileri holder, int position) {
        GradingSystemEntity system = data.get(position);
        holder.diziIsim.setText(system.name);

        holder.radio.setChecked(selectedSystemId == position);

        holder.radio.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            int previousSelected = selectedSystemId;

            if (previousSelected != clickedPosition) {
                selectedSystemId = clickedPosition;

                // Güncelle model
                data.get(previousSelected).isSelected = false;
                data.get(clickedPosition).isSelected = true;

                // UI güncelle
                notifyItemChanged(previousSelected);
                notifyItemChanged(clickedPosition);
            }
        });

        if (system.isDefault) {
            holder.silme.setVisibility(View.GONE);
            holder.silme.setEnabled(false);
            holder.goruntule.setEnabled(true);
        } else {
            holder.silme.setEnabled(true);
            holder.goruntule.setEnabled(true);
        }

        holder.goruntule.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotOzellestir.class);
            intent.putExtra("systemId", data.get(position).id);
            intent.putExtra("diziIsimDuzenleme", data.get(position).name);
            context.startActivity(intent);
        });

        holder.silme.setOnClickListener(v -> {
            if (!system.isDefault) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && !system.isDefault) {
                    new Thread(() -> {
                        AppDataBase.getInstance(MainActivity.getAppContext())
                                .gradingSystemDao().deleteSystem(system);

                        ((Activity) context).runOnUiThread(() -> {
                            data.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);

                            // Eğer silinen item seçiliyse, seçimi sıfırla
                            if (selectedSystemId == adapterPosition) {
                                selectedSystemId = 0;
                                notifyItemChanged(adapterPosition);
                            }
                        });
                    }).start();
                }
            }
        });
    }

    public List<GradingSystemEntity> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TableViewHolderNotDizileri extends RecyclerView.ViewHolder {
        RadioButton radio;
        TextView diziIsim;
        ImageButton goruntule;
        ImageButton silme;
        public TableViewHolderNotDizileri(@NonNull View itemView) {
            super(itemView);
            radio = itemView.findViewById(R.id.select);
            diziIsim = itemView.findViewById(R.id.diziIsim);
            goruntule = itemView.findViewById(R.id.goruntule);
            silme = itemView.findViewById(R.id.silme);
        }
    }

    public void isPositionVisible(RecyclerView recyclerView) {
        if (selectedPosition == -1) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        isScrolled = true;
        recyclerView.clearFocus();
        selectedPosition = -1;
    }
}
