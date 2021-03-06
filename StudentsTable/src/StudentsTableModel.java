import javax.swing.table.*;
import javax.swing.event.*;
import javax.persistence.*;

import java.util.*;


public class StudentsTableModel  extends AbstractTableModel {
	List<Student> studentResultList;   // stores the model data in a List collection of type CourseList
	private static final String PERSISTENCE_UNIT_NAME = "PersistenceUnit";  // Used in persistence.xml
	private static EntityManagerFactory factory;    
	private EntityManager manager;		       
	private Student student;// represents the entity courselist

    // This field contains additional information about the results   
	private StudentsService studentService;
	private int numcols, numrows; // number of rows and columns
	
	StudentsTableModel() { 	    
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME); 	    
		manager = factory.createEntityManager(); 	    
		student = new Student(); 

		// read all the records from courselist 	   	    
		studentService = new StudentsService(manager); 	

		// update the number of rows and columns in the model 	         	 
		studentResultList = studentService.readAll(); 	     	 
		 numrows = studentResultList.size(); 	    
		 numcols = student.getNumberOfColumns();       
		}

	 // create a new table model using the existing data in list
	 public StudentsTableModel(List<Student> list, EntityManager em)  {
	    studentResultList = list;
	    numrows = studentResultList.size();
	    student = new Student();
	   	numcols = student.getNumberOfColumns();     
		manager = em;  
		studentService = new StudentsService(manager);
	 }
	 
	
	public int getRowCount() {
		return numrows;
	 }
	
	 // returns a count of the number of columns
	 public int getColumnCount() {
		return numcols;
	 }
	
	 
	 // returns the data at the given row and column number
	 public Object getValueAt(int row, int col) {
		try {
		  return studentResultList.get(row).getColumnData(col);
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	 }
	
	 // table cells are not editable
	 public boolean isCellEditable(int rowIndex, int colIndex) {
		return false;
	 }
	
	 public Class<?> getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	 }
	
	 // returns the name of the column
	 public String getColumnName(int col) {
		   try {
				return student.getColumnName(col);
			} catch (Exception err) {
	             return err.toString();
	       }             
	 }
	
	 // update the data in the given row and column to aValue
	 public void setValueAt(Object aValue, int row, int col) {
		//data[rowIndex][columnIndex] = (String) aValue;
		try {
		   Student element = studentResultList.get(row);
                   element.setColumnData(col, aValue); 
            fireTableCellUpdated(row, col);
		} catch(Exception err) {
			err.toString();
		}	
	 }
	
	 public List<Student> getList() {
		 return studentResultList;
	 }

	 public EntityManager getEntityManager() {
	      return manager;
	 }

	 public void addRow(Object[] array) {
		 EntityTransaction userTransaction = manager.getTransaction();  
		 userTransaction.begin();
		 Student newRecord = studentService.createStudent((String) array[0], (String) array[1], Integer.parseInt((String) array[2]), (String) array[3]);
		 userTransaction.commit();
			 
			// set the current row to rowIndex
		 studentResultList.add(newRecord);
	     int row = studentResultList.size();  
	     int col = 0;

	     // update the data in the model to the entries in array
	     for (Object data : array) {
	      	 setValueAt((String) data, row-1, col++);
	     }
	     
	     numrows++;
	 }	  
	 
	 public void updateRow(Object[] array) {
		 EntityTransaction userTransaction = manager.getTransaction();  
		 userTransaction.begin();
		 studentService.updateStudent((String) array[0], (String) array[1], Integer.parseInt((String) array[2]), (String) array[3]);
		 userTransaction.commit();
		 
		 fireTableDataChanged();
		
	 }	  
	
	 public List<Student> getStudent(String studentName) {
		 EntityTransaction userTransaction = manager.getTransaction();  
		 userTransaction.begin();
		 List<Student> student = studentService.getStudent(studentName);
		 userTransaction.commit();
		 
		 return student;
		 
		}	
	 
	/* public void updateScore(Object[] array) {
		 EntityTransaction userTransaction = manager.getTransaction();  
		 userTransaction.begin();
		 studentService.updateStudent((String) array[0], (String) array[1], Integer.parseInt((String) array[2]), (String) array[3]);
		 userTransaction.commit();
		 
		 fireTableDataChanged();
		
	 }	*/  
	 
	 public void deleteRow(Object studentID) {
		 EntityTransaction userTransaction = manager.getTransaction();  
		 userTransaction.begin();
		 studentService.deleteStudent((String) studentID);
		 userTransaction.commit();
			 
		 studentResultList = studentService.readAll();
		 numrows--;
	     
		 fireTableDataChanged();
	 }

	 

}
