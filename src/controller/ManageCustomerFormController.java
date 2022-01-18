package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
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
    public TableView<?> tblCustomers;

    public void initialize() throws IOException {
        btnRemove.setDisable(true);
        btnAdd.setDisable(true);

        txtTelephone.textProperty().addListener((observable, oldValue, newValue) ->
                btnAdd.setDisable(!newValue.trim().matches("\\d{3}-\\d{7}"))
        );

        lstTelephone.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnRemove.setDisable(newValue == null);
        });
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
        txtId.setText(generateNewId());
    }

    private String generateNewId(){
        if (tblCustomers.getItems().isEmpty()){
            return "C001";
        }else{
            /* Todo: After creating a table model */
//            ObservableList<?> customers = tblCustomers.getItems();
//            customers.get(customers.size() - 1).getId();
            return "C001";
        }
    }

    public void btnSaveCustomer_OnAction(ActionEvent event) {
        if (!isValidated()){
            return;
        }
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
        }else if(txtDob.getValue() == null || !LocalDate.now().minus(10, ChronoUnit.YEARS).isAfter(txtDob.getValue())){
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
