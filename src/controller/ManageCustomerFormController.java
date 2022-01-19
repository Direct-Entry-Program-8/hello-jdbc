package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import util.CustomerTM;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ManageCustomerFormController {

    public Button btnNewCustomer;
    public TextField txtId;
    public TextField txtFirstName;
    public TextField txtLastName;
    public DatePicker txtDob;
    public TextField txtPicture;
    public Button btnBrowse;
    public TextField txtTelephone;
    public Button btnAdd;
    public ListView<String> lstTelephone;
    public Button btnRemove;
    public Button btnSaveCustomer;
    public TableView<CustomerTM> tblCustomers;

    public void initialize() throws IOException {
        btnRemove.setDisable(true);
        btnAdd.setDisable(true);

        txtTelephone.textProperty().addListener((observable, oldValue, newValue) ->
                btnAdd.setDisable(!newValue.trim().matches("\\d{3}-\\d{7}"))
        );

        lstTelephone.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnRemove.setDisable(newValue == null);
        });

        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<CustomerTM, ImageView> colPicture = (TableColumn<CustomerTM, ImageView>) tblCustomers.getColumns().get(1);

        colPicture.setCellValueFactory(param -> {
            byte[] picture = param.getValue().getPicture();
            ByteArrayInputStream bais = new ByteArrayInputStream(picture);

            ImageView imageView = new ImageView(new Image(bais));
            imageView.setFitHeight(75);
            imageView.setFitWidth(75);
            return new ReadOnlyObjectWrapper<>(imageView);
        });

        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tblCustomers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tblCustomers.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("dob"));

        disableControls(true);
    }

    private void disableControls(boolean disable){
        txtId.setDisable(disable);
        txtFirstName.setDisable(disable);
        txtLastName.setDisable(disable);
        txtDob.setDisable(disable);
        txtPicture.setDisable(disable);
        btnBrowse.setDisable(disable);
        txtTelephone.setDisable(disable);
        btnSaveCustomer.setDisable(disable);
        lstTelephone.getSelectionModel().clearSelection();
        tblCustomers.getSelectionModel().clearSelection();

        if (disable){
            txtId.clear();
            txtFirstName.clear();
            txtLastName.clear();
            txtDob.setValue(null);
            txtPicture.clear();
            txtTelephone.clear();
            lstTelephone.getItems().clear();
        }
    }

    public void btnRemove_OnAction(ActionEvent event) {
        String selectedTelephoneNumber = lstTelephone.getSelectionModel().getSelectedItem();
        lstTelephone.getItems().remove(selectedTelephoneNumber);
        lstTelephone.getSelectionModel().clearSelection();
    }

    public void txtTelephone_OnAction(ActionEvent event) {
        btnAdd.fire();
    }

    public void btnAdd_OnAction(ActionEvent event) {
        lstTelephone.getSelectionModel().clearSelection();
        for (String telephone : lstTelephone.getItems()) {
            if (telephone.equals(txtTelephone.getText())){
                txtTelephone.selectAll();
                return;
            }
        }
        lstTelephone.getItems().add(txtTelephone.getText());
        txtTelephone.clear();
        txtTelephone.requestFocus();
    }


    public void btnBrowse_OnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("Images", "*.jpeg", "*.jpg", "*.gif", "*.png", "*.bmp"));
        fileChooser.setTitle("Select an image");
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        txtPicture.setText(file != null ? file.getAbsolutePath() : "");
    }


    public void btnNewCustomer_OnAction(ActionEvent event) {
        disableControls(false);
        txtId.setText(generateNewId());
        txtFirstName.requestFocus();
    }

    private String generateNewId(){
        if (tblCustomers.getItems().isEmpty()){
            return "C001";
        }else{
            ObservableList<CustomerTM> customers = tblCustomers.getItems();
            int lastCustomerId = Integer.parseInt(customers.get(customers.size() - 1).getId().replace("C", ""));
            return String.format("C%03d", (lastCustomerId + 1));
        }
    }

    public void btnSaveCustomer_OnAction(ActionEvent event) throws IOException {
        if (!isValidated()){
            return;
        }

        tblCustomers.getItems().add(new CustomerTM(
                txtId.getText(),
                txtFirstName.getText().trim(),
                txtLastName.getText().trim(),
                txtDob.getValue(),
                Files.readAllBytes(Paths.get(txtPicture.getText())),
                lstTelephone.getItems()
        ));

        disableControls(true);
        btnNewCustomer.requestFocus();
    }

    private boolean isValidated(){
        if (!txtFirstName.getText().matches("[A-Za-z ]+")){
            new Alert(Alert.AlertType.ERROR, "Invalid first name", ButtonType.OK).show();
            txtFirstName.requestFocus();
            return false;
        }else if (!txtLastName.getText().matches("[A-Za-z ]+")){
            new Alert(Alert.AlertType.ERROR, "Invalid last name", ButtonType.OK).show();
            txtLastName.requestFocus();
            return false;
        }else if(txtDob.getValue() == null ||
                !LocalDate.now().minus(10, ChronoUnit.YEARS).isAfter(txtDob.getValue())){
            new Alert(Alert.AlertType.ERROR, "Customer should be at least 10 years old", ButtonType.OK).show();
            txtDob.requestFocus();
            return false;
        }else if (txtPicture.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Customer should have a profile picture", ButtonType.OK).show();
            btnBrowse.requestFocus();
            return false;
        }else if (lstTelephone.getItems().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Customer should have at least one phone number",ButtonType.OK).show();
            txtTelephone.requestFocus();
            return false;
        }

        return true;
    }

}
