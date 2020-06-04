package com.example.notetracker.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.example.notetracker.Database.Encryption;
import com.example.notetracker.MainActivity;
import com.example.notetracker.Model.Note;
import com.example.notetracker.Database.NoteDatabase;
import com.example.notetracker.ViewHolders.NoteViewHolder;
import com.example.notetracker.R;

import org.w3c.dom.Text;


import java.util.ArrayList;


public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Note> noteList;
    private ArrayList<Note> mArrayList;
    private NoteDatabase mDatabase;

    public NoteAdapter(Context context, ArrayList<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        this.mArrayList = noteList;
        mDatabase = new NoteDatabase(context);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notetracker_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        final Note notes = noteList.get(position);

        String passKey = "asd@Q#RS!%G5yi^UE%WT$$";
        try {
            holder.title.setText(Encryption.decTitle(notes.getTitle(), passKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            holder.description.setText(Encryption.decDesc(notes.getDescription(), passKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.noteEdit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                try {
                    editTaskDialog(notes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        holder.noteDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                mDatabase.noteDelete(notes.getId());
                deleteTaskDialog(notes);
//                ((Activity) context).finish();
//                context.startActivity(((Activity) context).getIntent());
            }
        });


    }


    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    noteList = mArrayList;
                } else {

                    ArrayList<Note> filteredList = new ArrayList<>();

                    for (Note notes : mArrayList) {
                        String passKey = "asd@Q#RS!%G5yi^UE%WT$$";

                        try {
                            if ((Encryption.decTitle(notes.getTitle(), passKey).contains(charString)
                                    ||
                                    (Encryption.decTitle(notes.getTitle(), passKey).toLowerCase().contains(charString))
                                    ||
                                    (Encryption.decTitle(notes.getTitle(), passKey).toUpperCase().contains(charString)))
                                    ||
                                    ((Encryption.decDesc(notes.getDescription(), passKey).contains(charString)
                                            ||
                                            (Encryption.decDesc(notes.getDescription(), passKey).toLowerCase().contains(charString))
                                            ||
                                            (Encryption.decDesc(notes.getDescription(), passKey).toUpperCase().contains(charString))))) {

                                filteredList.add(notes);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    noteList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = noteList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                noteList = (ArrayList<Note>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }


    private void editTaskDialog(final Note notes) throws Exception {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.activity_add_note, null);

        final EditText editTitle = (EditText) subView.findViewById(R.id.title_et);
        final EditText editDescription = (EditText) subView.findViewById(R.id.description_et);

        String passKey = "asd@Q#RS!%G5yi^UE%WT$$";
        if (notes != null) {
//            editTitle.setText(notes.getTitle());
//            editDescription.setText(notes.getDescription());

            editTitle.setText(Encryption.decTitle(notes.getTitle(), passKey));
            editDescription.setText(Encryption.decDesc(notes.getDescription(), passKey));

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit note");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("EDIT NOTE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String passKey = "asd@Q#RS!%G5yi^UE%WT$$";
                String title = editTitle.getText().toString();
                String description = editDescription.getText().toString();
//                editTitle.setText(Encryption.decTitle(notes.getTitle(), passKey));
//                editDescription.setText(Encryption.decDesc(notes.getDescription(), passKey));

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(context, "Something went wrong. Check your input values", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        mDatabase.noteUpdate(new Note(notes.getId(), title, description));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //refresh the activity
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private void deleteTaskDialog(final Note notes) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final TextView question = (TextView) subView.findViewById(R.id.question_tv);

        if (notes != null) {
            question.setText("are you sure?");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete note?");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("DELETE NOTE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                final String title = question.getText().toString();
////                final String description = editDescription.getText().toString();

                mDatabase.noteDelete(notes.getId());

                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }

        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

}
