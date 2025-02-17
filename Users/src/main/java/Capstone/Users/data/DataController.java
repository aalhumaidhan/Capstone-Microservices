package Capstone.Users.data;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @RequestMapping("/clear")
    public void clearDatabase() {
        dataService.clearDatabase();
    }

    @RequestMapping("/seed")
    public void seedDatabase() {
        try {
            dataService.clearDatabase();
            dataService.seedUsers();
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed database");
        }
    }
}
