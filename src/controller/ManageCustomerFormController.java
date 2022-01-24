package controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import util.CustomerTM;
import util.DBConnection;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public void initialize() throws IOException, ClassNotFoundException {
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

        loadAllCustomers();

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCustomer) -> {
            if (selectedCustomer == null) return;

            disableControls(false);

            txtId.setText(selectedCustomer.getId());
            txtFirstName.setText(selectedCustomer.getFirstName());
            txtLastName.setText(selectedCustomer.getLastName());
            txtDob.setValue(selectedCustomer.getDob());

            if (selectedCustomer.getPicture() != null){
                txtPicture.setText("[PICTURE]");
            }

            lstTelephone.setItems(selectedCustomer.getTelephone());
        });
    }

    private void loadAllCustomers() throws ClassNotFoundException {
        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql = "SELECT * FROM customer";
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery(sql);
            PreparedStatement stmContact = connection.prepareStatement("SELECT telephone FROM contact WHERE customer_id=?");

            ObservableList<CustomerTM> customers = tblCustomers.getItems();
            customers.clear();
            while (rst.next()){

                Blob blobPic = rst.getBlob("picture");
                byte[] picture = blobPic.getBytes(1, (int) blobPic.length());

                stmContact.setString(1, rst.getString("id"));
                ResultSet rstContacts = stmContact.executeQuery();
                List<String> telephoneNumbers = new ArrayList<>();

                while (rstContacts.next()){
                    telephoneNumbers.add(rstContacts.getString("telephone"));
                }

                customers.add(new CustomerTM(
                        rst.getString("id"),
                        rst.getString("first_name"),
                        rst.getString("last_name"),
                        rst.getDate("dob").toLocalDate(),
                        picture,
                        telephoneNumbers
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load customers", ButtonType.OK).showAndWait();
            System.exit(0);
        }
    }

    private void disableControls(boolean disable){
        txtId.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtDob.setValue(null);
        txtPicture.clear();
        txtTelephone.clear();
        lstTelephone.setItems(FXCollections.observableArrayList());

        txtId.setDisable(disable);
        txtFirstName.setDisable(disable);
        txtLastName.setDisable(disable);
        txtDob.setDisable(disable);
        txtPicture.setDisable(disable);
        btnBrowse.setDisable(disable);
        txtTelephone.setDisable(disable);
        btnSaveCustomer.setDisable(disable);

        if (disable){
            lstTelephone.getSelectionModel().clearSelection();
            tblCustomers.getSelectionModel().clearSelection();
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

    public void btnSaveCustomer_OnAction(ActionEvent event) throws IOException, ClassNotFoundException {
        if (!isValidated()){
            return;
        }

        byte[] picture = Files.readAllBytes(Paths.get(txtPicture.getText()));
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            String sql = "INSERT INTO customer (id, first_name, last_name, dob, picture) VALUES (?,?,?,?,?)";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, txtId.getText());
            stm.setString(2, txtFirstName.getText());
            stm.setString(3, txtLastName.getText());
            stm.setDate(4, Date.valueOf(txtDob.getValue()));
            stm.setBlob(5, new SerialBlob(picture));
            stm.executeUpdate();

            PreparedStatement stmContact = connection.
                    prepareStatement("INSERT INTO contact (customer_id, telephone) VALUES (?,?)");
            for (String telephone : lstTelephone.getItems()) {
                stmContact.setString(1, txtId.getText());
                stmContact.setString(2, telephone);
                stmContact.addBatch();
            }
            if (!lstTelephone.getItems().isEmpty()){
                stmContact.executeBatch();
            }

            connection.commit();
        } catch (Throwable e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the customer, contact DEPPO", ButtonType.OK).show();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            btnSaveCustomer.requestFocus();
            return;
        }finally{
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        tblCustomers.getItems().add(new CustomerTM(
                txtId.getText(),
                txtFirstName.getText().trim(),
                txtLastName.getText().trim(),
                txtDob.getValue(),
                picture,
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
