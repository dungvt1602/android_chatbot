package hcmute.edu.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messegerEditText;
    ImageButton sendButton;
    List<Messager> messagerList;

    MessagerAdapter messagerAdapter;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS) // Thời gian timeout là 30 giây
            .build();


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

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt",question);
            jsonBody.put("max_tokens",2026);
            jsonBody.put("temperature",0);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Không thể trả lời tin của bạn 1 "+e.getMessage() );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(request.body().toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }else {
                    addResponse("Không thể trả lời tin của bạn 2 "+response.body().toString()+"  "+ response.code());
                }
            }
        });


    }
}