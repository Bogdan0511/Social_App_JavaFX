package com.example.socialnetwork_javafx;

import com.example.socialnetwork_javafx.domain.User;
import com.example.socialnetwork_javafx.domain.validators.FriendshipValidator;
import com.example.socialnetwork_javafx.domain.validators.UserValidator;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController {
    UserValidator userValidator = new UserValidator();
    FriendshipValidator friendshipValidator = new FriendshipValidator();

    UserDBRepo userDBRepo = new UserDBRepo(userValidator);
    FriendshipDBRepo friendshipDBRepo = new FriendshipDBRepo(friendshipValidator);
    RequestDBRepo requestDBRepo = new RequestDBRepo();
    Service service = Service.getInstance(userDBRepo,friendshipDBRepo,requestDBRepo);
    @FXML
    TextField emailText;
    @FXML
    PasswordField passwordText;

    @FXML
    public void login(ActionEvent event) throws IOException{
        String email = emailText.getText();
        String password = passwordText.getText();

        if(email.isEmpty() || password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Date invalide!");
            alert.setContentText("Emailul sau parola nu pot fi necompletate!");
            alert.show();

            emailText.clear();
            passwordText.clear();
        }

        User user = null;
        for (User _user : service.findAllUsers()) {
            if (Objects.equals(_user.getEmail(), email) && (Objects.equals(_user.getPassword(), password))) {
                user = _user;
            }
        }

        if (user != null) {
            service.setUser(user);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("UtilizatorView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Hello, " + user.getUsername() + "!");
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Date invalide!");
            alert.setContentText("Email sau parola gresita !");
            alert.show();

            emailText.clear();
            passwordText.clear();
        }
    }

    @FXML
    public void redirectToSignUp(MouseEvent event) throws  IOException{
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("CreateAccountView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        stage.setTitle("Sign Up");
        stage.setScene(scene);
        stage.show();
    }
}
