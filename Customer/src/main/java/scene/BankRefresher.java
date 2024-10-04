package scene;

import com.example.ABSServer.Utils.Constants;
import com.example.ABSServer.Utils.Decoder;
import com.example.ABSServer.Utils.http.HttpClientUtil;
import bank.Bank;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class BankRefresher extends TimerTask {
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<Bank> bankConsumer;
    private int requestNumber;
    private final boolean shouldUpdate;


    public BankRefresher(boolean shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<Bank> bankConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.bankConsumer = bankConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {

        if (!shouldUpdate) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + Constants.BANK + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(Constants.BANK, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String codedBank = response.body().string();
                //httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + codedBank);
                try {
                    Bank bank = (Bank)Decoder.fromString(codedBank);
                    bankConsumer.accept(bank);
                }catch(Exception ignored){}
            }
        });
    }
}
