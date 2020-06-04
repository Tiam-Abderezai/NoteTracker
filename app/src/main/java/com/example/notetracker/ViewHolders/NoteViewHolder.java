package com.example.notetracker.ViewHolders;

import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notetracker.MainActivity;
import com.example.notetracker.R;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView title, description;
    public ImageView noteDelete;
    public ImageView noteEdit;




    public NoteViewHolder(View itemView){
        super(itemView);


        title = (TextView) itemView.findViewById(R.id.title_tv);
        description = (TextView) itemView.findViewById(R.id.description_tv);
        noteDelete = (ImageView) itemView.findViewById(R.id.note_delete);
        noteEdit = (ImageView) itemView.findViewById(R.id.note_edit);
    }


}