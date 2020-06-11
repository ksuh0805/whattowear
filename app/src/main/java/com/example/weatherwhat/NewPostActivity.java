package com.example.weatherwhat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActivity{

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;
    private ImageView iv_selectImage;
    private Uri selectedImageUri;
    private String imageUrl;
    private TextView order_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Log.d("TAG", "WAAA");

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        Log.d("TAG", "AAA");
        mTitleField = findViewById(R.id.fieldTitle);
        mBodyField = findViewById(R.id.fieldBody);
        mSubmitButton = findViewById(R.id.fabSubmitPost);
        iv_selectImage = findViewById(R.id.selectImage);
        order_image = findViewById(R.id.order_image2);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        iv_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            //saveInStorage();
                            //writeNewPost(userId, user.username, title, body, imageUrl);
                            saveInStorage2(userId, user.username, title, body);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.show();
        } else {
            mSubmitButton.hide();
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body, String imgUrl) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body, imgUrl);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        Log.d("TAG", String.valueOf(childUpdates));

        mDatabase.updateChildren(childUpdates);

    }
    // [END write_fan_out]
    public final int GET_GALLERY_IMAGE = 200;

    public void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Uri selectedImageUri;
            selectedImageUri = data.getData();
            iv_selectImage.setImageURI(selectedImageUri); // 골라온 이미지를 임시적으로 표시해주는 imageView에 set
            order_image.setVisibility(View.INVISIBLE);
        }
    }
    public void saveInStorage(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageRef = firebaseStorage.getReference().child(selectedImageUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(selectedImageUri);
        Log.d("TAG","sWAAA");
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d("TAG", "DWAAA");
                    throw task.getException();
                }

                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) { // 4.다운로드 Url을 받을 수 있는곳
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    Log.d("이미지",imageUrl);
                    // 성공했을 경우에 업로드한 것의 다운로드 url을 가져온다 여기서 이미지를 저장할 firestore에 바로 저장해주기.
                    //여기서 전역변수에 저장했다가 다른곳에서 저장하려면 자꾸 Url에 null이 저장된다. 그러므로 여기서 저장

                    //5. firestore에 데이터 저장하는 메서드 saveItem을 직접 정의해서 넣어줌
                    //item.setImage(imageUrl);

                }
            }
        });


    }
    public void saveInStorage2(final String userId, final String username, final String title, final String body){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageRef = firebaseStorage.getReference().child(selectedImageUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(selectedImageUri);
        Log.d("TAG","sWAAA");
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d("TAG", "DWAAA");
                    throw task.getException();
                }

                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) { // 4.다운로드 Url을 받을 수 있는곳
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    Log.d("이미지",imageUrl);
                    // 성공했을 경우에 업로드한 것의 다운로드 url을 가져온다 여기서 이미지를 저장할 firestore에 바로 저장해주기.
                    //여기서 전역변수에 저장했다가 다른곳에서 저장하려면 자꾸 Url에 null이 저장된다. 그러므로 여기서 저장

                    //5. firestore에 데이터 저장하는 메서드 saveItem을 직접 정의해서 넣어줌
                    writeNewPost(userId, username, title, body, imageUrl);

                }
            }
        });


    }

}
