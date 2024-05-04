package com.bouzine.chatbot;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bouzine.chatbot.adapters.ChatBotAdapter;
import com.bouzine.chatbot.apis.BrainShopApi;
import com.bouzine.chatbot.models.BrainShopResponse;
import com.bouzine.chatbot.models.MessageModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private List<MessageModel> messages = new ArrayList<>();
    private EditText editTextUser;
    private ImageButton buttonSend;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUser = findViewById(R.id.edit_text);
        buttonSend = findViewById(R.id.send_button);
        recyclerView = findViewById(R.id.recycler_view);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.brainshop.ai/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        BrainShopApi brainShopApi = retrofit.create(BrainShopApi.class);
        ChatBotAdapter chatBotAdapter = new ChatBotAdapter(messages, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(chatBotAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        buttonSend.setOnClickListener(view -> {
           String msg = editTextUser.getText().toString();
           messages.add(new MessageModel(msg, "user"));
           chatBotAdapter.notifyDataSetChanged();
           String url = "http://api.brainshop.ai/get?bid=181824&key=Sup3DuiOCSLHyytL&uid=[uid]&msg=" + msg;
           Call<BrainShopResponse> response = brainShopApi.getResponse(url);
           editTextUser.setText("");
           response.enqueue(new Callback<BrainShopResponse>() {
               @Override
               public void onResponse(Call<BrainShopResponse> call, Response<BrainShopResponse> response) {
                   Log.i("Info", "Response: " + response.body().getCnt());
                   messages.add(new MessageModel(response.body().getCnt(), "bot"));
                   chatBotAdapter.notifyDataSetChanged();
               }

               @Override
               public void onFailure(Call<BrainShopResponse> call, Throwable t) {
                    Log.i("Error", "Error: " + t.getMessage());
               }
           });
        });
    }
}
