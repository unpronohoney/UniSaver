package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class NameInputBottomSheet extends BottomSheetDialogFragment {

    private EditText nameEditText;
    private Button nextButton;
    private Context context;

    public NameInputBottomSheet(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_name_input, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            if (!name.isEmpty()) {
                new Thread(()->{
                    List<GradingSystemEntity> systems = AppDataBase.getInstance(MainActivity.getAppContext()).gradingSystemDao().getAllSystems();
                    boolean cont = true;
                    for (GradingSystemEntity ent : systems) {
                        if (name.equals(ent.name)) {
                            cont = false;
                            break;
                        }
                    }
                    if (cont) {
                        Intent intent = new Intent(getActivity(), NotOzellestir.class);
                        intent.putExtra("diziIsim", name);
                        startActivity(intent);
                        super.dismiss();
                    } else {
                        ((Activity) context).runOnUiThread(()->{
                            Toast.makeText(context, getString(R.string.sys_err), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            } else {
                Toast.makeText(context, getString(R.string.sys_empty_name), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}