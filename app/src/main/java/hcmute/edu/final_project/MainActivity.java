package hcmute.edu.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messegerEditText;
    ImageButton sendButton;
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
        //okhttp
        messagerList.add(new Messager("Đợi xíu....",Messager.SEND_BY_BOT));


        String url = "http://api.brainshop.ai/get?bid=175452&key=UZNMMF4E8JMd8vAL&uid=[uid]&msg=" + question;

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