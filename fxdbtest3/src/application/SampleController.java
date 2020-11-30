package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;


public class SampleController {
	Connection conn;
	Statement stmt = null;
	PreparedStatement pst=null;
	ResultSet srs;
	ObservableList<Student> list;
	String index;

	@FXML
	private TableView<Student> tableContent;

	@FXML
	private TableColumn<Student, String> col_id;

	@FXML
	private TableColumn<Student, String> col_name;

	@FXML
	private TableColumn<Student, String> col_email;

	@FXML
	private TableColumn<Student, String> col_phone;

	@FXML
	private void initialize() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sampledb?serverTimezone=UTC", "root","brd901as-kim");
			System.out.println("DB 연결 완료");

		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 에러");
		} catch (SQLException e) {
			System.out.println("SQL 실행 에러");
		} 

		tablelookup();
	}
	@FXML
	private TextField txtid;

	@FXML
	private TextField txtname;

	@FXML
	private TextField txtemail;

	@FXML
	private TextField txtphone;
	
	public void cleartext() {
		txtid.setText("");
		txtname.setText("");
		txtemail.setText("");
		txtphone.setText("");
	}

	public void tablelookup() {
		try {
			//	stmt = conn.prepareStatement("select * from student");
			stmt = conn.createStatement();
			srs = stmt.executeQuery("select * from student");

			list = FXCollections.observableArrayList();
			//		ResultSetMetaData rsd = srs.getMetaData();
			//		int c = rsd.getColumnCount();
			while(srs.next()) {
				String r1 = srs.getString("id");
				String r2 = srs.getString("name");
				String r3 = srs.getString("email");
				String r4 = srs.getString("phone");

				Student student = new Student(r1,r2,r3,r4);	

				list.add(student);

				col_id.setCellValueFactory(new PropertyValueFactory<Student,String>("id"));
				col_name.setCellValueFactory(new PropertyValueFactory<Student,String>("name"));
				col_email.setCellValueFactory(new PropertyValueFactory<Student,String>("email"));
				col_phone.setCellValueFactory(new PropertyValueFactory<Student,String>("phone"));										

				tableContent.setItems(list);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@FXML
	void onClickAdd(ActionEvent event) {
		txtid.requestFocus();

		String r1 = txtid.getText();
		String r2 = txtname.getText();
		String r3 = txtemail.getText();
		String r4 = txtphone.getText();

		try {
			pst = conn.prepareStatement("insert into student (id,name, email, phone) values (?,?,?,?)");
			pst.setString(1, r1);
			pst.setString(2, r2);
			pst.setString(3, r3);
			pst.setString(4, r4);
			int k =pst.executeUpdate();

			if(k==1) {
				JOptionPane.showMessageDialog(null, "Add Student !!!");
			} else {
				JOptionPane.showMessageDialog(null, "Error!");
			}
		} catch(SQLIntegrityConstraintViolationException e) {
			JOptionPane.showMessageDialog(null, "Duplicate!");
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		tablelookup();
		cleartext();
	}
	
    @FXML
    void onClickClear(ActionEvent event) {
    	cleartext();
    }

	@FXML
	void onClickDelete(ActionEvent event) {
		int dialogresult = JOptionPane.showConfirmDialog(null, "Do you want to delete this student?");
		if(dialogresult == JOptionPane.YES_NO_OPTION)
		{
			try {
				pst = conn.prepareStatement("delete from student where id = ?");
				pst.setString(1, index);
				pst.executeUpdate();
				JOptionPane.showMessageDialog(null, "Student update!");
				tablelookup();						
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cleartext();
	}

	@FXML
	void onClickStop(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	void onClickUpdate(ActionEvent event) {
		String r1 = txtid.getText();
		String r2 = txtname.getText();
		String r3 = txtemail.getText();
		String r4 = txtphone.getText();

		try {
			pst = conn.prepareStatement("update student set name= ?, email=?,phone=? where id = ?");
			pst.setString(1, r2);
			pst.setString(2, r3);
			pst.setString(3, r4);
			pst.setString(4, r1);

			pst.executeUpdate();
			JOptionPane.showMessageDialog(null, "Student update!");
			cleartext();

			tablelookup();	

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	void onMouseClicked(MouseEvent event) {
		TableViewSelectionModel<Student> model = tableContent.getSelectionModel();

		Student s = (Student)model.getSelectedItem();
		txtid.setText(s.id);
		txtname.setText(s.name);
		txtemail.setText(s.email);
		txtphone.setText(s.phone);

		index = s.id;
	}

}
