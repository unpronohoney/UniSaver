package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableAdapterDizi extends RecyclerView.Adapter<TableAdapterDizi.TableViewHolderAdapterDizi> {
    private List<GradeMappingEntity> data = new ArrayList<>();
    private int selectedPosition = -1;
    private boolean isScrolled = false;
    private boolean isDefault = false;
    private GradingSystemDao dao;
    private int systemId;
    private Context context;

    public TableAdapterDizi(boolean isdef, int systemId, Context context) {
        this.context = context;
        isDefault = isdef;
        this.systemId = systemId;
        new Thread(()->{
            dao = AppDataBase.getInstance(MainActivity.getAppContext()).gradingSystemDao();
        }).start();
    }

    public void setData(List<GradeMappingEntity> mappings) {
        data.addAll(mappings);
    }

    public List<GradeMappingEntity> getData() {
        return data;
    }

    @NonNull
    @Override
    public TableAdapterDizi.TableViewHolderAdapterDizi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notlar_row, parent, false);
        return new TableAdapterDizi.TableViewHolderAdapterDizi(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableAdapterDizi.TableViewHolderAdapterDizi holder, int position) {
        GradeMappingEntity ent = data.get(position);
        if (isDefault) {
            holder.notNicki.setFocusable(false);
            holder.notNicki.setClickable(false);
            holder.notEtkisi.setFocusable(false);
            holder.notEtkisi.setClickable(false);
            holder.silme.setVisibility(View.GONE);
        }


        holder.notNicki.setText(ent.grade);
        holder.notNicki.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            holder.notNicki.requestFocus();
        });

        holder.notNicki.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (isScrolled) {
                    isScrolled = false;
                } else {
                    String nick = holder.notNicki.getText().toString();
                    new Thread(()-> {
                        List<GradeMappingEntity> mappings = dao.getMappingsForSystem(systemId);
                        boolean cont = true;
                        for (GradeMappingEntity map : mappings) {
                            if (nick.equals(map.grade)) {
                                cont = false;
                                break;
                            }
                        }
                        if (cont) {
                            ent.grade = nick;
                            dao.updateMapping(ent);
                        }
                        else {
                            ((Activity) context).runOnUiThread(()->{
                               Toast.makeText(context, context.getString(R.string.same_grade), Toast.LENGTH_SHORT).show();
                               holder.notNicki.setText(ent.grade);
                            });
                        }
                    }).start();
                }
            }
        });

        holder.notNicki.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Klavye kapat
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Focus'u kaldır
                v.clearFocus();
                return true;
            }
            return false;
        });


        holder.notEtkisi.setText(String.valueOf(ent.value));

        holder.notEtkisi.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            holder.notEtkisi.requestFocus();
        });

        holder.notEtkisi.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (isScrolled) {
                    isScrolled = false;
                } else {
                    ent.value = Double.parseDouble(holder.notEtkisi.getText().toString());
                    new Thread(()->{
                        dao.updateMapping(ent);
                    }).start();
                }
            }
        });

        holder.notEtkisi.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // Klavye kapat
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Focus'u kaldır
                v.clearFocus();
                return true;
            }
            return false;
        });

        holder.silme.setOnClickListener(v -> {
            new Thread(()->{
                dao.deleteMapping(ent);
            }).start();
            data.remove(position);
            notifyItemRemoved(position);
        });
    }

    public void notEkle(GradeMappingEntity ent) {
        data.add(ent);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class TableViewHolderAdapterDizi extends RecyclerView.ViewHolder {
        EditText notNicki, notEtkisi;
        ImageButton silme;
        public TableViewHolderAdapterDizi(@NonNull View itemView) {
            super(itemView);
            notNicki = itemView.findViewById(R.id.notNicki);
            notEtkisi = itemView.findViewById(R.id.notEtkisi);
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
