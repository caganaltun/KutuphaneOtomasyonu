package cagan.controller;

import cagan.dao.Database;
import cagan.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainController {

    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private ComboBox<String> comboCategory;
    @FXML private TextField txtPageCount;
    @FXML private TextField txtSearch;

    @FXML private Button btnAdd;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;

    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, Integer> colPageCount;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
         colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPageCount.setCellValueFactory(new PropertyValueFactory<>("pageCount"));

        comboCategory.getItems().addAll("Roman", "Tarih", "Bilim", "Felsefe", "Çizgi Roman", "Eğitim", "Genel", "Din", "Kişisel Gelişim", "Biyografi");

        loadBooksFromDatabase();

        FilteredList<Book> filteredData = new FilteredList<>(bookList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (book.getCategory().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (String.valueOf(book.getPageCount()).contains(lowerCaseFilter)) return true;

                return false;
            });
        });

        SortedList<Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableBooks.comparatorProperty());
        tableBooks.setItems(sortedData);

        tableBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtTitle.setText(newSelection.getTitle());
                txtAuthor.setText(newSelection.getAuthor());
                comboCategory.setValue(newSelection.getCategory());
                txtPageCount.setText(String.valueOf(newSelection.getPageCount()));
            }
        });
    }

    @FXML
    public void handleAddButton() {
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String category = comboCategory.getValue();
        String pageText = txtPageCount.getText();

        if (title.isEmpty() || author.isEmpty() || category == null || pageText.isEmpty()) {
            showAlert("Hata", "Lütfen tüm alanları doldurun!");
            return;
        }

        int pageCount = 0;
        try {
            pageCount = Integer.parseInt(pageText);
        } catch (NumberFormatException e) {
            showAlert("Hata", "Sayfa sayısı sadece rakam olabilir!");
            return;
        }

        String sql = "INSERT INTO books (title, author, category, page_count, status) VALUES (?, ?, ?, ?, 'RAFT')";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, category);
            pstmt.setInt(4, pageCount);
            pstmt.executeUpdate();

            clearFields();
            loadBooksFromDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Ekleme başarısız: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdateButton() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Uyarı", "Lütfen güncellemek için bir kitap seçin.");
            return;
        }

        String newTitle = txtTitle.getText();
        String newAuthor = txtAuthor.getText();
        String newCategory = comboCategory.getValue();
        String newPageText = txtPageCount.getText();

        if (newTitle.isEmpty() || newAuthor.isEmpty() || newCategory == null || newPageText.isEmpty()) {
            showAlert("Hata", "Alanlar boş olamaz!");
            return;
        }

        int newPageCount = 0;
        try {
            newPageCount = Integer.parseInt(newPageText);
        } catch (NumberFormatException e) {
            showAlert("Hata", "Sayfa sayısı rakam olmalı!");
            return;
        }

        String sql = "UPDATE books SET title = ?, author = ?, category = ?, page_count = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTitle);
            pstmt.setString(2, newAuthor);
            pstmt.setString(3, newCategory);
            pstmt.setInt(4, newPageCount);
            pstmt.setInt(5, selectedBook.getId());

            pstmt.executeUpdate();

            clearFields();
            loadBooksFromDatabase();
            showAlert("Başarılı", "Kitap güncellendi!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteButton() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Uyarı", "Lütfen silmek için bir kitap seçin.");
            return;
        }

        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedBook.getId());
            pstmt.executeUpdate();
            loadBooksFromDatabase();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBooksFromDatabase() {
        bookList.clear();
        String sql = "SELECT * FROM books";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String cat = rs.getString("category");
                if (cat == null) cat = "Genel";

                bookList.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        cat,
                        rs.getInt("page_count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtAuthor.clear();
        txtPageCount.clear();
        comboCategory.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}