package hcmute.edu.final_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hcmute.edu.final_project.ml.MobilenetV110224Quant;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messegerEditText;
    ImageButton sendButton , selectImage;
    List<Messager> messagerList;


    MessagerAdapter messagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messagerList = new ArrayList<>();



        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messegerEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        selectImage = findViewById(R.id.selectImage);

        // setup recycler view
        messagerAdapter = new MessagerAdapter(messagerList);
        recyclerView.setAdapter(messagerAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        //xử lý sự kiện gửi
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question  = messegerEditText.getText().toString();
                Toast.makeText(MainActivity.this ,question,Toast.LENGTH_LONG).show();
                addToChat(question,Messager.SEND_BY_ME);
                messegerEditText.setText("");
                callApi(question);
                welcomeTextView.setVisibility(View.GONE);

            }
        });


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent() ;
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 10)
        {
            Uri uri = data.getData();
            try {

                String[] labels = new String[1001];

                int cnt = 0;
                try {
                    BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(getAssets().open("label.txt")));
                    String line = bufferedReader.readLine();
                    while(line!= null)
                    {
                        labels[cnt] = line;
                        cnt++;
                        line = bufferedReader.readLine();
                    }
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , uri);

                messagerList.add(new Messager(Messager.SEND_BY_ME, bitmap));
                messagerAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messagerAdapter.getItemCount());

                MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(MainActivity.this);

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

                bitmap = Bitmap.createScaledBitmap(bitmap ,224,224, true);

                inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                // Runs model inference and gets result.
                MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                String text = labels[getMax(outputFeature0.getFloatArray())];

                // Releases model resources if no longer used.
                model.close();


            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    int getMax(float[] arr){
            int max = 0;
            for (int i =0 ;i <arr.length ; i++)
            {
                if(arr[i] > arr[max] )
                    max = i;
            }
            return max;
    }

    void addToChat(String message , String sendBy){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagerList.add(new Messager(message,sendBy));
                messagerAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messagerAdapter.getItemCount());
            }
        });

    }

    void addResponse(String response){
        messagerList.remove(messagerList.size()-1);
        addToChat(response , Messager.SEND_BY_BOT);
    }

    void callApi(String question){


        messagerList.add(new Messager("Đợi xíu....",Messager.SEND_BY_BOT));


        String url = "http://api.brainshop.ai/get?bid=175452&key=UZNMMF4E8JMd8vAL&uid=[uid]&msg="+question;

        // creating a variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // on below line we are making a json object request for a get request and passing our url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // in on response method we are extracting data
                    // from json response and adding this response to our array list.
                    String botResponse = response.getString("cnt");
                    addResponse(botResponse);

                    // notifying our adapter as data changed.
                    messagerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // handling error response from bot.
                    messagerList.remove(messagerList.size()-1);
                    messagerList.add(new Messager("No response", Messager.SEND_BY_BOT));
                    messagerAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling.
                messagerList.remove(messagerList.size()-1);
                messagerList.add(new Messager("Không tìm thấy", Messager.SEND_BY_BOT));
                Toast.makeText(MainActivity.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
            }
        });

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest);
    }

}