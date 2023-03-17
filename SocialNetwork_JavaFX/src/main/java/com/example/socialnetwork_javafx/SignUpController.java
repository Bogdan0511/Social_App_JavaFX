package com.example.socialnetwork_javafx;

import com.example.socialnetwork_javafx.domain.User;
import com.example.socialnetwork_javafx.domain.validators.FriendshipValidator;
import com.example.socialnetwork_javafx.domain.validators.UserValidator;
import com.example.socialnetwork_javafx.exceptions.RepositoryException;
import com.example.socialnetwork_javafx.exceptions.ValidationException;
import com.example.socialnetwork_javafx.repository.FriendshipDBRepo;
import com.example.socialnetwork_javafx.repository.RequestDBRepo;
import com.example.socialnetwork_javafx.repository.UserDBRepo;
import com.example.socialnetwork_javafx.service.Service;
import com.example.socialnetwork_javafx.utils.events.UserEntityChangeEvent;
import com.example.socialnetwork_javafx.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SignUpController {
    UserValidator userValidator = new UserValidator();
    FriendshipValidator friendshipValidator = new FriendshipValidator();

    UserDBRepo userDBRepo = new UserDBRepo(userValidator);
    FriendshipDBRepo friendshipDBRepo = new FriendshipDBRepo(friendshipValidator);
    RequestDBRepo requestDBRepo = new RequestDBRepo();
    Service service = Service.getInstance(userDBRepo,friendshipDBRepo,requestDBRepo);
    @FXML
    TextField idTextField;
    @FXML
    TextField usernameTextField;
    @FXML
    TextField emailTextField;
    @FXML
    PasswordField passwordPasswordField;

    public void signUp(ActionEvent event) throws IOException{
        int id = Integer.parseInt(idTextField.getText());
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordPasswordField.getText();

        if(id == 0 || username.isEmpty() || email.isEmpty() || password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Date invalide!");
            alert.setContentText("Campurile nu pot fi necompletate!");
            alert.show();
        }
        else{
            try{
                service.addUser(id, username, password, email);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Contul tau a fost creat cu succes!");
                alert.setContentText("Apasa conectare");
                alert.show();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Date invalide!");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        }
    }

    public void login(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("NetworkFX");
        stage.setScene(scene);
        stage.show();
    }
}
