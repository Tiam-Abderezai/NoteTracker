package com.example.notetracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.notetracker.Adapters.NoteAdapter;
import com.example.notetracker.Database.Encryption;
import com.example.notetracker.Database.NoteDatabase;
import com.example.notetracker.Model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;

import android.widget.EditText;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private NoteDatabase mDatabase;
    private ArrayList<Note> allNotes = new ArrayList<>();
    private NoteAdapter mAdapter;
    LocalDateTime time = LocalDateTime.now();
    private String AES = "AES";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView noteView = (RecyclerView) findViewById(R.id.note_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        noteView.setLayoutManager(linearLayoutManager);
        noteView.setHasFixedSize(true);
        mDatabase = new NoteDatabase(this);
        allNotes = mDatabase.noteList();

        if (allNotes.size() > 0) {
            noteView.setVisibility(View.VISIBLE);
            mAdapter = new NoteAdapter(this, allNotes);
            noteView.setAdapter(mAdapter);

        } else {
            noteView.setVisibility(View.GONE);
            Toast.makeText(this, "No notes currently added. Please start adding notes!", Toast.LENGTH_LONG).show();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });

        // Generates time-stamp of when the app was launched
        try {
            String text = "NoteTracker app started at: " + time + "\n";
            FileOutputStream fos = openFileOutput("time-stamp.txt", Context.MODE_APPEND);
            fos.write(text.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.activity_add_note, null);

        final EditText fieldTitle = (EditText) subView.findViewById(R.id.title_et);
        final EditText fieldDescription = (EditText) subView.findViewById(R.id.description_et);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD NOTE", new DialogInterface.OnClickListener() {




            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = fieldTitle.getText().toString();
                String description = fieldDescription.getText().toString();


                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(MainActivity.this, "Please check your input.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        String passKey = "asd@Q#RS!%G5yi^UE%WT$$";
                        String encTitle = Encryption.encTitle(title, passKey);
                        String encDesc = Encryption.encDesc(description, passKey);

                        Note newNote = new Note(encTitle, encDesc);
                        mDatabase.noteAdd(newNote);
                        finish();
                        startActivity(getIntent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Action was canceled.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null)
                    mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
