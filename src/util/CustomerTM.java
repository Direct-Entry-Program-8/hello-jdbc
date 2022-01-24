package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

public class CustomerTM {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private byte[] picture;
    private ObservableList<String> telephone;

    public CustomerTM() {
    }

    public CustomerTM(String id, String firstName, String lastName, LocalDate dob, byte[] picture, List<String> telephone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.picture = picture;
        this.telephone = FXCollections.observableArrayList(telephone);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public ObservableList<String> getTelephone() {
        return telephone;
    }

    public void setTelephone(List<String> telephone) {
        this.telephone = FXCollections.observableArrayList(telephone);
    }
}
