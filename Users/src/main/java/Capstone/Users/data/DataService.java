package Capstone.Users.data;

import Capstone.Users.entity.BusinessEntity;
import Capstone.Users.entity.PersonalEntity;
import Capstone.Users.entity.AssociateEntity;
import Capstone.Users.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@Service
public class DataService {

    private final UserRepository userRepository;

    private final ResourceLoader resourceLoader;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RestTemplate restTemplate;

    public DataService(UserRepository userRepository, ResourceLoader resourceLoader, BCryptPasswordEncoder bCryptPasswordEncoder, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.restTemplate = restTemplate;
    }

    public void clearDatabase() {
        userRepository.deleteAll();
    }

    public String readJsonFile(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        try (Scanner scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    public void seedUsers() {
        try {
            String jsonContent = readJsonFile("UserData.json");
            JSONObject jsonData = new JSONObject(jsonContent);

            // Save Personal Entities first
            JSONArray personals = jsonData.getJSONArray("personals");
            for (int i = 0; i < personals.length(); i++) {
                JSONObject personalJson = personals.getJSONObject(i);
                PersonalEntity personalEntity = new PersonalEntity();
                personalEntity.setUsername(personalJson.getString("username"));
                personalEntity.setEmail(personalJson.getString("email"));
                personalEntity.setPassword(bCryptPasswordEncoder.encode(personalJson.getString("password")));
                personalEntity.setFullName(personalJson.getString("fullName"));
                personalEntity.setAddress(personalJson.getString("address"));
                personalEntity.setPhoneNumber(personalJson.getString("phoneNumber"));
                personalEntity.setCivilId(personalJson.getString("civilId"));
                personalEntity.setWalletBalance(personalJson.getDouble("walletBalance"));
                personalEntity.setFaceID(personalJson.getString("faceID"));
                personalEntity.setBankAccountNumber(personalJson.getString("bankAccountNumber"));
                personalEntity.setTransactionLimit(personalJson.getDouble("transactionLimit"));

                userRepository.save(personalEntity);
            }

            // Save Business Entities and their Associates
            JSONArray businesses = jsonData.getJSONArray("businesses");
            for (int i = 0; i < businesses.length(); i++) {
                JSONObject businessJson = businesses.getJSONObject(i);
                BusinessEntity businessEntity = new BusinessEntity();
                businessEntity.setUsername(businessJson.getString("username"));
                businessEntity.setEmail(businessJson.getString("email"));
                businessEntity.setPassword(bCryptPasswordEncoder.encode(businessJson.getString("password")));
                businessEntity.setName(businessJson.getString("name"));
                businessEntity.setAddress(businessJson.getString("address"));
                businessEntity.setBusinessLicenseId(businessJson.getString("businessLicenseId"));
                businessEntity.setBankAccountNumber(businessJson.getString("bankAccountNumber"));

                userRepository.save(businessEntity);

                JSONArray associatesJson = businessJson.getJSONArray("associates");
                for (int j = 0; j < associatesJson.length(); j++) {
                    JSONObject associateJson = associatesJson.getJSONObject(j);
                    AssociateEntity associateEntity = new AssociateEntity();
                    associateEntity.setFullName(associateJson.getString("fullName"));
                    associateEntity.setName(businessEntity.getName());
                    associateEntity.setPhoneNumber(associateJson.getString("phoneNumber"));
                    associateEntity.setUsername(associateJson.getString("username"));
                    associateEntity.setEmail(associateJson.getString("email"));
                    associateEntity.setPassword(bCryptPasswordEncoder.encode(associateJson.getString("password")));
                    associateEntity.setAddress(associateJson.getString("address"));
                    associateEntity.setBankAccountNumber(businessEntity.getBankAccountNumber());
                    associateEntity.setBusinessLicenseId(businessEntity.getBusinessLicenseId());
                    associateEntity.setBusiness(businessEntity);
                    businessEntity.addAssociate(associateEntity);

                    userRepository.save(associateEntity);
                    userRepository.save(businessEntity);
                }
            }

            // Call the seedTransactions API
            String transactionServiceUrl = "http://transactions:8082/data/seed";
            restTemplate.postForEntity(transactionServiceUrl, null, String.class);

        } catch (IOException e) {
            throw new RuntimeException("Failed to seed database", e);
        }
    }
}