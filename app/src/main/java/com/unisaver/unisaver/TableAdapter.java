package com.unisaver.unisaver;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private List<DersTrans> data; // Ders bilgileri
    private BelgeliHesap bh;
    Collection<String> validNotes;
    private TextView agno;
    private TextView kredis;
    private List<DersTrans> sonSilinen = new ArrayList<>();
    private boolean isChanged = false;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private boolean isScrolled = false;

    public TableAdapter(BelgeliHesap bh, TextView agno, TextView kredis) {
        this.bh = bh;
        this.data = bh.createLessonsTable();
        this.validNotes = bh.getMachine().getNotes().keySet();
        this.agno = agno;
        this.kredis = kredis;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_item, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        DersTrans ders = data.get(position);
        holder.dersNo.setText(String.valueOf(ders.getDersNo()));
        holder.dersAdi.setText(ders.getDersAdi());
        holder.dersKredi.setText(String.valueOf(ders.getDersKredi()));
        holder.dersNotu.setText(ders.getDersNotu());

        agno.setText(MainActivity.getAppContext().getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
        kredis.setText(MainActivity.getAppContext().getString(R.string.tot_cred, bh.getMachine().getComputedCred()));

        if (position == selectedPosition) {
            holder.dersNotu.requestFocus();
        } else {
            holder.dersNotu.clearFocus();
        }

        // Ders notu düzenlenebilir
        holder.dersNotu.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // Focus kaybedildiğinde
                if (isScrolled) {
                    isScrolled = false;
                } else {
                    String newNote = holder.dersNotu.getText().toString();
                    boolean isValid = false;
                    for (String note : validNotes) {
                        if (newNote.equals(note)) {
                            isValid = true;
                            break;
                        }
                    }
                    if (!isValid) {
                        Toast.makeText(MainActivity.getAppContext(), MainActivity.getAppContext().getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                        holder.dersNotu.setText(ders.getDersNotu()); // eski değeri geri al
                    } else {
                        isChanged = true;
                        ders.setDersNotu(newNote);// geçerli notu güncelle
                        bh.getMachine().updateNotes(newNote, position);
                        agno.setText(MainActivity.getAppContext().getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
                        kredis.setText(MainActivity.getAppContext().getString(R.string.tot_cred, bh.getMachine().getComputedCred()));
                    }
                }
            }
        });

        holder.dersNotu.setOnEditorActionListener((v, actionId, event) -> {
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

        holder.dersNotu.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            holder.dersNotu.requestFocus();
        });

        holder.dersSilButton.setOnClickListener(v -> {
            sonSilinen.add(data.get(position));
            data.remove(position);
            bh.getMachine().removeLesson(position);
            notifyItemRemoved(position);
            isChanged = true;
            for (int i = position; i < data.size(); i++) {
                data.get(i).setDersNo(i+1);
                notifyItemChanged(i);
            }
            agno.setText(MainActivity.getAppContext().getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
            kredis.setText(MainActivity.getAppContext().getString(R.string.tot_cred, bh.getMachine().getComputedCred()));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void basaAl() {
        System.out.println(31);
        if (isChanged) {
            isChanged = false;
            bh.getMachine().gerAl();
            data = bh.createLessonsTable();
            notifyDataSetChanged();
            agno.setText(MainActivity.getAppContext().getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
            kredis.setText(MainActivity.getAppContext().getString(R.string.tot_cred, bh.getMachine().getComputedCred()));
            sonSilinen.clear();
        }
    }
    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView dersNo, dersAdi, dersKredi;
        EditText dersNotu;
        ImageButton dersSilButton;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            dersNo = itemView.findViewById(R.id.dersNo);
            dersAdi = itemView.findViewById(R.id.dersAdi);
            dersKredi = itemView.findViewById(R.id.dersKredi);
            dersNotu = itemView.findViewById(R.id.dersNotu);
            dersSilButton = itemView.findViewById(R.id.dersSilButton);
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

    public void addDers(String ad, int cred, String not) {
        if (bh.getMachine().getComputedCred() > Integer.MAX_VALUE - cred) {
            Toast.makeText(MainActivity.getAppContext(), MainActivity.getAppContext().getString(R.string.cok_yuksek), Toast.LENGTH_SHORT).show();
        } else {
            isChanged = true;
            data.add(new DersTrans(data.size()+1, ad, cred, not));
            bh.getMachine().addLesson(ad, cred, not);
            notifyItemInserted(data.size() - 1);
            agno.setText(MainActivity.getAppContext().getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
            kredis.setText(MainActivity.getAppContext().getString(R.string.tot_cred, bh.getMachine().getComputedCred()));
        }
    }
    public void sonSilineniGeriAl() {
        if (sonSilinen.size() > 0) {
            addDers(sonSilinen.get(sonSilinen.size()-1).getDersAdi(), sonSilinen.get(sonSilinen.size()-1).getDersKredi(),
                    sonSilinen.get(sonSilinen.size()-1).getDersNotu());
            sonSilinen.remove(sonSilinen.size()-1);
            agno.setText(MainActivity.getAppContext().getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
            kredis.setText(MainActivity.getAppContext().getString(R.string.tot_cred, bh.getMachine().getComputedCred()));
        }
    }
}
