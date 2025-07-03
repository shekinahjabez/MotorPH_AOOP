/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.payroll.UI;

import com.payroll.subdomain.ComboItem;
import com.payroll.domain.IT;
import com.payroll.domain.Person;
import com.payroll.domain.Employee;
import com.payroll.subdomain.EmployeePosition;
import com.payroll.subdomain.EmployeeStatus;
import com.payroll.domain.LeaveBalance;
import com.payroll.domain.HR;
import com.payroll.DAO.HRDAO;
import com.payroll.DAO.ITDAO;
import com.payroll.DAO.EmployeeDAO;
import com.payroll.DAO.FinanceDAO;
import com.payroll.table.TableActionCellEditor;
import com.payroll.util.DatabaseConnection;
import com.payroll.table.TableActionCellRender;
import com.payroll.table.TableActionEvent;
import com.payroll.util.ReportGenerator;
import java.awt.CardLayout;
import java.awt.Color;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang3.StringUtils;
import javax.swing.JTable;


/**
 *
 * @author leniejoice
 */


public class HRDashboard extends javax.swing.JFrame {
    private DatabaseConnection dbConnection;
    private CardLayout cardLayout;
    private IT empAccount;
    private ITDAO empAccountService;
    private HRDAO hrService;
    private FinanceDAO payrollService;
    private EmployeeDAO employeeService;
    private Integer employeeSearchID;
    private long validatedPagibig;
    private long validatedPhilhealth;
    private ReportGenerator reportGenerator;
    private int currentEmployeeId; 
    
    public HRDashboard(IT empAccount) {
        initComponents();
        cardLayout = (CardLayout) (mphCards.getLayout());
        this.empAccount = empAccount;
        this.reportGenerator = new ReportGenerator();
        this.currentEmployeeId = empAccount.getEmpDetails().getEmpID(); 
        updateUserLabels(empAccount);

        try {
            Connection connection = DatabaseConnection.getConnection();
            this.empAccountService = new ITDAO(connection);
            this.hrService = new HRDAO(connection);
            this.payrollService = new FinanceDAO(connection);
            this.employeeService = new EmployeeDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace(); // Optionally use JOptionPane for user-friendly error
        }
        TableActionEvent event = new TableActionEvent(){
            @Override
            public void onApprove(int row){
                DefaultTableModel model = (DefaultTableModel) leaveManagementTable.getModel();
                int leaveId = (int) model.getValueAt(row, 0); 
                int empID = (int) model.getValueAt(row, 1); 
                employeeService.updateLeaveRequestStatus(HR.LeaveStatus.APPROVED, leaveId, currentEmployeeId);
                updateLeaveBalance(empID);
                refreshLeaveManagementTable();
            }

            @Override
            public void onDecline(int row) {
                DefaultTableModel model = (DefaultTableModel) leaveManagementTable.getModel();
                int leaveId = (int) model.getValueAt(row, 0);
                int empID = (int) model.getValueAt(row, 1); 
                employeeService.updateLeaveRequestStatus(HR.LeaveStatus.DECLINED, leaveId, currentEmployeeId);
                updateLeaveBalance(empID);
                refreshLeaveManagementTable(); 
            }
        };
        leaveManagementTable.getColumnModel().getColumn(10).setCellRenderer(new TableActionCellRender());
        leaveManagementTable.getColumnModel().getColumn(10).setCellEditor(new TableActionCellEditor(event));
        loadAllPositions();
        loadAllStatus();
        loadAllEmployee();
        loadAllYears();
        loadAllMonths();
        
    }
    public HRDashboard(){
        
    }
    public IT getEmpAccount() {
        return empAccount;
    }
    
     public IT getUsername() {
        return empAccount;
    }

    public void setEmpAccount(IT empAccount) {
        this.empAccount = empAccount;
    }
    
    private void updateLeaveBalance(int empID){
        List<HR> leaveDetails =  employeeService.getLeavesByEmployee(empID);
        LeaveBalance balance = new LeaveBalance();
        balance.setEmpID(empID);
        balance.updateLeaveBalance(leaveDetails);
        employeeService.updateLeaveBalance(balance);
    }
    
    private void updateUserLabels(IT empAccount) {
        if (empAccount == null) {
            System.err.println("Error: empAccount is null");
            return;
        }

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        if (empAccount.getEmpDetails() != null) {
            Person empDetails = empAccount.getEmpDetails();

            String tin = empDetails.getEmpTIN() != null ? empDetails.getEmpTIN() : "";
            String phone = empDetails.getEmpPhoneNumber() != null ? empDetails.getEmpPhoneNumber() : "";
            String pagIbig = empDetails.getEmpPagibig() != 0 ? String.valueOf(empDetails.getEmpPagibig()) : "";
            String philhealth = empDetails.getEmpPhilHealth() != 0 ? String.valueOf(empDetails.getEmpPhilHealth()) : "";
            String sss = empDetails.getEmpSSS() != null ? empDetails.getEmpSSS() : "";

            usernameLabel.setText("@" + empAccount.getEmpUserName());
            fullNameValue.setText(empDetails.getFormattedName());
            fullNameValue2.setText(empDetails.getFormattedName());
            empNumValue.setText(String.valueOf(empDetails.getEmpID()));
            addressLabelValue.setText(empDetails.getEmpAddress() != null ? empDetails.getEmpAddress() : "");
            phoneLabelValue.setText(phone);
            tinLabelValue.setText(tin);
            pagibigLabelValue.setText(pagIbig);
            sssLabelValue.setText(sss);
            philhealthLabelValue.setText(philhealth);

            basicSalaryLabelValue.setText("PHP " + String.format("%.2f", empDetails.getEmpBasicSalary()));
            riceLabelValue.setText("PHP " + String.format("%.2f", empDetails.getEmpRice()));
            phoneAllowanceValue.setText("PHP " + String.format("%.2f", empDetails.getEmpPhone()));
            clothingLabelValue.setText("PHP " + String.format("%.2f", empDetails.getEmpClothing()));
            hourlyrateLabelValue.setText("PHP " + String.format("%.2f", empDetails.getEmpHourlyRate()));

            empIDPayLabelValue.setText(String.valueOf(empDetails.getEmpID()));
            namePayLabelValue.setText(empDetails.getFormattedName());


            if (empDetails.getEmpImmediateSupervisor() != null) {
                supervisorLabelValue.setText(empDetails.getEmpImmediateSupervisor().getFormattedName());
            } else {
                supervisorLabelValue.setText("N/A");
            }

            if (empDetails.getEmpPosition() != null) {
                positionLabelValue.setText(empDetails.getEmpPosition().getPosition());
            } else {
                positionLabelValue.setText("N/A");
            }
            if (empDetails.getEmpStatus() != null) {
                statusLabelValue.setText(empDetails.getEmpStatus().getStatus());
            } else {
                statusLabelValue.setText("N/A");
            }
            if (empDetails.getEmpBirthday() != null) {
                String formattedBirthday = formatter.format(empDetails.getEmpBirthday());
                bdayLabelValue.setText(formattedBirthday);
            } else {
                bdayLabelValue.setText("N/A");
            }
            
        }
   
    }
    private void updatePayrollEmpLabels(IT empAccount){
        empIDPayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpID()));
        namePayLabelValue.setText(empAccount.getEmpDetails().getFormattedName());
    }
    


    
    private void loadAllPositions(){
        List<EmployeePosition> positions = hrService.getAllPosition();
        positionDropdown.addItem(new ComboItem(null,"Select Position"));
        for(EmployeePosition empPosition : positions){
            positionDropdown.addItem(new ComboItem(empPosition.getId(),empPosition.getPosition()));
        }
    }  
    
    private void loadAllStatus(){
        List<EmployeeStatus> statuses = hrService.getAllStatuses();
        statusDropdown.addItem(new ComboItem(null,"Select Status"));
        for(EmployeeStatus empStatus : statuses){
            statusDropdown.addItem(new ComboItem(empStatus.getId(),empStatus.getStatus()));
        }
    }
    
    private void loadAllEmployee(){
        List<Person> allEmployee = hrService.getAllEmployee();
        supervisorDropdown.addItem(new ComboItem(null,"Select Supervisor"));
        for(Person employeeDetails: allEmployee){
            supervisorDropdown.addItem(new ComboItem(employeeDetails.getEmpID(),employeeDetails.getFormattedName()));
        }
    }   
 
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        motorphdash = new javax.swing.JPanel();
        header = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logoutButton = new javax.swing.JButton();
        navigatorSplitPane = new javax.swing.JSplitPane();
        navigation = new javax.swing.JPanel();
        profilePictureLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        empManagementButton = new javax.swing.JButton();
        attendancelButton = new javax.swing.JButton();
        leaveManagementButton = new javax.swing.JButton();
        profileButton = new javax.swing.JButton();
        usernameLabel = new javax.swing.JLabel();
        fullNameValue2 = new javax.swing.JLabel();
        empNumValue = new javax.swing.JLabel();
        mphCards = new javax.swing.JPanel();
        profile = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        birthdayLabel = new javax.swing.JLabel();
        addressLabelValue = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        sssLabel = new javax.swing.JLabel();
        phoneNumberLabel = new javax.swing.JLabel();
        phealthLabel = new javax.swing.JLabel();
        tinLabel = new javax.swing.JLabel();
        pagibigLabel = new javax.swing.JLabel();
        empIDLabelValue = new javax.swing.JLabel();
        positionLabelValue = new javax.swing.JLabel();
        supervisorLabel = new javax.swing.JLabel();
        fullNameValue = new javax.swing.JLabel();
        riceSubsidyLabel = new javax.swing.JLabel();
        phoneAllowanceLabel = new javax.swing.JLabel();
        basicSalaryLabel = new javax.swing.JLabel();
        clothingLabel = new javax.swing.JLabel();
        phoneLabelValue = new javax.swing.JLabel();
        riceLabelValue = new javax.swing.JLabel();
        supervisorLabelValue = new javax.swing.JLabel();
        basicSalaryLabelValue = new javax.swing.JLabel();
        bdayLabelValue = new javax.swing.JLabel();
        philhealthLabelValue = new javax.swing.JLabel();
        sssLabelValue = new javax.swing.JLabel();
        tinLabelValue = new javax.swing.JLabel();
        pagibigLabelValue = new javax.swing.JLabel();
        clothingLabelValue = new javax.swing.JLabel();
        phoneAllowanceValue = new javax.swing.JLabel();
        hourlyrateLabel = new javax.swing.JLabel();
        hourlyrateLabelValue = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        statusLabelValue = new javax.swing.JLabel();
        profileLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        changePasswordButton = new javax.swing.JButton();
        confirmPasswordTField = new javax.swing.JPasswordField();
        newPasswordTField = new javax.swing.JPasswordField();
        existingPasswordTField = new javax.swing.JPasswordField();
        attendance = new javax.swing.JPanel();
        salarySlips = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        attendanceTable = new javax.swing.JTable();
        monthDropdown = new javax.swing.JComboBox<>();
        yearDropdown = new javax.swing.JComboBox<>();
        empIDPayLabel = new javax.swing.JLabel();
        namePayLabel = new javax.swing.JLabel();
        empIDPayLabelValue = new javax.swing.JLabel();
        namePayLabelValue = new javax.swing.JLabel();
        exportAttendance = new javax.swing.JButton();
        searchTextField1 = new javax.swing.JTextField();
        searchButton1 = new javax.swing.JButton();
        empManagement = new javax.swing.JPanel();
        empSectionLabel = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        empFirstNameLabel = new javax.swing.JLabel();
        empLastNameLabel = new javax.swing.JLabel();
        empPositionLabel = new javax.swing.JLabel();
        empAddressLabel = new javax.swing.JLabel();
        lastNameTField = new javax.swing.JTextField();
        firstNameTField = new javax.swing.JTextField();
        addressTField = new javax.swing.JTextField();
        empDetailsLabel = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        empBirthdayLabel = new javax.swing.JLabel();
        empPhoneLabel = new javax.swing.JLabel();
        phoneTField = new javax.swing.JTextField();
        empSalaryLabel = new javax.swing.JLabel();
        salaryTField = new javax.swing.JTextField();
        empHourlyLabel = new javax.swing.JLabel();
        riceTField = new javax.swing.JTextField();
        empSssLabel = new javax.swing.JLabel();
        empTinLabel = new javax.swing.JLabel();
        empPagibigLabel = new javax.swing.JLabel();
        empPhilhealthLabel = new javax.swing.JLabel();
        sssTField = new javax.swing.JTextField();
        pagibigTField = new javax.swing.JTextField();
        tinTField = new javax.swing.JTextField();
        philhealthTField = new javax.swing.JTextField();
        salaryDetailsLabel = new javax.swing.JLabel();
        empBiMonthLabel = new javax.swing.JLabel();
        empRiceLabel = new javax.swing.JLabel();
        empPhoneAllowLabel = new javax.swing.JLabel();
        empClothAllowLabel = new javax.swing.JLabel();
        empStatusLabel = new javax.swing.JLabel();
        empSupervisorLabel = new javax.swing.JLabel();
        phoneAllowTField = new javax.swing.JTextField();
        clothingTField = new javax.swing.JTextField();
        biMonthlyTField = new javax.swing.JTextField();
        supervisorDropdown = new javax.swing.JComboBox<>();
        positionDropdown = new javax.swing.JComboBox<>();
        statusDropdown = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        employeeIDTField = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        usernameTField = new javax.swing.JTextField();
        passwordTField = new javax.swing.JPasswordField();
        jScrollPane2 = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();
        viewAllButton = new javax.swing.JButton();
        printAllEmployees = new javax.swing.JButton();
        dateChooser = new com.toedter.calendar.JDateChooser();
        hourlyTField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        searchTextField = new javax.swing.JTextField();
        leaveManagement = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        leaveRequest = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        leaveManagementTable = new javax.swing.JTable();
        jLabel29 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        header.setBackground(new java.awt.Color(255, 51, 51));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("  MotorPH   ");

        logoutButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        logoutButton.setForeground(new java.awt.Color(255, 255, 255));
        logoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logout-final.png"))); // NOI18N
        logoutButton.setText("Logout");
        logoutButton.setContentAreaFilled(false);
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutButtonMouseClicked(evt);
            }
        });
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutButton)
                .addGap(35, 35, 35))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(headerLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(logoutButton)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        navigation.setBackground(new java.awt.Color(102, 102, 102));

        profilePictureLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        profilePictureLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/user (3).png"))); // NOI18N

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("NAVIGATION");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(127, 127, 127))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        empManagementButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empManagementButton.setForeground(new java.awt.Color(255, 255, 255));
        empManagementButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/employees.png"))); // NOI18N
        empManagementButton.setText(" Employees Section");
        empManagementButton.setContentAreaFilled(false);
        empManagementButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        empManagementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                empManagementButtonActionPerformed(evt);
            }
        });

        attendancelButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        attendancelButton.setForeground(new java.awt.Color(255, 255, 255));
        attendancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/clock-8-16.png"))); // NOI18N
        attendancelButton.setText("Attendance Tracker");
        attendancelButton.setContentAreaFilled(false);
        attendancelButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        attendancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendancelButtonActionPerformed(evt);
            }
        });

        leaveManagementButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        leaveManagementButton.setForeground(new java.awt.Color(255, 255, 255));
        leaveManagementButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/text-file-4-16.png"))); // NOI18N
        leaveManagementButton.setText(" Leave Management");
        leaveManagementButton.setContentAreaFilled(false);
        leaveManagementButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        leaveManagementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leaveManagementButtonActionPerformed(evt);
            }
        });

        profileButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        profileButton.setForeground(new java.awt.Color(255, 255, 255));
        profileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/user-16.png"))); // NOI18N
        profileButton.setText(" Profile");
        profileButton.setContentAreaFilled(false);
        profileButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        profileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileButtonActionPerformed(evt);
            }
        });

        usernameLabel.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        usernameLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        usernameLabel.setText("sample");

        fullNameValue2.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        fullNameValue2.setForeground(new java.awt.Color(255, 255, 255));
        fullNameValue2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        fullNameValue2.setText("jLabel3");

        empNumValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        empNumValue.setForeground(new java.awt.Color(255, 255, 255));
        empNumValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        empNumValue.setText("empNum");

        javax.swing.GroupLayout navigationLayout = new javax.swing.GroupLayout(navigation);
        navigation.setLayout(navigationLayout);
        navigationLayout.setHorizontalGroup(
            navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(profileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(attendancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(empManagementButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(leaveManagementButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(navigationLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(profilePictureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(empNumValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fullNameValue2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        navigationLayout.setVerticalGroup(
            navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, navigationLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(navigationLayout.createSequentialGroup()
                        .addComponent(fullNameValue2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(usernameLabel)
                        .addGap(5, 5, 5)
                        .addComponent(empNumValue))
                    .addComponent(profilePictureLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(profileButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(empManagementButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leaveManagementButton)
                .addGap(538, 538, 538))
        );

        navigatorSplitPane.setLeftComponent(navigation);

        mphCards.setLayout(new java.awt.CardLayout());

        profile.setBackground(new java.awt.Color(229, 229, 229));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        birthdayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        birthdayLabel.setText("Birthday");

        addressLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        addressLabelValue.setText("Address");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/128px user.png"))); // NOI18N

        sssLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        sssLabel.setText("SSS #");

        phoneNumberLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        phoneNumberLabel.setText("Phone #");

        phealthLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        phealthLabel.setText("Philhealth #");

        tinLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        tinLabel.setText("TIN #");

        pagibigLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        pagibigLabel.setText("Pag-ibig #");

        empIDLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        empIDLabelValue.setText("EMP ID");

        positionLabelValue.setBackground(new java.awt.Color(255, 255, 255));
        positionLabelValue.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        positionLabelValue.setText("Position");

        supervisorLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        supervisorLabel.setText("Supervisor");

        fullNameValue.setFont(new java.awt.Font("Century Gothic", 1, 30)); // NOI18N
        fullNameValue.setText("Name");

        riceSubsidyLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        riceSubsidyLabel.setText("Rice Subsidy");

        phoneAllowanceLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        phoneAllowanceLabel.setText("Phone Allow.");

        basicSalaryLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        basicSalaryLabel.setText("Basic Salary");

        clothingLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        clothingLabel.setText("Clothing Allow.");

        phoneLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        phoneLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        phoneLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        riceLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        riceLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        riceLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        supervisorLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        supervisorLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        supervisorLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        basicSalaryLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        basicSalaryLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        basicSalaryLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        bdayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        bdayLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bdayLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        philhealthLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        philhealthLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        philhealthLabelValue.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        philhealthLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        sssLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        sssLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sssLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        tinLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        tinLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tinLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        pagibigLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        pagibigLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pagibigLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        clothingLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        clothingLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        clothingLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        phoneAllowanceValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        phoneAllowanceValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        phoneAllowanceValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        hourlyrateLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        hourlyrateLabel.setText("Hourly Rate");

        hourlyrateLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        hourlyrateLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hourlyrateLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/image-removebg-preview (3) (1)_1.png"))); // NOI18N

        statusLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        statusLabel.setText("Status");

        statusLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(statusLabel))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(supervisorLabel)
                            .addComponent(phoneNumberLabel)
                            .addComponent(birthdayLabel)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tinLabel)
                            .addComponent(sssLabel)
                            .addComponent(phealthLabel)
                            .addComponent(basicSalaryLabel)
                            .addComponent(hourlyrateLabel)
                            .addComponent(phoneAllowanceLabel)
                            .addComponent(clothingLabel)
                            .addComponent(riceSubsidyLabel)
                            .addComponent(pagibigLabel))))
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tinLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(clothingLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(phoneAllowanceValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(hourlyrateLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(riceLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(186, 186, 186))
                            .addComponent(basicSalaryLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addGap(15, 15, 15))
                    .addComponent(addressLabelValue)
                    .addComponent(fullNameValue)
                    .addComponent(positionLabelValue)
                    .addComponent(empIDLabelValue)
                    .addComponent(statusLabelValue)
                    .addComponent(phoneLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bdayLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(philhealthLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(supervisorLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sssLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagibigLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel6))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(fullNameValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addressLabelValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(positionLabelValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(empIDLabelValue)))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneNumberLabel)
                    .addComponent(phoneLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supervisorLabel)
                    .addComponent(supervisorLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(birthdayLabel)
                    .addComponent(bdayLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tinLabel)
                    .addComponent(tinLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sssLabel)
                            .addComponent(sssLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pagibigLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pagibigLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(phealthLabel)
                            .addComponent(philhealthLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(basicSalaryLabel)
                            .addComponent(basicSalaryLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hourlyrateLabel)
                            .addComponent(hourlyrateLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(phoneAllowanceValue)
                            .addComponent(phoneAllowanceLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clothingLabel)
                            .addComponent(clothingLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(riceSubsidyLabel)
                            .addComponent(riceLabelValue)))
                    .addComponent(jLabel31))
                .addGap(21, 21, 21))
        );

        profileLabel.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        profileLabel.setText("My Profile");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        jLabel2.setText("Change Password");

        jLabel17.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel17.setText("Existing Password:");

        jLabel20.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel20.setText("New Password:");

        jLabel21.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        jLabel21.setText("Confirm Password:");

        changePasswordButton.setBackground(new java.awt.Color(0, 0, 0));
        changePasswordButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        changePasswordButton.setForeground(new java.awt.Color(255, 255, 255));
        changePasswordButton.setText("Submit");
        changePasswordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePasswordButtonActionPerformed(evt);
            }
        });

        confirmPasswordTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmPasswordTFieldActionPerformed(evt);
            }
        });

        newPasswordTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPasswordTFieldActionPerformed(evt);
            }
        });

        existingPasswordTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                existingPasswordTFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(changePasswordButton)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel17)
                    .addComponent(jLabel2)
                    .addComponent(newPasswordTField, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(existingPasswordTField)
                    .addComponent(confirmPasswordTField))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(existingPasswordTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addComponent(newPasswordTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(confirmPasswordTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(changePasswordButton)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout profileLayout = new javax.swing.GroupLayout(profile);
        profile.setLayout(profileLayout);
        profileLayout.setHorizontalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(profileLabel)
                    .addGroup(profileLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        profileLayout.setVerticalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(profileLabel)
                .addGap(18, 18, 18)
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );

        mphCards.add(profile, "card3");

        attendance.setBackground(new java.awt.Color(229, 229, 229));

        salarySlips.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        salarySlips.setText("Attendance Tracker");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        attendanceTable.setBackground(new java.awt.Color(211, 211, 211));
        attendanceTable.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        attendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Day", "Time-in", "Time-out", "Total Hours", "Remarks"
            }
        ));
        attendanceTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        attendanceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                attendanceTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(attendanceTable);

        monthDropdown.setFont(new java.awt.Font("Century Gothic", 1, 15)); // NOI18N
        monthDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthDropdownActionPerformed(evt);
            }
        });

        yearDropdown.setFont(new java.awt.Font("Century Gothic", 1, 15)); // NOI18N
        yearDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearDropdownActionPerformed(evt);
            }
        });

        empIDPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empIDPayLabel.setText("EMP. ID");

        namePayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        namePayLabel.setText("Name");

        empIDPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        namePayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        exportAttendance.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        exportAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export.png"))); // NOI18N
        exportAttendance.setText("Export");
        exportAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAttendanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1057, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(namePayLabel)
                                    .addComponent(empIDPayLabel))
                                .addGap(31, 31, 31)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(empIDPayLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(namePayLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(monthDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(yearDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(501, 501, 501)
                        .addComponent(exportAttendance)
                        .addGap(51, 51, 51))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namePayLabel)
                    .addComponent(namePayLabelValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(empIDPayLabel)
                    .addComponent(empIDPayLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(monthDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportAttendance))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        searchTextField1.setForeground(new java.awt.Color(153, 153, 153));
        searchTextField1.setText("Enter the Employee ID here...");
        searchTextField1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        searchTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchTextField1FocusLost(evt);
            }
        });
        searchTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextField1ActionPerformed(evt);
            }
        });

        searchButton1.setBackground(new java.awt.Color(0, 0, 153));
        searchButton1.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        searchButton1.setForeground(new java.awt.Color(255, 255, 255));
        searchButton1.setText("Search");
        searchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout attendanceLayout = new javax.swing.GroupLayout(attendance);
        attendance.setLayout(attendanceLayout);
        attendanceLayout.setHorizontalGroup(
            attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendanceLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(attendanceLayout.createSequentialGroup()
                        .addComponent(salarySlips)
                        .addGap(477, 477, 477)
                        .addComponent(searchTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(searchButton1)))
                .addContainerGap())
        );
        attendanceLayout.setVerticalGroup(
            attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendanceLayout.createSequentialGroup()
                .addGroup(attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(attendanceLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchButton1)
                            .addComponent(searchTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, attendanceLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(salarySlips)
                        .addGap(30, 30, 30)))
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mphCards.add(attendance, "card4");

        empManagement.setBackground(new java.awt.Color(229, 229, 229));

        empSectionLabel.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        empSectionLabel.setText("Employees Section");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        addButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setBackground(new java.awt.Color(255, 51, 51));
        deleteButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        deleteButton.setForeground(new java.awt.Color(255, 255, 255));
        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        empFirstNameLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empFirstNameLabel.setText("First Name");

        empLastNameLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empLastNameLabel.setText("Last Name");

        empPositionLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPositionLabel.setText("Position");

        empAddressLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empAddressLabel.setText("Address");

        addressTField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        addressTField.setToolTipText("");
        addressTField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        addressTField.setMaximumSize(new java.awt.Dimension(64, 22));
        addressTField.setName(""); // NOI18N

        empDetailsLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        empDetailsLabel.setText("Employee Details");

        clearButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        empBirthdayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empBirthdayLabel.setText("Birthday");

        empPhoneLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPhoneLabel.setText("Phone #");

        empSalaryLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empSalaryLabel.setText("Basic Salary");

        empHourlyLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empHourlyLabel.setText("Hourly Rate");

        riceTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                riceTFieldActionPerformed(evt);
            }
        });

        empSssLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empSssLabel.setText("SSS #");

        empTinLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empTinLabel.setText("TIN #");

        empPagibigLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPagibigLabel.setText("PAG-IBIG #");

        empPhilhealthLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPhilhealthLabel.setText("PhilHealth #");

        sssTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sssTFieldActionPerformed(evt);
            }
        });

        salaryDetailsLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        salaryDetailsLabel.setText("Salary Details");

        empBiMonthLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empBiMonthLabel.setText("Bi-Monthly");

        empRiceLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empRiceLabel.setText("Rice Allowance");

        empPhoneAllowLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPhoneAllowLabel.setText("Phone Allowance");

        empClothAllowLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empClothAllowLabel.setText("Clothing Allowance");

        empStatusLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empStatusLabel.setText("Status");

        empSupervisorLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empSupervisorLabel.setText("Supervisor");

        clothingTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clothingTFieldActionPerformed(evt);
            }
        });

        biMonthlyTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                biMonthlyTFieldActionPerformed(evt);
            }
        });

        positionDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                positionDropdownActionPerformed(evt);
            }
        });

        statusDropdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statusDropdownActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel3.setText("Employee ID");

        employeeIDTField.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel5.setText("Username");

        jLabel7.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel7.setText("Password");

        passwordTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordTFieldActionPerformed(evt);
            }
        });

        employeeTable.setBackground(new java.awt.Color(222, 222, 222));
        employeeTable.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        employeeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Emp. ID", "Last Name", "First Name", "Position", "Status"
            }
        ));
        employeeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        employeeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                employeeTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(employeeTable);

        viewAllButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        viewAllButton.setText("View All Employees");
        viewAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewAllButtonActionPerformed(evt);
            }
        });

        printAllEmployees.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        printAllEmployees.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/printer.png"))); // NOI18N
        printAllEmployees.setText("Print");
        printAllEmployees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printAllEmployeesActionPerformed(evt);
            }
        });

        hourlyTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hourlyTFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empLastNameLabel)
                            .addComponent(empAddressLabel)
                            .addComponent(empBirthdayLabel)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(empPositionLabel)
                                .addComponent(empPhoneLabel))
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(salaryDetailsLabel)
                            .addComponent(empSalaryLabel)
                            .addComponent(empHourlyLabel)
                            .addComponent(empBiMonthLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(biMonthlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(hourlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(salaryTField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(35, 35, 35)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(empPhoneAllowLabel)
                                        .addGap(46, 46, 46))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(empRiceLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(empClothAllowLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel10Layout.createSequentialGroup()
                                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(dateChooser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                                        .addComponent(addressTField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(phoneTField))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(empStatusLabel)
                                                        .addComponent(empPhilhealthLabel)
                                                        .addComponent(empSupervisorLabel)))
                                                .addGroup(jPanel10Layout.createSequentialGroup()
                                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(usernameTField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(lastNameTField, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel7)
                                                                .addComponent(empFirstNameLabel))
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(jPanel10Layout.createSequentialGroup()
                                                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(passwordTField, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                                                        .addComponent(firstNameTField))
                                                                    .addGap(31, 31, 31)
                                                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(empTinLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(empSssLabel, javax.swing.GroupLayout.Alignment.TRAILING)))
                                                                .addGroup(jPanel10Layout.createSequentialGroup()
                                                                    .addGap(112, 112, 112)
                                                                    .addComponent(empPagibigLabel))))
                                                        .addComponent(employeeIDTField))
                                                    .addGap(0, 0, Short.MAX_VALUE)))
                                            .addGroup(jPanel10Layout.createSequentialGroup()
                                                .addComponent(positionDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(113, 113, 113)))
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(pagibigTField, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                            .addComponent(sssTField)
                                            .addComponent(tinTField)
                                            .addComponent(philhealthTField)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(supervisorDropdown, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(clearButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(statusDropdown, javax.swing.GroupLayout.Alignment.TRAILING, 0, 124, Short.MAX_VALUE)
                                                .addComponent(riceTField, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(clothingTField)
                                                .addComponent(phoneAllowTField))))
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(addButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(updateButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(deleteButton)))
                                .addGap(34, 34, 34)))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(viewAllButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(printAllEmployees))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(empDetailsLabel)
                        .addContainerGap())))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(empDetailsLabel)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(employeeIDTField)
                    .addComponent(clearButton)
                    .addComponent(viewAllButton)
                    .addComponent(printAllEmployees))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(usernameTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empSssLabel)
                            .addComponent(sssTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empLastNameLabel)
                            .addComponent(lastNameTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empFirstNameLabel)
                            .addComponent(firstNameTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empTinLabel)
                            .addComponent(tinTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empAddressLabel)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(addressTField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(empPagibigLabel)
                                .addComponent(pagibigTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empBirthdayLabel)
                            .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(philhealthTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(empPhilhealthLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statusDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empStatusLabel)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(phoneTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(empPhoneLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(positionDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(empPositionLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(empSupervisorLabel)
                                .addComponent(supervisorDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(23, 23, 23)
                        .addComponent(salaryDetailsLabel)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empSalaryLabel)
                            .addComponent(salaryTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empRiceLabel)
                            .addComponent(riceTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empHourlyLabel)
                            .addComponent(hourlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empPhoneAllowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(phoneAllowTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empBiMonthLabel)
                            .addComponent(biMonthlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empClothAllowLabel)
                            .addComponent(clothingTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteButton)
                            .addComponent(updateButton)
                            .addComponent(addButton))
                        .addGap(77, 77, 77))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        searchButton.setBackground(new java.awt.Color(0, 0, 153));
        searchButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        searchTextField.setForeground(new java.awt.Color(153, 153, 153));
        searchTextField.setText("Enter the Employee ID here...");
        searchTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        searchTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchTextFieldFocusLost(evt);
            }
        });
        searchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout empManagementLayout = new javax.swing.GroupLayout(empManagement);
        empManagement.setLayout(empManagementLayout);
        empManagementLayout.setHorizontalGroup(
            empManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, empManagementLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(empManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(empManagementLayout.createSequentialGroup()
                        .addComponent(empSectionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchButton)))
                .addGap(42, 42, 42))
        );
        empManagementLayout.setVerticalGroup(
            empManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(empManagementLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(empManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(empSectionLabel)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addGap(20, 20, 20)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        mphCards.add(empManagement, "card5");

        leaveManagement.setBackground(new java.awt.Color(229, 229, 229));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel28.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        jLabel28.setText("Leave request");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel28)
                .addGap(47, 679, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel28)
                .addContainerGap(529, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout leaveManagementLayout = new javax.swing.GroupLayout(leaveManagement);
        leaveManagement.setLayout(leaveManagementLayout);
        leaveManagementLayout.setHorizontalGroup(
            leaveManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveManagementLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(327, Short.MAX_VALUE))
        );
        leaveManagementLayout.setVerticalGroup(
            leaveManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveManagementLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        mphCards.add(leaveManagement, "card6");

        leaveRequest.setBackground(new java.awt.Color(229, 229, 229));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        leaveManagementTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Code", "Emp ID", "Name", "Subject", "Leave Type", "Date From", "Date To", "Total Days", "Reason", "Status", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        leaveManagementTable.setGridColor(new java.awt.Color(255, 255, 255));
        leaveManagementTable.setRowHeight(30);
        jScrollPane4.setViewportView(leaveManagementTable);
        if (leaveManagementTable.getColumnModel().getColumnCount() > 0) {
            leaveManagementTable.getColumnModel().getColumn(10).setResizable(false);
        }

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1081, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        jLabel29.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        jLabel29.setText("Leave Management");

        javax.swing.GroupLayout leaveRequestLayout = new javax.swing.GroupLayout(leaveRequest);
        leaveRequest.setLayout(leaveRequestLayout);
        leaveRequestLayout.setHorizontalGroup(
            leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveRequestLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58))
        );
        leaveRequestLayout.setVerticalGroup(
            leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveRequestLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        mphCards.add(leaveRequest, "card6");

        navigatorSplitPane.setRightComponent(mphCards);

        javax.swing.GroupLayout motorphdashLayout = new javax.swing.GroupLayout(motorphdash);
        motorphdash.setLayout(motorphdashLayout);
        motorphdashLayout.setHorizontalGroup(
            motorphdashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navigatorSplitPane)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        motorphdashLayout.setVerticalGroup(
            motorphdashLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, motorphdashLayout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(navigatorSplitPane))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(motorphdash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(motorphdash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void profileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileButtonActionPerformed
    cardLayout.show(mphCards, "card3");        // TODO add your handling code here:
    }//GEN-LAST:event_profileButtonActionPerformed

    private void leaveManagementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leaveManagementButtonActionPerformed
    cardLayout.show(mphCards, "card6"); 
    refreshLeaveManagementTable();
    }//GEN-LAST:event_leaveManagementButtonActionPerformed

    private void attendancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendancelButtonActionPerformed
    cardLayout.show(mphCards, "card4"); 
    }//GEN-LAST:event_attendancelButtonActionPerformed

    private void empManagementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empManagementButtonActionPerformed
    cardLayout.show(mphCards, "card5"); 
    }//GEN-LAST:event_empManagementButtonActionPerformed
    private void refreshLeaveManagementTable(){  
        List<HR> leaveDetails =  employeeService.getAllLeaveRequestByStatus(HR.LeaveStatus.PENDING);
            DefaultTableModel model = (DefaultTableModel) leaveManagementTable.getModel();
            model.setRowCount(0);

            for(HR leave : leaveDetails) {
                if (leave.getEmpID() == currentEmployeeId) {
                    continue;
                }
                Vector<Object> rowData = new Vector<>();
               
                rowData.add(leave.getLeaveId());
                rowData.add(leave.getEmpID());
                Person empDetails = hrService.getByEmpID(leave.getEmpID());
                rowData.add(empDetails.getFormattedName());
                rowData.add(leave.getSubject());
                rowData.add(leave.getLeaveType());
                rowData.add(leave.getDateFrom());
                rowData.add(leave.getDateTo());
                rowData.add(leave.getTotalDays());
                rowData.add(leave.getReason());
                rowData.add(leave.getStatus());
                model.addRow(rowData);
            }
    }
    
    private void refreshTable(){
        viewAllButtonActionPerformed(null);
    }
    
    private void loadEmployeeValues(int empID){
        Person empDetails = hrService.getByEmpID(empID);
        
        if (empDetails == null) {
            JOptionPane.showMessageDialog(this, "No employee details found for ID: " + empID);
            return;
        }
        employeeIDTField.setText(String.valueOf(empDetails.getEmpID()));
        lastNameTField.setText(empDetails.getLastName());
        firstNameTField.setText(empDetails.getFirstName());
        sssTField.setText(empDetails.getEmpSSS());
        philhealthTField.setText(String.valueOf(empDetails.getEmpPhilHealth()));
        tinTField.setText(String.valueOf(empDetails.getEmpTIN()));
        pagibigTField.setText(String.valueOf(empDetails.getEmpPagibig()));
        addressTField.setText(empDetails.getEmpAddress());
        dateChooser.setDate(empDetails.getEmpBirthday());
        phoneTField.setText(String.valueOf(empDetails.getEmpPhoneNumber()));
        salaryTField.setText(String.valueOf(empDetails. getEmpBasicSalary()));
        hourlyTField.setText(String.valueOf(empDetails.getEmpHourlyRate()));
        biMonthlyTField.setText(String.valueOf(empDetails.getEmpMonthlyRate()));
        riceTField.setText(String.valueOf(empDetails.getEmpRice()));
        phoneAllowTField.setText(String.valueOf(empDetails.getEmpPhone()));
        clothingTField.setText(String.valueOf(empDetails.getEmpClothing()));

        
        statusDropdown.setSelectedIndex(0);
        positionDropdown.setSelectedIndex(0);
        supervisorDropdown.setSelectedIndex(0);
        if(empDetails.getEmpStatus() != null){
            statusDropdown.setSelectedItem(new ComboItem(empDetails.getEmpStatus().getId(),empDetails.getEmpStatus().getStatus()));
        }
        if(empDetails.getEmpPosition() != null){
            positionDropdown.setSelectedItem(new ComboItem(empDetails.getEmpPosition().getId(),empDetails.getEmpPosition().getPosition()));
        }
        if(empDetails.getEmpImmediateSupervisor() != null){
            supervisorDropdown.setSelectedItem(new ComboItem(empDetails.getEmpImmediateSupervisor().getEmpID(),empDetails.getEmpImmediateSupervisor().getFormattedName()));
        }
        
        IT empAccount = empAccountService.getByEmpID(empID);
        usernameTField.setText(empAccount.getEmpUserName());
        passwordTField.setText(empAccount.getEmpPassword());   

    }
       
    private boolean validateRequiredFields(IT empAccount, Person empDetails){
        List<String> errors = new ArrayList<>();
        if (empAccount == null) {
            errors.add("Employee account cannot be null.");
        } else {
            if (StringUtils.isEmpty(empAccount.getEmpUserName())) {
                errors.add("Username");
            }
            if (StringUtils.isEmpty(empAccount.getEmpPassword())) {
                errors.add("Password");
            }
        }
        
        if (empDetails == null) {
            errors.add("Employee details cannot be null.");
        } else {
            // First Name - required and must contain only letters
            if (StringUtils.isEmpty(empDetails.getFirstName())) {
                errors.add("First Name is required.");
            } else if (!empDetails.getFirstName().matches("^[A-Za-z\\-]+$")) {
                errors.add("First Name must contain letters and dashes only.");
            }

            // Last Name - required and must contain only letters
            if (StringUtils.isEmpty(empDetails.getLastName())) {
                errors.add("Last Name is required.");
            } else if (!empDetails.getLastName().matches("^[A-Za-z\\-]+$")) {
                errors.add("Last Name must contain letters and dashes only.");
            }

            // Address - required and max 250 characters
            if (StringUtils.isEmpty(empDetails.getEmpAddress())) {
                errors.add("Address is required.");
            } else if (empDetails.getEmpAddress().length() > 250) {
                errors.add("Address must not exceed 250 characters.");
            }

            // Birthday - required and must be at least 18 years old
            if (empDetails.getEmpBirthday() == null) {
                errors.add("Birthday is required.");
            } else {
                Calendar dob = Calendar.getInstance();
                dob.setTime(empDetails.getEmpBirthday());
                Calendar today = Calendar.getInstance();
                int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }
                if (age < 18) {
                    errors.add("Employee must be at least 18 years old.");
                }
            }

            // Phone Number - required and digits only
            if (StringUtils.isEmpty(empDetails.getEmpPhoneNumber())) {
                errors.add("Phone Number is required.");
            } else if (!empDetails.getEmpPhoneNumber().matches("^[\\d\\-\\+]+$")) {
                errors.add("Phone Number must contain only digits, dashes (-), or plus sign (+).");
            }

            // Status
            if (empDetails.getEmpStatus() == null) {
                errors.add("Status is required.");
            }

            // Position
            if (empDetails.getEmpPosition() == null) {
                errors.add("Position is required.");
            }

            // Supervisor
            if (empDetails.getEmpImmediateSupervisor() == null) {
                errors.add("Immediate Supervisor is required.");
            }

            // SSS - required and digits only
            if (StringUtils.isEmpty(empDetails.getEmpSSS())) {
                errors.add("SSS# is required.");
            } else if (!empDetails.getEmpSSS().matches("^\\d+$")) {
                errors.add("SSS# must contain digits only.");
            }

            // TIN - required and digits only
            if (StringUtils.isEmpty(empDetails.getEmpTIN())) {
                errors.add("TIN# is required.");
            } else if (!empDetails.getEmpTIN().matches("^\\d+$")) {
                errors.add("TIN# must contain digits only.");
            }

            String pagibigInput = pagibigTField.getText().trim();
            if (StringUtils.isEmpty(pagibigInput)) {
                errors.add("PAG-IBIG# is required.");
            } else if (!pagibigInput.matches("^\\d+$")) {
                errors.add("PAG-IBIG# must contain digits only.");
            } else {
                try {
                    long pagibig = Long.parseLong(pagibigInput);
                    if (pagibig <= 0) {
                        errors.add("PAG-IBIG# must be a positive number.");
                    } else {
                        validatedPagibig = pagibig; // ✔️ store for later use
                    }
                } catch (NumberFormatException e) {
                    errors.add("PAG-IBIG# is not a valid number.");
                }
            }

            String philHealthInput = philhealthTField.getText().trim();
            if (StringUtils.isEmpty(philHealthInput)) {
                errors.add("PhilHealth# is required.");
            } else if (!philHealthInput.matches("^\\d+$")) {
                errors.add("PhilHealth# must contain digits only.");
            } else {
                try {
                    long philhealth = Long.parseLong(philHealthInput);
                    if (philhealth <= 0) {
                        errors.add("PhilHealth# must be a positive number.");
                    } else {
                        validatedPhilhealth = philhealth; // ✔️ store for later use
                    }
                } catch (NumberFormatException e) {
                    errors.add("PhilHealth# is not a valid number.");
                }
            }
        }
        if (!errors.isEmpty()) {
              String errorMessage = "The following required fields are missing or invalid:\n\n" + String.join("\n", errors);
              JOptionPane.showMessageDialog(null, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
              return false; // Prevents execution instead of throwing an exception
        }

         return true; // All fields are valid
    }

    
    private Person updateEmpDetailValues(){
        String lastname = lastNameTField.getText().trim() !=null ? lastNameTField.getText() : "";
        String firstname= firstNameTField.getText().trim() !=null ?  firstNameTField.getText().trim() : "";
        //String birthday = birthdayTField.getText().trim()) !=null ? Date(birthdayTField.getText().trim()) : "";
        String address = addressTField.getText().trim() !=null ? addressTField.getText().trim():"";
        double salary = !salaryTField.getText().trim().equals("") ? Double.parseDouble(salaryTField.getText().trim()) : 0;
        String phoneNumber = phoneTField.getText().trim() !=null ? phoneTField.getText().trim() :"";
        double hourlyRate = !hourlyTField.getText().trim().equals("") ? Double.parseDouble(hourlyTField.getText().trim()) : 0;
        double biMonthly = !biMonthlyTField.getText().trim().equals("") ?  Double.parseDouble(biMonthlyTField.getText().trim()): 0;
        double rice = !riceTField.getText().trim().equals("") ? Double.parseDouble(riceTField.getText().trim()): 0;
        double phoneAllow= !phoneAllowTField.getText().trim().equals("") ? Double.parseDouble(phoneAllowTField.getText().trim()): 0;
        double clothing = !clothingTField.getText().trim().equals("")? Double.parseDouble(clothingTField.getText().trim()): 0;
        //long pagibig = !pagibigTField.getText().trim().equals("")? Long.valueOf(pagibigTField.getText().trim()): 0;
        String sss = sssTField.getText().trim() !=null ? sssTField.getText().trim() :"";
        String tin = tinTField.getText().trim() !=null ? tinTField.getText().trim():"";
        //long philhealth = !philhealthTField.getText().equals("") ? Long.valueOf(philhealthTField.getText().trim()): 0;
        long pagibig = 0;
        String pagibigInput = pagibigTField.getText().trim();
        if (!pagibigInput.isEmpty() && pagibigInput.matches("^\\d+$")) {
            pagibig = Long.parseLong(pagibigInput);
        }

        long philhealth = 0;
        String philhealthInput = philhealthTField.getText().trim();
        if (!philhealthInput.isEmpty() && philhealthInput.matches("^\\d+$")) {
            philhealth = Long.parseLong(philhealthInput);
        }
    
        
        Person empDetails = new Employee();
        
        empDetails.setLastName(lastname);
        empDetails.setFirstName(firstname);
        if (dateChooser != null && dateChooser.getDate() != null) {
            Date selectedDate = dateChooser.getDate();
            empDetails.setEmpBirthday(new java.sql.Date(selectedDate.getTime())); // ← direct assignment
        }
        
        if(!employeeIDTField.getText().trim().equals("")){
            empDetails.setEmpID(Integer.parseInt(employeeIDTField.getText().trim()));
        }
        
        empDetails.setEmpAddress(address);
        empDetails.setEmpBasicSalary(salary);
        empDetails.setEmpPhoneNumber(phoneNumber);
        empDetails.setEmpHourlyRate(hourlyRate);
        empDetails.setEmpMonthlyRate(biMonthly); 
        empDetails.setEmpRice(rice);
        empDetails.setEmpPhone(phoneAllow);
        empDetails.setEmpClothing(clothing);
        empDetails.setEmpPagibig(pagibig);
        empDetails.setEmpSSS(sss);
        empDetails.setEmpTIN(tin);
        empDetails.setEmpPhilHealth(philhealth);
        
        ComboItem positionValue = (ComboItem) positionDropdown.getSelectedItem();
        ComboItem statusValue = (ComboItem) statusDropdown.getSelectedItem();
        ComboItem supervisorValue = (ComboItem) supervisorDropdown.getSelectedItem();
        
        if(positionValue.getKey() != null){
            empDetails.setEmpPosition(hrService.getPositionById(positionValue.getKey()));
        }
        if(statusValue.getKey() != null){
            empDetails.setEmpStatus(hrService.getStatusById(statusValue.getKey()));
        }
        if(supervisorValue.getKey() !=null){
            empDetails.setEmpImmediateSupervisor(hrService.getByEmpID(supervisorValue.getKey()));  
        }

        
        return empDetails;
    }
    private IT updateEmpAccountValues(){
        IT empAccount = new IT();
        empAccount.setEmpUserName(usernameTField.getText());
        empAccount.setEmpPassword(passwordTField.getText());
        return empAccount;
    }
    
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String input = searchTextField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input Employee ID!");
            return;
        } else if (!input.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Employee ID must contain numbers only.");
            return;
        }

        int empID = Integer.parseInt(input);

        // Check if employee exists BEFORE calling loadEmployeeValues
        if (hrService.getByEmpID(empID) == null) {
            JOptionPane.showMessageDialog(this, "Employee Not Found!");
            clearButtonActionPerformed(evt);
            return;
        }

        // Safe to load values since employee exists
        loadEmployeeValues(empID);
        refreshTable(); 

        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (empID == Integer.parseInt(model.getValueAt(i, 0).toString())) {
                employeeTable.setRowSelectionInterval(i, i);
                employeeTable.scrollRectToVisible(employeeTable.getCellRect(i, 0, true));
                break;
            }
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextFieldActionPerformed
        
    }//GEN-LAST:event_searchTextFieldActionPerformed

    private void confirmPasswordTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmPasswordTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_confirmPasswordTFieldActionPerformed

    private void newPasswordTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPasswordTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newPasswordTFieldActionPerformed

    private void existingPasswordTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_existingPasswordTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_existingPasswordTFieldActionPerformed

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        JOptionPane.showMessageDialog(null, "You will now be redirected to the login page.");
            LogIn info = new LogIn();
                    info.setVisible(true);
                    this.dispose(); 
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void logoutButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutButtonMouseClicked
             // TODO add your handling code here:
    }//GEN-LAST:event_logoutButtonMouseClicked

    private void searchTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextFieldFocusGained
        if (searchTextField.getText().equals("Enter the Employee ID here...")) {
        searchTextField.setText("");
        searchTextField.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_searchTextFieldFocusGained

    private void searchTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextFieldFocusLost
        if (searchTextField.getText().isEmpty()) {
        searchTextField.setForeground(Color.GRAY);
        searchTextField.setText("Enter the Employee ID here...");
        }
    }//GEN-LAST:event_searchTextFieldFocusLost

    private void passwordTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordTFieldActionPerformed

    private void statusDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statusDropdownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_statusDropdownActionPerformed

    private void positionDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_positionDropdownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_positionDropdownActionPerformed

    private void populateAttendanceTable(List<Employee> empHours){
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);

        for (Employee employeeHours : empHours) {
            Vector<Object> rowData = new Vector<>();

            try {
                // Safe way to convert java.util.Date or java.sql.Date to LocalDate
                Date rawDate = employeeHours.getDate(); // java.util.Date or java.sql.Date
                LocalDate localDate;

                if (rawDate instanceof java.sql.Date) {
                    localDate = ((java.sql.Date) rawDate).toLocalDate();
                } else {
                    localDate = rawDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }

                String day = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

                LocalTime timeIn = employeeHours.getTimeIn();
                LocalTime timeOut = employeeHours.getTimeOut();
                String formattedHours = employeeHours.getFormattedHoursWorked();

                String remarks;
                if (timeIn == null || timeOut == null) {
                    remarks = (day.equals("Sat") || day.equals("Sun")) ? "Weekend" : "Absent";
                } else {
                    remarks = "Present";
                }   
                rowData.add(localDate.toString());
                rowData.add(day);
                rowData.add(timeIn != null ? timeIn.toString() : "-");
                rowData.add(timeOut != null ? timeOut.toString() : "-");
                rowData.add(formattedHours);
                rowData.add(remarks);

                model.addRow(rowData);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error processing attendance row: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void viewAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAllButtonActionPerformed
        List<Person> allEmployee =  hrService.getAllEmployee();
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);

        for(Person empDetails : allEmployee) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(empDetails.getEmpID());
            rowData.add(empDetails.getLastName());
            rowData.add(empDetails.getFirstName());
            rowData.add(empDetails.getEmpPosition().getPosition());
            rowData.add(empDetails.getEmpStatus().getStatus());
            model.addRow(rowData);
        }
        
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Emp. ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Last Name
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100); // First Name
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Position
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status

    }//GEN-LAST:event_viewAllButtonActionPerformed

    private void employeeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeeTableMouseClicked

        DefaultTableModel model  = (DefaultTableModel) employeeTable.getModel();
        int selectedIndex = employeeTable.getSelectedRow();
        int empID = Integer.parseInt(model.getValueAt(selectedIndex,0).toString());
        loadEmployeeValues(empID);

    }//GEN-LAST:event_employeeTableMouseClicked

    private void sssTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sssTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sssTFieldActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

        lastNameTField.setText("");
        firstNameTField.setText("");
        addressTField.setText("");
        dateChooser.setDate(null);
        phoneTField.setText("");
        sssTField.setText("");
        tinTField.setText("");
        pagibigTField.setText("");
        philhealthTField.setText("");
        salaryTField.setText("");
        hourlyTField.setText("");
        riceTField.setText("");
        phoneAllowTField.setText("");
        clothingTField.setText("");
        usernameTField.setText("");
        passwordTField.setText("");
        biMonthlyTField.setText("");
        employeeIDTField.setText("");
        statusDropdown.setSelectedIndex(0);
        positionDropdown.setSelectedIndex(0);
        supervisorDropdown.setSelectedIndex(0);
        employeeTable.clearSelection();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        int selectedIndex = employeeTable.getSelectedRow();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        int empID = Integer.parseInt(model.getValueAt(selectedIndex, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try{
                employeeService.deleteLeaveBalance(empID);
                employeeService.deleteLeaveRequestbyEmpID(empID);
                employeeService.deleteAttendanceRecords(empID);
                empAccountService.deleteEmpAccount(empID);
                hrService.deleteEmployeeDetails(empID);
                
            }catch(RuntimeException e ){
                JOptionPane.showMessageDialog(this, "Error: Employee not deleted");
                throw e;
            }
            JOptionPane.showMessageDialog(this, "Employee successfully deleted");

            clearButtonActionPerformed(null);
            refreshTable();
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try {
            Person empDetails = updateEmpDetailValues();
            IT empAccount = updateEmpAccountValues();
            if (!validateRequiredFields(empAccount,empDetails)) {
                clearButtonActionPerformed(null);
                return; // Stop execution if validation fails
            }
            hrService.updateEmployeeDetails(empDetails);

            empAccount.setEmpID(empDetails.getEmpID()); // Linking account with details
            empAccount.setEmpDetails(empDetails);
            empAccountService.updateEmployeeCredentials(empAccount);


            refreshTable();
            clearButtonActionPerformed(null);

            if (getEmpAccount().getEmpDetails().getEmpID() == empAccount.getEmpID()) {
                updateUserLabels(empAccount);
            }

            JOptionPane.showMessageDialog(null, "Employee has been successfully updated", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating employee: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        Person empDetails = updateEmpDetailValues();
        IT empAccount = updateEmpAccountValues();
        if (!validateRequiredFields(empAccount, empDetails)) {
            return; // Stop if validation fails
        }

        if (hrService.isDuplicateEmployee(empDetails)) {
            JOptionPane.showMessageDialog(this, "An employee with these details already exists.", "Duplicate Employee", JOptionPane.WARNING_MESSAGE);
        } else {
            // --- Wrap the database operations in a try-catch block ---
            try {
                // Only save if it's a new employee
                hrService.addEmployeeDetails(empDetails);
                empAccountService.saveUserAccount(empAccount, empDetails);

                LeaveBalance leaveBalance = new LeaveBalance();
                leaveBalance.setEmpID(empDetails.getEmpID());
                employeeService.saveLeaveBalance(leaveBalance);

                JOptionPane.showMessageDialog(this, "Account added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                // This block will run if any of the service calls fail
                e.printStackTrace(); // For debugging in the console
                JOptionPane.showMessageDialog(this, "A database error occurred while adding the employee.\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        clearButtonActionPerformed(null);
        refreshTable();
    }//GEN-LAST:event_addButtonActionPerformed

    private void changePasswordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePasswordButtonActionPerformed
        String existing = existingPasswordTField.getText().trim();
        String newPassword = newPasswordTField.getText().trim();
        String confirm = confirmPasswordTField.getText().trim();
        
        if(!existing.equals(empAccount.getEmpPassword())){
            JOptionPane.showMessageDialog( null,"Password doesn't exist!");
        } else {
            // Check if the new password and confirmation match
            if (newPassword.equals(confirm)) {
                empAccount.setEmpPassword(newPassword); // Update the account object with the new password
                empAccountService.changePassword(empAccount); // Update the password in the database
                JOptionPane.showMessageDialog(null, "Password changed successfully!");
                existingPasswordTField.setText("");
                newPasswordTField.setText("");
                confirmPasswordTField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Password does not match!"); 
            }
        }
    }//GEN-LAST:event_changePasswordButtonActionPerformed

    private void searchTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextField1FocusGained
        if(searchTextField1.getText().equals("Enter the Employee ID here...")){
           searchTextField1.setText("");
           searchTextField1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_searchTextField1FocusGained

    private void searchTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchTextField1FocusLost
         if(searchTextField1.getText().isEmpty()){
           searchTextField1.setForeground(Color.GRAY);
           searchTextField1.setText("Enter the Employee ID here...");
        }
    }//GEN-LAST:event_searchTextField1FocusLost

    private void searchTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchTextField1ActionPerformed

    private void searchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButton1ActionPerformed
        String empId = searchTextField1.getText().equals("Enter the Employee ID here...") ? "" : searchTextField1.getText().trim();

        // Check if empId is not empty and contains only digits
        if(empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input Employee ID!");
            return;
        } else if (!empId.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Employee ID must contain numbers only.");
            return;
        }

    // Parse Employee ID
        employeeSearchID = Integer.parseInt(empId);

        IT empAccount = empAccountService.getByEmpID(employeeSearchID);
        if (empAccount != null) {
            updatePayrollEmpLabels(empAccount);
            if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
                Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
                Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();

                if (monthValue != null && year != null) {
                    List<Employee> empHours = getEmployeeHours(monthValue, year, employeeSearchID);

                    if (empHours == null || empHours.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No attendance records found for the selected month and year.");
                    } else {
                        populateAttendanceTable(empHours);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Employee Not Found!");
        }
    }//GEN-LAST:event_searchButton1ActionPerformed
    
    
    private void loadAllMonths(){
        monthDropdown.addItem(new ComboItem(null,"Select Month"));
        monthDropdown.addItem(new ComboItem(Calendar.JANUARY,"January"));
        monthDropdown.addItem(new ComboItem(Calendar.FEBRUARY,"February"));
        monthDropdown.addItem(new ComboItem(Calendar.MARCH,"March"));
        monthDropdown.addItem(new ComboItem(Calendar.APRIL,"April"));
        monthDropdown.addItem(new ComboItem(Calendar.MAY,"May"));
        monthDropdown.addItem(new ComboItem(Calendar.JUNE,"June"));
        monthDropdown.addItem(new ComboItem(Calendar.JULY,"July"));
        monthDropdown.addItem(new ComboItem(Calendar.AUGUST,"August"));
        monthDropdown.addItem(new ComboItem(Calendar.SEPTEMBER,"September"));
        monthDropdown.addItem(new ComboItem(Calendar.OCTOBER,"October"));
        monthDropdown.addItem(new ComboItem(Calendar.NOVEMBER,"November"));
        monthDropdown.addItem(new ComboItem(Calendar.DECEMBER,"December"));
    }
    
    private void loadAllYears(){
        yearDropdown.addItem(new ComboItem(null, "Select Year"));
        Arrays.asList(2022, 2023, 2024,2025).forEach(year -> { 
            yearDropdown.addItem(new ComboItem(year,String.valueOf(year)));

        });
    }
    private void loadAttendanceIfReady() {
        if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
            Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
            Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();
            Integer empId = employeeSearchID != null ? employeeSearchID : empAccount.getEmpID();

            if (monthValue != null && year != null) {
                List<Employee> empHours = getEmployeeHours(monthValue, year, empId);

                if (empHours.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No attendance found for the selected month and year.");
                    populateAttendanceTable(new ArrayList<>()); // Optionally clear table
                    return;
                }

                populateAttendanceTable(empHours);
            }
        }
    }
    
    
    private void clothingTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clothingTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clothingTFieldActionPerformed

    private void riceTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_riceTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_riceTFieldActionPerformed

    private void printAllEmployeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printAllEmployeesActionPerformed
        try {
            int selectedRow = employeeTable.getSelectedRow(); // 👈 Check if a row is selected

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "No employee selected. Generating full employee report.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);

                reportGenerator.generateEmployeeReport();
            } else {
                // Get employee ID from the selected row (adjust column index as needed)
                int employeeID = (int) employeeTable.getValueAt(selectedRow, 0); // Assuming column 0 holds the ID
                reportGenerator.generateEmployeeProfileReport(employeeID);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_printAllEmployeesActionPerformed

    private void hourlyTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hourlyTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hourlyTFieldActionPerformed

    private void biMonthlyTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_biMonthlyTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_biMonthlyTFieldActionPerformed

    private void exportAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAttendanceActionPerformed
        try {
            if (employeeSearchID == null && empAccount == null) {
                JOptionPane.showMessageDialog(this, "Please search for a valid employee first.", "Missing Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ComboItem selectedMonth = (ComboItem) monthDropdown.getSelectedItem();
            ComboItem selectedYear = (ComboItem) yearDropdown.getSelectedItem();

            if (selectedMonth == null || selectedMonth.getKey() == null ||
                selectedYear == null || selectedYear.getKey() == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid month and year.", "Missing Info", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer empId = (employeeSearchID != null) ? employeeSearchID : empAccount.getEmpID();
            Integer month = selectedMonth.getKey();
            Integer year = selectedYear.getKey();

            int reportMonth = month + 1; // ✅ Now safe to increment

            reportGenerator.generateTimecardReport(empId, reportMonth, year);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Something went wrong when exporting the attendance report.\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_exportAttendanceActionPerformed

    private void yearDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearDropdownActionPerformed
        loadAttendanceIfReady();
        /*    if(monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null ){
            Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
            Integer year = ((ComboItem)yearDropdown.getSelectedItem()).getKey();
            Integer empId = employeeSearchID != null ? employeeSearchID : empAccount.getEmpID();

            if(monthValue != null & year != null){
                List<Employee> empHours = getEmployeeHours(monthValue,year,empId);
                populateAttendanceTable(empHours);

            }
        }
        */
    }//GEN-LAST:event_yearDropdownActionPerformed

    private void monthDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthDropdownActionPerformed
        loadAttendanceIfReady();
        /*    if(monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null ){
            Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
            Integer year = ((ComboItem)yearDropdown.getSelectedItem()).getKey();
            Integer empId = employeeSearchID != null ? employeeSearchID : empAccount.getEmpID();

            if(monthValue != null & year != null){
                List<Employee> empHours = getEmployeeHours(monthValue,year,empId);
                populateAttendanceTable(empHours);

            }
        }
        */
    }//GEN-LAST:event_monthDropdownActionPerformed

    private void attendanceTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_attendanceTableMouseClicked

    }//GEN-LAST:event_attendanceTableMouseClicked

    
    
    private List<Employee> getEmployeeHours(int month, int year, int empID){
       Calendar dateFrom = Calendar.getInstance();
       dateFrom.set(Calendar.MONTH,month);
       dateFrom.set(Calendar.YEAR, year);
       dateFrom.set(Calendar.DATE,dateFrom.getActualMinimum(Calendar.DATE));
       
       Calendar dateTo = Calendar.getInstance();
       dateTo.set(Calendar.MONTH,month);
       dateTo.set(Calendar.YEAR, year);
       dateTo.set(Calendar.DATE,dateTo.getActualMaximum(Calendar.DATE));
       
       return payrollService.getEmployeeHours(empID, dateFrom.getTime(),dateTo.getTime());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HRDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HRDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HRDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HRDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HRDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel addressLabelValue;
    private javax.swing.JTextField addressTField;
    private javax.swing.JPanel attendance;
    private javax.swing.JTable attendanceTable;
    private javax.swing.JButton attendancelButton;
    private javax.swing.JLabel basicSalaryLabel;
    private javax.swing.JLabel basicSalaryLabelValue;
    private javax.swing.JLabel bdayLabelValue;
    private javax.swing.JTextField biMonthlyTField;
    private javax.swing.JLabel birthdayLabel;
    private javax.swing.JButton changePasswordButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel clothingLabel;
    private javax.swing.JLabel clothingLabelValue;
    private javax.swing.JTextField clothingTField;
    private javax.swing.JPasswordField confirmPasswordTField;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel empAddressLabel;
    private javax.swing.JLabel empBiMonthLabel;
    private javax.swing.JLabel empBirthdayLabel;
    private javax.swing.JLabel empClothAllowLabel;
    private javax.swing.JLabel empDetailsLabel;
    private javax.swing.JLabel empFirstNameLabel;
    private javax.swing.JLabel empHourlyLabel;
    private javax.swing.JLabel empIDLabelValue;
    private javax.swing.JLabel empIDPayLabel;
    private javax.swing.JLabel empIDPayLabelValue;
    private javax.swing.JLabel empLastNameLabel;
    private javax.swing.JPanel empManagement;
    private javax.swing.JButton empManagementButton;
    private javax.swing.JLabel empNumValue;
    private javax.swing.JLabel empPagibigLabel;
    private javax.swing.JLabel empPhilhealthLabel;
    private javax.swing.JLabel empPhoneAllowLabel;
    private javax.swing.JLabel empPhoneLabel;
    private javax.swing.JLabel empPositionLabel;
    private javax.swing.JLabel empRiceLabel;
    private javax.swing.JLabel empSalaryLabel;
    private javax.swing.JLabel empSectionLabel;
    private javax.swing.JLabel empSssLabel;
    private javax.swing.JLabel empStatusLabel;
    private javax.swing.JLabel empSupervisorLabel;
    private javax.swing.JLabel empTinLabel;
    private javax.swing.JLabel employeeIDTField;
    private javax.swing.JTable employeeTable;
    private javax.swing.JPasswordField existingPasswordTField;
    private javax.swing.JButton exportAttendance;
    private javax.swing.JTextField firstNameTField;
    private javax.swing.JLabel fullNameValue;
    private javax.swing.JLabel fullNameValue2;
    private javax.swing.JPanel header;
    private javax.swing.JTextField hourlyTField;
    private javax.swing.JLabel hourlyrateLabel;
    private javax.swing.JLabel hourlyrateLabelValue;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField lastNameTField;
    private javax.swing.JPanel leaveManagement;
    private javax.swing.JButton leaveManagementButton;
    private javax.swing.JTable leaveManagementTable;
    private javax.swing.JPanel leaveRequest;
    private javax.swing.JButton logoutButton;
    private javax.swing.JComboBox<ComboItem> monthDropdown;
    private javax.swing.JPanel motorphdash;
    private javax.swing.JPanel mphCards;
    private javax.swing.JLabel namePayLabel;
    private javax.swing.JLabel namePayLabelValue;
    private javax.swing.JPanel navigation;
    private javax.swing.JSplitPane navigatorSplitPane;
    private javax.swing.JPasswordField newPasswordTField;
    private javax.swing.JLabel pagibigLabel;
    private javax.swing.JLabel pagibigLabelValue;
    private javax.swing.JTextField pagibigTField;
    private javax.swing.JPasswordField passwordTField;
    private javax.swing.JLabel phealthLabel;
    private javax.swing.JLabel philhealthLabelValue;
    private javax.swing.JTextField philhealthTField;
    private javax.swing.JTextField phoneAllowTField;
    private javax.swing.JLabel phoneAllowanceLabel;
    private javax.swing.JLabel phoneAllowanceValue;
    private javax.swing.JLabel phoneLabelValue;
    private javax.swing.JLabel phoneNumberLabel;
    private javax.swing.JTextField phoneTField;
    private javax.swing.JComboBox<ComboItem> positionDropdown;
    private javax.swing.JLabel positionLabelValue;
    private javax.swing.JButton printAllEmployees;
    private javax.swing.JPanel profile;
    private javax.swing.JButton profileButton;
    private javax.swing.JLabel profileLabel;
    private javax.swing.JLabel profilePictureLabel;
    private javax.swing.JLabel riceLabelValue;
    private javax.swing.JLabel riceSubsidyLabel;
    private javax.swing.JTextField riceTField;
    private javax.swing.JLabel salaryDetailsLabel;
    private javax.swing.JLabel salarySlips;
    private javax.swing.JTextField salaryTField;
    private javax.swing.JButton searchButton;
    private javax.swing.JButton searchButton1;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTextField searchTextField1;
    private javax.swing.JLabel sssLabel;
    private javax.swing.JLabel sssLabelValue;
    private javax.swing.JTextField sssTField;
    private javax.swing.JComboBox<ComboItem> statusDropdown;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusLabelValue;
    private javax.swing.JComboBox<ComboItem> supervisorDropdown;
    private javax.swing.JLabel supervisorLabel;
    private javax.swing.JLabel supervisorLabelValue;
    private javax.swing.JLabel tinLabel;
    private javax.swing.JLabel tinLabelValue;
    private javax.swing.JTextField tinTField;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernameTField;
    private javax.swing.JButton viewAllButton;
    private javax.swing.JComboBox<ComboItem> yearDropdown;
    // End of variables declaration//GEN-END:variables
}
