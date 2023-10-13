package com.GLocanicKT.locanic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> fileList;

    public CustomAdapter(Context context, List<String> fileList) {
        super(context, 0, fileList);
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        String file = fileList.get(position);

        TextView fileNameTextView = listItemView.findViewById(R.id.file_path);
        fileNameTextView.setText(file);

        TextView uploadDateTextView = listItemView.findViewById(R.id.upload_date);
        // Set upload date and time

        ImageView iconImageView = listItemView.findViewById(R.id.icon);
        // Set icon image based on file type

        return listItemView;
    }
}
