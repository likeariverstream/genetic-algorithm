package algorithm;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class HelloController {
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menu;
    public MenuItem menuItem1;
    public MenuItem menuItem2;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onMenuItem1Click() {
        welcomeText.setText("Сергей Калачев");
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Genetic Application!");
    }

    @FXML
    public void onMenuItem2Click() {
        welcomeText.setText("Приложение для демонстрации генетического алгоритма");
    }
}