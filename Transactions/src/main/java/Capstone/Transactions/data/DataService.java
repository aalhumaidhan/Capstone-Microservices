package Capstone.Transactions.data;

import Capstone.Transactions.Enums.Methods;
import Capstone.Transactions.Enums.Status;
import Capstone.Transactions.entity.TransactionEntity;
import Capstone.Transactions.repository.TransactionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class DataService {

    private final TransactionRepository transactionRepository;
    private final ResourceLoader resourceLoader;

    public DataService(TransactionRepository transactionRepository, ResourceLoader resourceLoader) {
        this.transactionRepository = transactionRepository;
        this.resourceLoader = resourceLoader;
    }

    public void clearDatabase() {
        transactionRepository.deleteAll();
    }

    public String readJsonFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        try (Scanner scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    public void seedDatabase() {
        try {
            String jsonContent = readJsonFile("TransactionData.json");
            JSONObject jsonData = new JSONObject(jsonContent);

            JSONArray transactions = jsonData.getJSONArray("transactions");
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transactionJson = transactions.getJSONObject(i);
                TransactionEntity transactionEntity = new TransactionEntity();
                transactionEntity.setAmount(transactionJson.getDouble("amount"));
                transactionEntity.setDateTime(transactionJson.getString("dateTime"));
                transactionEntity.setStatus(Status.valueOf(transactionJson.getString("status")));
                transactionEntity.setMethod(Methods.valueOf(transactionJson.getString("method")));
                transactionEntity.setSenderId(transactionJson.getJSONObject("sender").getLong("id"));
                transactionEntity.setReceiverId(transactionJson.getJSONObject("receiver").getLong("id"));
                if (!transactionJson.isNull("associateId")) {
                    transactionEntity.setAssociateId(transactionJson.getJSONObject("associateId").getLong("id"));
                }

                transactionRepository.save(transactionEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to seed database", e);
        }
    }
}