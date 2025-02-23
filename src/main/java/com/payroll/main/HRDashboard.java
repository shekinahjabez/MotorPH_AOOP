/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.payroll.main;

import com.payroll.domain.ComboItem;
import com.payroll.domain.EmployeeAccount;
import com.payroll.domain.EmployeeDetails;
import com.payroll.domain.EmployeeHours;
import com.payroll.domain.EmployeePosition;
import com.payroll.domain.EmployeeStatus;
import com.payroll.domain.LeaveBalance;
import com.payroll.domain.LeaveDetails;
import com.payroll.services.EmployeeDetailsService;
import com.payroll.services.EmployeeAccountService;
import com.payroll.services.LeaveDetailsService;
import com.payroll.services.PayrollService;
import com.payroll.table.TableActionCellEditor;
import com.payroll.table.TableActionCellRender;
import com.payroll.util.DatabaseConnection;
import com.payroll.table.TableActionCellRender;
import com.payroll.table.TableActionEvent;
import com.payroll.util.PayrollUtils;
import static japgolly.scalajs.react.vdom.all.table;
import java.awt.CardLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author leniejoice
 */


public class HRDashboard extends javax.swing.JFrame {
    private DatabaseConnection dbConnection;
    private CardLayout cardLayout;
    private EmployeeAccount empAccount;
    private EmployeeAccountService empAccountService;
    private EmployeeDetailsService empDetailsService;
    private PayrollService payrollService;
    private LeaveDetailsService leaveDetailsService;
    private Integer employeeSearchID;
    
    public HRDashboard(EmployeeAccount empAccount) {
        initComponents();
        cardLayout = (CardLayout)(mphCards.getLayout());
        this.empAccount=empAccount;
        this.dbConnection = new DatabaseConnection();
        updateUserLabels(empAccount);
        this.empAccountService = new EmployeeAccountService(this.dbConnection);
        this.empDetailsService = new EmployeeDetailsService(this.dbConnection);  
        this.payrollService = new PayrollService(this.dbConnection);
        this. leaveDetailsService = new LeaveDetailsService(this.dbConnection);
        TableActionEvent event = new TableActionEvent(){
            @Override
            public void onApprove(int row){
                DefaultTableModel model = (DefaultTableModel) leaveManagementTable.getModel();
                int leaveId = (int) model.getValueAt(row, 0); 
                int empID = (int) model.getValueAt(row, 1); 
                leaveDetailsService.updateLeaveRequestStatus(LeaveDetails.LeaveStatus.APPROVED, leaveId);
                updateLeaveBalance(empID);
                refreshLeaveManagementTable();
            }

            @Override
            public void onDecline(int row) {
                DefaultTableModel model = (DefaultTableModel) leaveManagementTable.getModel();
                int leaveId = (int) model.getValueAt(row, 0);
                int empID = (int) model.getValueAt(row, 1); 
                leaveDetailsService.updateLeaveRequestStatus(LeaveDetails.LeaveStatus.DECLINED, leaveId);
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
    public EmployeeAccount getEmpAccount() {
        return empAccount;
    }
    
     public EmployeeAccount getUsername() {
        return empAccount;
    }

    public void setEmpAccount(EmployeeAccount empAccount) {
        this.empAccount = empAccount;
    }
    
    private void updateLeaveBalance(int empID){
        List<LeaveDetails> leaveDetails =  leaveDetailsService.getLeavesByEmployee(empID);
        LeaveBalance balance = new LeaveBalance();
        balance.setEmpID(empID);
        balance.updateLeaveBalance(leaveDetails);
        leaveDetailsService.updateLeaveBalance(balance);
    }
    
    private void updateUserLabels(EmployeeAccount empAccount) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String tin = empAccount.getEmpDetails().getEmpTIN() != null ? empAccount.getEmpDetails().getEmpTIN() : "";
        String phone = empAccount.getEmpDetails().getEmpPhoneNumber() != null ? empAccount.getEmpDetails().getEmpPhoneNumber(): "" ;
        String pagIbig = empAccount.getEmpDetails().getEmpPagibig() != 0 ? String.valueOf(empAccount.getEmpDetails().getEmpPagibig()) : "";
        String philhealth = empAccount.getEmpDetails().getEmpPhilHealth() != 0 ? String.valueOf(empAccount.getEmpDetails().getEmpPhilHealth()) : "";
        String sss = empAccount.getEmpDetails().getEmpSSS() !=null ? empAccount.getEmpDetails().getEmpSSS() : "";
      
        
        usernameLabel.setText("@"+ empAccount.getEmpUserName()); 
        fullNameValue.setText(empAccount.getEmpDetails().getFormattedName());
        fullNameValue2.setText(empAccount.getEmpDetails().getFormattedName());
        empNumValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpID()));
        empIDLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpID()));
        addressLabelValue.setText(empAccount.getEmpDetails().getEmpAddress());
        phoneLabelValue.setText(phone);
        basicSalaryLabelValue.setText("PHP " + String.valueOf(empAccount.getEmpDetails().getEmpBasicSalary()));
        riceLabelValue.setText("PHP " + String.valueOf(empAccount.getEmpDetails().getEmpRice()));
        phoneAllowanceValue.setText("PHP " + String.valueOf(empAccount.getEmpDetails().getEmpPhone()));
        clothingLabelValue.setText("PHP " + String.valueOf(empAccount.getEmpDetails().getEmpClothing()));
        hourlyrateLabelValue.setText("PHP " + String.valueOf(empAccount.getEmpDetails().getEmpHourlyRate()));
        tinLabelValue.setText(tin);
        pagibigLabelValue.setText(pagIbig);
        sssLabelValue.setText(sss);
        philhealthLabelValue.setText(philhealth);
        
        if(empAccount.getEmpDetails().getEmpBirthday() != null){
             bdayLabelValue.setText(formatter.format(empAccount.getEmpDetails().getEmpBirthday()));
        }

        if(empAccount.getEmpDetails().getEmpImmediateSupervisor() != null){
            supervisorLabelValue.setText(empAccount.getEmpDetails().getEmpImmediateSupervisor().getFormattedName());
        }
        if(empAccount.getEmpDetails().getEmpPosition() != null){
            positionLabelValue.setText(empAccount.getEmpDetails().getEmpPosition().getPosition());
        }
        if(empAccount.getEmpDetails().getEmpStatus() != null){
            statusLabelValue.setText(empAccount.getEmpDetails().getEmpStatus().getStatus());
        }
        
        updatePayrollEmpLabels(empAccount);
    }   
    
    private void updatePayrollEmpLabels(EmployeeAccount empAccount){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        if(empAccount.getEmpDetails().getEmpBirthday() != null){
             bdayLabelValue.setText(formatter.format(empAccount.getEmpDetails().getEmpBirthday()));
        }
        if(empAccount.getEmpDetails().getEmpBirthday() != null){
             birthdayPayLabelValue.setText(formatter.format(empAccount.getEmpDetails().getEmpBirthday()));
        }
        if(empAccount.getEmpDetails().getEmpPosition() != null){
            positionPayLabelValue.setText(empAccount.getEmpDetails().getEmpPosition().getPosition());
        }
        if(empAccount.getEmpDetails().getEmpStatus() != null){
            statusPayLabelValue.setText(empAccount.getEmpDetails().getEmpStatus().getStatus());
        }
        
        
        empIDPayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpID()));
        namePayLabelValue.setText(empAccount.getEmpDetails().getFormattedName());
        
        hourlyRatePayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpHourlyRate()));
        ricePayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpRice()));
        phonePayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpPhone()));
        clothingPayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpClothing()));
        totalAllowPayLabelValue.setText(String.valueOf(PayrollUtils.getTotalAllowance(empAccount.getEmpDetails())));
        basicSalaryPayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpBasicSalary()));
    }
    
    private void updatePayrollLabels(List<EmployeeHours> employeeHours){
        
        if(employeeHours.isEmpty()){
            resetPayrollLabels();
        }else{
            totalHoursPayLabelValue.setText(PayrollUtils.getFormattedTotalHoursWorked(employeeHours));
        
            double basicSalary = PayrollUtils.getBasicSalary(employeeHours, empAccount);
            computedSalaryLabelValue.setText(String.format("%.2f",basicSalary));

            double grossPay = PayrollUtils.getGrossSalary(employeeHours, empAccount);
            grossSalaryPayLabelValue.setText(String.format("%.2f", grossPay));

            double sssContri = payrollService.calculateSssContribution(basicSalary);

            // Contributions
            philhealthContriPayLabelValue.setText(String.format("%.2f", PayrollUtils.calculatePhilHealthContribution(grossPay)));
            pagibigContriPayLabelValue.setText(String.format("%.2f", PayrollUtils.calculatePagibigContribution(grossPay)));
            sssContriPayLabelValue.setText(String.format("%.2f", sssContri));
            totalDeductionsPayLabelValue.setText(String.format("%.2f", PayrollUtils.getTotalDeductions(grossPay,sssContri)));

            // Tax and Net Pay
            taxableIncomePayLabelValue.setText(String.format("%.2f", PayrollUtils.getTaxableIncome(grossPay,sssContri)));
            taxPayLabelValue.setText(String.format("%.2f", PayrollUtils.calculateWithholdingTax(grossPay)));
            netPayLabelValue.setText(String.format("%.2f", PayrollUtils.getNetPay(grossPay,sssContri)));
         }
    }
    
    private void resetPayrollLabels(){
        totalHoursPayLabelValue.setText("0");
        computedSalaryLabelValue.setText("0");
        grossSalaryPayLabelValue.setText("0");
        sssContriPayLabelValue.setText("0");
        philhealthContriPayLabelValue.setText("0");
        pagibigContriPayLabelValue.setText("0");
        totalDeductionsPayLabelValue.setText("0");
        taxableIncomePayLabelValue.setText("0");
        taxPayLabelValue.setText("0");
        netPayLabelValue.setText("0");

    }
    
    private void loadAllPositions(){
        List<EmployeePosition> positions = empDetailsService.getAllPosition();
        positionDropdown.addItem(new ComboItem(null,"Select Position"));
        for(EmployeePosition empPosition : positions){
            positionDropdown.addItem(new ComboItem(empPosition.getId(),empPosition.getPosition()));
        }
    }  
    
    private void loadAllStatus(){
        List<EmployeeStatus> statuses = empDetailsService.getAllStatuses();
        statusDropdown.addItem(new ComboItem(null,"Select Status"));
        for(EmployeeStatus empStatus : statuses){
            statusDropdown.addItem(new ComboItem(empStatus.getId(),empStatus.getStatus()));
        }
    }
    
    private void loadAllEmployee(){
        List<EmployeeDetails> allEmployee = empDetailsService.getAllEmployee();
        supervisorDropdown.addItem(new ComboItem(null,"Select Supervisor"));
        for(EmployeeDetails employeeDetails: allEmployee){
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
        jButton1 = new javax.swing.JButton();
        profilePictureLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        empManagementButton = new javax.swing.JButton();
        payrollButton = new javax.swing.JButton();
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
        attendanceLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox<>();
        payroll = new javax.swing.JPanel();
        salarySlips = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        attendanceTable = new javax.swing.JTable();
        monthDropdown = new javax.swing.JComboBox<>();
        yearDropdown = new javax.swing.JComboBox<>();
        salaryDetailsPayLabel = new javax.swing.JLabel();
        totalHoursPayLabel = new javax.swing.JLabel();
        hourlyRatePayLabel = new javax.swing.JLabel();
        basicSalaryPayLabel = new javax.swing.JLabel();
        allowPayLabel = new javax.swing.JLabel();
        ricePayLabel = new javax.swing.JLabel();
        phonePayLabel = new javax.swing.JLabel();
        clothingPayLabel = new javax.swing.JLabel();
        totalAllowPayLabel = new javax.swing.JLabel();
        deductionsPayLabel = new javax.swing.JLabel();
        sssContriPayLabel = new javax.swing.JLabel();
        philhealthContriPayLabel = new javax.swing.JLabel();
        pagibigContriPayLabel = new javax.swing.JLabel();
        taxPayLabel = new javax.swing.JLabel();
        netPayLabel = new javax.swing.JLabel();
        grossSalaryPayLabel = new javax.swing.JLabel();
        totalHoursPayLabelValue = new javax.swing.JLabel();
        hourlyRatePayLabelValue = new javax.swing.JLabel();
        basicSalaryPayLabelValue = new javax.swing.JLabel();
        grossSalaryPayLabelValue = new javax.swing.JLabel();
        phonePayLabelValue = new javax.swing.JLabel();
        clothingPayLabelValue = new javax.swing.JLabel();
        totalAllowPayLabelValue = new javax.swing.JLabel();
        ricePayLabelValue = new javax.swing.JLabel();
        sssContriPayLabelValue = new javax.swing.JLabel();
        philhealthContriPayLabelValue = new javax.swing.JLabel();
        pagibigContriPayLabelValue = new javax.swing.JLabel();
        taxPayLabelValue = new javax.swing.JLabel();
        netPayLabelValue = new javax.swing.JLabel();
        totalDeductionsPayLabel = new javax.swing.JLabel();
        totalDeductionsPayLabelValue = new javax.swing.JLabel();
        taxableIncomePayLabel = new javax.swing.JLabel();
        taxableIncomePayLabelValue = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        empIDPayLabel = new javax.swing.JLabel();
        namePayLabel = new javax.swing.JLabel();
        statusPayLabel = new javax.swing.JLabel();
        positionPayLabel = new javax.swing.JLabel();
        empIDPayLabelValue = new javax.swing.JLabel();
        namePayLabelValue = new javax.swing.JLabel();
        positionPayLabelValue = new javax.swing.JLabel();
        statusPayLabelValue = new javax.swing.JLabel();
        birthdayPayLabel = new javax.swing.JLabel();
        birthdayPayLabelValue = new javax.swing.JLabel();
        computedSalaryLabel = new javax.swing.JLabel();
        computedSalaryLabelValue = new javax.swing.JLabel();
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
        birthdayTField = new javax.swing.JTextField();
        empPhoneLabel = new javax.swing.JLabel();
        phoneTField = new javax.swing.JTextField();
        empSalaryLabel = new javax.swing.JLabel();
        salaryTField = new javax.swing.JTextField();
        empHourlyLabel = new javax.swing.JLabel();
        riceTField = new javax.swing.JTextField();
        govIdsLabel = new javax.swing.JLabel();
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
        hourlyTField = new javax.swing.JTextField();
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

        jButton1.setBackground(new java.awt.Color(255, 102, 0));
        jButton1.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/headphones-6-16.png"))); // NOI18N
        jButton1.setText("Need some help?");
        jButton1.setAlignmentY(0.0F);
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.setInheritsPopupMenu(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        payrollButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        payrollButton.setForeground(new java.awt.Color(255, 255, 255));
        payrollButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/wallet-16.png"))); // NOI18N
        payrollButton.setText(" Payroll");
        payrollButton.setContentAreaFilled(false);
        payrollButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        payrollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollButtonActionPerformed(evt);
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
            .addComponent(payrollButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(payrollButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(empManagementButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leaveManagementButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 597, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addGap(4, 4, 4)
                        .addComponent(jLabel31)
                        .addGap(17, 17, 17))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addComponent(pagibigLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel6))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(fullNameValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addressLabelValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(positionLabelValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(empIDLabelValue)))
                .addGap(33, 33, 33)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneNumberLabel)
                    .addComponent(phoneLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supervisorLabel)
                    .addComponent(supervisorLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(birthdayLabel)
                    .addComponent(bdayLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tinLabel)
                    .addComponent(tinLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sssLabel)
                    .addComponent(sssLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pagibigLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagibigLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phealthLabel)
                    .addComponent(philhealthLabelValue))
                .addGap(21, 21, 21)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(basicSalaryLabel)
                    .addComponent(basicSalaryLabelValue))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hourlyrateLabel)
                            .addComponent(hourlyrateLabelValue))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(phoneAllowanceValue)
                            .addComponent(phoneAllowanceLabel))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(clothingLabel)
                            .addComponent(clothingLabelValue))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(riceSubsidyLabel)
                            .addComponent(riceLabelValue))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel31)
                        .addGap(23, 23, 23))))
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
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mphCards.add(profile, "card3");

        attendance.setBackground(new java.awt.Color(229, 229, 229));

        attendanceLabel1.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        attendanceLabel1.setText("Attendance Tracker");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select Month---", "August", "September", "October", "November" }));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Day", "Time-in", "Time-out", "Time Worked"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Select Year--", "2023", "2024", "2025" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 742, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        javax.swing.GroupLayout attendanceLayout = new javax.swing.GroupLayout(attendance);
        attendance.setLayout(attendanceLayout);
        attendanceLayout.setHorizontalGroup(
            attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendanceLayout.createSequentialGroup()
                .addGroup(attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(attendanceLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(attendanceLabel1))
                    .addGroup(attendanceLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        attendanceLayout.setVerticalGroup(
            attendanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendanceLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(attendanceLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
        );

        mphCards.add(attendance, "card2");

        payroll.setBackground(new java.awt.Color(229, 229, 229));

        salarySlips.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        salarySlips.setText("Payroll Details");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        attendanceTable.setBackground(new java.awt.Color(211, 211, 211));
        attendanceTable.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        attendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Time-in", "Time-out", "Total Hours"
            }
        ));
        attendanceTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
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

        salaryDetailsPayLabel.setFont(new java.awt.Font("Century Gothic", 2, 18)); // NOI18N
        salaryDetailsPayLabel.setText("Salary Details");

        totalHoursPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        totalHoursPayLabel.setText("Total Hours Worked:");

        hourlyRatePayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        hourlyRatePayLabel.setText("Hourly Rate:");

        basicSalaryPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        basicSalaryPayLabel.setText("Basic Salary:");

        allowPayLabel.setFont(new java.awt.Font("Century Gothic", 2, 18)); // NOI18N
        allowPayLabel.setText("Allowances");

        ricePayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        ricePayLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ricePayLabel.setText("Rice Subsidy:");
        ricePayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        phonePayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        phonePayLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        phonePayLabel.setText("Phone Allow.:");
        phonePayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        clothingPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        clothingPayLabel.setText("Clothing Allow.:");

        totalAllowPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        totalAllowPayLabel.setText("Total Allowance:");

        deductionsPayLabel.setFont(new java.awt.Font("Century Gothic", 2, 18)); // NOI18N
        deductionsPayLabel.setText("Deductions");

        sssContriPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        sssContriPayLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sssContriPayLabel.setText("SSS Cont:");
        sssContriPayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        philhealthContriPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        philhealthContriPayLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        philhealthContriPayLabel.setText("Philhealth Cont:");
        philhealthContriPayLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        pagibigContriPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        pagibigContriPayLabel.setText("PAG-IBIG Contr:");

        taxPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        taxPayLabel.setText("Withholding tax:");

        netPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        netPayLabel.setText("Net Pay:");

        grossSalaryPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        grossSalaryPayLabel.setText("Gross Salary:");

        totalHoursPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        hourlyRatePayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        basicSalaryPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        grossSalaryPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        phonePayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        clothingPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        totalAllowPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        ricePayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        ricePayLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        sssContriPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        philhealthContriPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        philhealthContriPayLabelValue.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        philhealthContriPayLabelValue.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        pagibigContriPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        taxPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        netPayLabelValue.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N

        totalDeductionsPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        totalDeductionsPayLabel.setText("Total Deductions: ");

        totalDeductionsPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        taxableIncomePayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        taxableIncomePayLabel.setText("Taxable Income");

        taxableIncomePayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Century Gothic", 2, 18)); // NOI18N
        jLabel8.setText("Employee Details");

        empIDPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empIDPayLabel.setText("EMP. ID");

        namePayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        namePayLabel.setText("Name");

        statusPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        statusPayLabel.setText("Status:");

        positionPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        positionPayLabel.setText("Position:");

        empIDPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        namePayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        positionPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        statusPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        birthdayPayLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        birthdayPayLabel.setText("Birthday:");

        birthdayPayLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        computedSalaryLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        computedSalaryLabel.setText("Computed Salary:");

        computedSalaryLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(namePayLabel)
                            .addComponent(positionPayLabel))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(positionPayLabelValue)
                                .addGap(317, 317, 317))
                            .addComponent(namePayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addComponent(monthDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(yearDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hourlyRatePayLabel)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(totalHoursPayLabel)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(birthdayPayLabel)
                                .addGap(94, 94, 94)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(totalHoursPayLabelValue)
                            .addComponent(statusPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(basicSalaryPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(hourlyRatePayLabelValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(empIDPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                            .addComponent(birthdayPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(salaryDetailsPayLabel)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(computedSalaryLabel)
                            .addComponent(grossSalaryPayLabel))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(computedSalaryLabelValue)
                            .addComponent(grossSalaryPayLabelValue)))
                    .addComponent(basicSalaryPayLabel)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(taxPayLabel)
                            .addComponent(taxableIncomePayLabel))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(taxableIncomePayLabelValue)
                            .addComponent(taxPayLabelValue)))
                    .addComponent(empIDPayLabel)
                    .addComponent(jLabel8)
                    .addComponent(statusPayLabel))
                .addGap(84, 84, 84)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(sssContriPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sssContriPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(philhealthContriPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(philhealthContriPayLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(netPayLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(netPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(totalDeductionsPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(totalDeductionsPayLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addComponent(clothingPayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(27, 27, 27))
                                            .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(ricePayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(totalAllowPayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(deductionsPayLabel)
                                                        .addComponent(phonePayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                .addGap(37, 37, 37)))
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(totalAllowPayLabelValue, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(clothingPayLabelValue, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(phonePayLabelValue, javax.swing.GroupLayout.Alignment.TRAILING))
                                            .addComponent(ricePayLabelValue, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                        .addComponent(pagibigContriPayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pagibigContriPayLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addComponent(allowPayLabel)
                        .addContainerGap())))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(namePayLabel)
                            .addComponent(namePayLabelValue)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(allowPayLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(positionPayLabel)
                    .addComponent(positionPayLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empIDPayLabel)
                            .addComponent(empIDPayLabelValue)
                            .addComponent(ricePayLabel)
                            .addComponent(ricePayLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(birthdayPayLabel)
                            .addComponent(birthdayPayLabelValue)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(phonePayLabel)
                                .addComponent(phonePayLabelValue)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(monthDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yearDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusPayLabel)
                            .addComponent(statusPayLabelValue)
                            .addComponent(clothingPayLabel)
                            .addComponent(clothingPayLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(basicSalaryPayLabel)
                                .addComponent(basicSalaryPayLabelValue))
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(totalAllowPayLabel)
                                .addComponent(totalAllowPayLabelValue)))
                        .addGap(58, 58, 58)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deductionsPayLabel)
                            .addComponent(salaryDetailsPayLabel))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sssContriPayLabel)
                            .addComponent(sssContriPayLabelValue)
                            .addComponent(totalHoursPayLabel)
                            .addComponent(totalHoursPayLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(philhealthContriPayLabel)
                            .addComponent(philhealthContriPayLabelValue)
                            .addComponent(hourlyRatePayLabel)
                            .addComponent(hourlyRatePayLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pagibigContriPayLabel)
                            .addComponent(pagibigContriPayLabelValue)
                            .addComponent(computedSalaryLabel)
                            .addComponent(computedSalaryLabelValue))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalDeductionsPayLabel)
                            .addComponent(totalDeductionsPayLabelValue)
                            .addComponent(grossSalaryPayLabel)
                            .addComponent(grossSalaryPayLabelValue))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(taxableIncomePayLabel)
                                    .addComponent(taxableIncomePayLabelValue))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(taxPayLabel)
                                    .addComponent(taxPayLabelValue)))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(netPayLabel)
                                    .addComponent(netPayLabelValue)))))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        javax.swing.GroupLayout payrollLayout = new javax.swing.GroupLayout(payroll);
        payroll.setLayout(payrollLayout);
        payrollLayout.setHorizontalGroup(
            payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payrollLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(payrollLayout.createSequentialGroup()
                        .addComponent(salarySlips)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(searchButton1))
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        payrollLayout.setVerticalGroup(
            payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payrollLayout.createSequentialGroup()
                .addGroup(payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(payrollLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchButton1)
                            .addComponent(searchTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, payrollLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(salarySlips)
                        .addGap(30, 30, 30)))
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mphCards.add(payroll, "card4");

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

        govIdsLabel.setFont(new java.awt.Font("Century Gothic", 2, 12)); // NOI18N
        govIdsLabel.setText("Government IDs");

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

        salaryDetailsLabel.setFont(new java.awt.Font("Century Gothic", 2, 12)); // NOI18N
        salaryDetailsLabel.setText("Salary Details");

        empBiMonthLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empBiMonthLabel.setText("Bi-Monthly");

        empRiceLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empRiceLabel.setText("Rice Subsidy");

        empPhoneAllowLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPhoneAllowLabel.setText("Phone Allow.");

        empClothAllowLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empClothAllowLabel.setText("Clothing Allow.");

        empStatusLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empStatusLabel.setText("Status");

        empSupervisorLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empSupervisorLabel.setText("Supervisor");

        clothingTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clothingTFieldActionPerformed(evt);
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

        employeeIDTField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N

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
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Emp. ID", "Last Name", "First Name", "SSS", "PhilHealth", "TIN", "PAG-IBIG"
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

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(empSalaryLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(salaryTField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(empHourlyLabel)
                                    .addComponent(empBiMonthLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hourlyTField)
                                    .addComponent(biMonthlyTField)))
                            .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(empPhoneAllowLabel)
                                    .addComponent(empRiceLabel))
                                .addGap(18, 18, 18)
                                .addComponent(phoneAllowTField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(updateButton)
                                    .addComponent(empClothAllowLabel))
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(clothingTField))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addComponent(deleteButton)))
                                .addGap(3, 3, 3))))
                    .addComponent(govIdsLabel)
                    .addComponent(salaryDetailsLabel)
                    .addComponent(empDetailsLabel)
                    .addComponent(jLabel5)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empLastNameLabel)
                            .addComponent(empAddressLabel)
                            .addComponent(empBirthdayLabel)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(empPositionLabel)
                                .addComponent(empPhoneLabel))
                            .addComponent(jLabel3)
                            .addComponent(empSupervisorLabel))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                    .addComponent(birthdayTField, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                    .addGap(18, 18, 18)
                                    .addComponent(empStatusLabel)
                                    .addGap(18, 18, 18)
                                    .addComponent(statusDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(phoneTField))
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(addressTField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lastNameTField, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                        .addComponent(usernameTField))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(empFirstNameLabel)
                                        .addComponent(jLabel7))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(firstNameTField, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                        .addComponent(passwordTField)))
                                .addComponent(clearButton))
                            .addComponent(supervisorDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeIDTField)
                            .addComponent(positionDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(riceTField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(empPagibigLabel)
                                    .addComponent(empTinLabel)
                                    .addComponent(empPhilhealthLabel))
                                .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addComponent(empSssLabel)
                                    .addGap(48, 48, 48)))
                            .addGap(25, 25, 25)
                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                    .addComponent(sssTField, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(2, 2, 2))
                                .addComponent(tinTField, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(pagibigTField, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(philhealthTField)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(viewAllButton)
                        .addGap(524, 524, 524))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 688, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(empDetailsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(employeeIDTField)
                    .addComponent(clearButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(usernameTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwordTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(viewAllButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empLastNameLabel)
                            .addComponent(lastNameTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empFirstNameLabel)
                            .addComponent(firstNameTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empAddressLabel)
                            .addComponent(addressTField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empBirthdayLabel)
                            .addComponent(birthdayTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empStatusLabel)
                            .addComponent(statusDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(phoneTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empPhoneLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(positionDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empPositionLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empSupervisorLabel)
                            .addComponent(supervisorDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(govIdsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(empPagibigLabel)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(sssTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(empSssLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tinTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(empTinLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pagibigTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(philhealthTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empPhilhealthLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(salaryDetailsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empSalaryLabel)
                            .addComponent(salaryTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empRiceLabel)
                            .addComponent(riceTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empPhoneAllowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hourlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(phoneAllowTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(empHourlyLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empBiMonthLabel)
                            .addComponent(empClothAllowLabel)
                            .addComponent(clothingTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(biMonthlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteButton)
                            .addComponent(updateButton)
                            .addComponent(addButton))
                        .addGap(59, 59, 59))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
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
                .addGap(39, 39, 39))
        );
        empManagementLayout.setVerticalGroup(
            empManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(empManagementLayout.createSequentialGroup()
                .addGap(25, 25, 25)
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
                .addContainerGap(270, Short.MAX_VALUE))
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
                .addContainerGap(153, Short.MAX_VALUE))
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
                .addContainerGap(58, Short.MAX_VALUE))
        );
        leaveRequestLayout.setVerticalGroup(
            leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveRequestLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel29)
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
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

    private void payrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payrollButtonActionPerformed
    cardLayout.show(mphCards, "card4"); 
    }//GEN-LAST:event_payrollButtonActionPerformed

    private void empManagementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_empManagementButtonActionPerformed
    cardLayout.show(mphCards, "card5"); 
    }//GEN-LAST:event_empManagementButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     
    }//GEN-LAST:event_jButton1ActionPerformed
    private void refreshLeaveManagementTable(){  
        List<LeaveDetails> leaveDetails =  leaveDetailsService.getAllLeaveRequestByStatus(LeaveDetails.LeaveStatus.PENDING);
            DefaultTableModel model = (DefaultTableModel) leaveManagementTable.getModel();
            model.setRowCount(0);

            for(LeaveDetails leave : leaveDetails) {
                Vector<Object> rowData = new Vector<>();
                rowData.add(leave.getId());
                rowData.add(leave.getEmpID());
                EmployeeDetails empDetails = empDetailsService.getByEmpID(leave.getEmpID());
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
        EmployeeDetails empDetails = empDetailsService.getByEmpID(empID);
        employeeIDTField.setText(String.valueOf(empDetails.getEmpID()));
        lastNameTField.setText(empDetails.getLastName());
        firstNameTField.setText(empDetails.getFirstName());
        sssTField.setText(empDetails.getEmpSSS());
        philhealthTField.setText(String.valueOf(empDetails.getEmpPhilHealth()));
        tinTField.setText(String.valueOf(empDetails.getEmpTIN()));
        pagibigTField.setText(String.valueOf(empDetails.getEmpPagibig()));
        addressTField.setText(empDetails.getEmpAddress());
        birthdayTField.setText(empDetails.getFormattedBirthday());
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
        
        EmployeeAccount empAccount = empAccountService.getByEmpID(empID);
        usernameTField.setText(empAccount.getEmpUserName());
        passwordTField.setText(empAccount.getEmpPassword());
    }
    
    private void validateRequiredFields(EmployeeDetails empDetails){
        List<String> errors = new ArrayList();
        
        if(StringUtils.isEmpty(empDetails.getFirstName())){
           errors.add("First Name");
        }
        if(StringUtils.isEmpty(empDetails.getLastName())){
           errors.add("Last Name");
        }
        if(StringUtils.isEmpty(empDetails.getEmpAddress())){
           errors.add("Address");
        }
        if(StringUtils.isEmpty(empDetails.getEmpPhoneNumber())){
           errors.add("Phone Number");
        }
        if(StringUtils.isEmpty(empDetails.getEmpSSS())){
           errors.add("SSS");
        }
        if(StringUtils.isEmpty(empDetails.getEmpTIN())){
           errors.add("TIN");
        }
        if(empDetails.getEmpBirthday() == null ){
           errors.add("Birthday");
        }
        if(empDetails.getEmpPosition() == null ){
           errors.add("Position");
        }
        if(empDetails.getEmpStatus()== null ){
           errors.add("Status");
        }
        if(empDetails.getEmpImmediateSupervisor()== null ){
           errors.add("Supervisor");
        }
        if(!errors.isEmpty()){
            String errorMessage = "These fields are required: \n";
            for(String s: errors){
                errorMessage += s+"\n";
            }
            JOptionPane.showMessageDialog(this, errorMessage);
            throw new RuntimeException();
        }
    }
    
    private EmployeeDetails updateEmpDetailValues(){
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
        long pagibig = !pagibigTField.getText().trim().equals("")? Long.valueOf(pagibigTField.getText().trim()): 0;
        String sss = sssTField.getText().trim() !=null ? sssTField.getText().trim() :"";
        String tin = tinTField.getText().trim() !=null ? tinTField.getText().trim():"";
        long philhealth = !philhealthTField.getText().equals("") ? Long.valueOf(philhealthTField.getText().trim()): 0;
    
        
        EmployeeDetails empDetails = new EmployeeDetails();
        
        empDetails.setLastName(lastname);
        empDetails.setFirstName(firstname);
        try {
            // Parsing the string to java.util.Date
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthdayTField.getText().trim());

            // Converting java.util.Date to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            // Setting the sqlDate to empDetails
            empDetails.setEmpBirthday(sqlDate);
        } catch (ParseException ex) {
            //JOptionPane.showMessageDialog(null, "Invalid date format. Please use yyyy-MM-dd");
            // Exit the method if the date format is invalid
        //empDetails.setEmpBirthday(firstNameTField.getText().trim());
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
            empDetails.setEmpPosition(empDetailsService.getPositionById(positionValue.getKey()));
        }
        if(statusValue.getKey() != null){
            empDetails.setEmpStatus(empDetailsService.getStatusById(statusValue.getKey()));
        }
        if(supervisorValue.getKey() !=null){
            empDetails.setEmpImmediateSupervisor(empDetailsService.getByEmpID(supervisorValue.getKey()));  
        }
        
        validateRequiredFields(empDetails);
        
        return empDetails;
    }
    private EmployeeAccount updateEmpAccountValues(){
        EmployeeAccount empAccount = new EmployeeAccount();
        empAccount.setEmpUserName(usernameTField.getText());
        empAccount.setEmpPassword(passwordTField.getText());
        return empAccount;
    }
    
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        int empID = Integer.parseInt(searchTextField.getText().trim());
        loadEmployeeValues(empID); 
        refreshTable(); 
        
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        boolean found = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (empID == Integer.parseInt(model.getValueAt(i, 0).toString())) {
                employeeTable.setRowSelectionInterval(i, i);
                employeeTable.scrollRectToVisible(employeeTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "Employee Not Found!");
            clearButtonActionPerformed(evt); 
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

    private void clothingTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clothingTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clothingTFieldActionPerformed

    private void populateAttendanceTable(List<EmployeeHours> empHours){
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);

        for(EmployeeHours employeeHours : empHours) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(employeeHours.getDate());
            rowData.add(employeeHours.getTimeIn());
            rowData.add(employeeHours.getTimeOut());
            rowData.add(employeeHours.getFormattedHoursWorked());
            model.addRow(rowData);
        }
    }
    private void viewAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewAllButtonActionPerformed
        List<EmployeeDetails> allEmployee =  empDetailsService.getAllEmployee();
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);

        for(EmployeeDetails empDetails : allEmployee) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(empDetails.getEmpID());
            rowData.add(empDetails.getLastName());
            rowData.add(empDetails.getFirstName());
            rowData.add(empDetails.getEmpSSS());
            rowData.add(empDetails.getEmpPhilHealth());
            rowData.add(empDetails.getEmpTIN());
            rowData.add(empDetails. getEmpPagibig());
            model.addRow(rowData);
        }

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

    private void riceTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_riceTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_riceTFieldActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

        lastNameTField.setText("");
        firstNameTField.setText("");
        addressTField.setText("");
        birthdayTField.setText("");
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
                leaveDetailsService.deleteLeaveBalance(empID);
                leaveDetailsService.deleteLeaveRequestbyEmpID(empID);
                empAccountService.deleteEmpAccount(empID);
                empDetailsService.deleteEmpDetails(empID);
                
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
            // Update employee details
            EmployeeDetails empDetails = updateEmpDetailValues();
            empDetailsService.updateEmployeeDetails(empDetails);

            // Update employee account
            EmployeeAccount empAccount = updateEmpAccountValues();
            empAccount.setEmpID(empDetails.getEmpID()); // Linking account with details
            empAccount.setEmpDetails(empDetails);
            empAccountService.updateEmployeeAccount(empAccount);

            // Refresh the displayed data (e.g., in a table)
            refreshTable();

            // If the updated account is currently logged-in or selected, update UI labels
            if (getEmpAccount().getEmpDetails().getEmpID() == empAccount.getEmpID()) {
                updateUserLabels(empAccount);
            }

            // Show success message
            JOptionPane.showMessageDialog(null, "Employee has been successfully updated", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
        // Show error message if exception occurs
            JOptionPane.showMessageDialog(null, "Error updating employee: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        EmployeeDetails empDetails = updateEmpDetailValues();
        empDetailsService.saveEmployeeDetails(empDetails);

        EmployeeAccount empAccount = updateEmpAccountValues();
        empAccountService.saveUserAccount(empAccount,empDetails);
        
        LeaveBalance  leaveBalance = new LeaveBalance();
        leaveBalance.setEmpID(empDetails.getEmpID());
        leaveDetailsService.saveLeaveBalance(leaveBalance);
       
        JOptionPane.showMessageDialog(null, "Account added successfully!");
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
        employeeSearchID = (empId == "" || empId == null) ? null : Integer.parseInt(empId);
        
        if(employeeSearchID !=null){
            EmployeeAccount empAccount = empAccountService.getByEmpID(employeeSearchID);
            if(empAccount != null){
                updatePayrollEmpLabels(empAccount);
                if(monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null ){
                    Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
                    Integer year = ((ComboItem)yearDropdown.getSelectedItem()).getKey();

                    if(monthValue != null & year != null){
                        List<EmployeeHours> empHours = getEmployeeHours(monthValue,year,employeeSearchID);
                        populateAttendanceTable(empHours);
                        updatePayrollLabels(empHours);
                    }
                }     
            }else{
                JOptionPane.showMessageDialog(this, "Employee Not Found!");
            }   
        }else{
            JOptionPane.showMessageDialog(this, "Please input Employee ID!");
        }
    }//GEN-LAST:event_searchButton1ActionPerformed
    
    private void clearPayrollLabels(){
        empIDPayLabelValue.setText("");
        birthdayPayLabelValue.setText("");
        namePayLabelValue.setText("");
        positionPayLabelValue.setText("");
        statusPayLabelValue.setText("");
        hourlyRatePayLabelValue.setText("");
        basicSalaryPayLabelValue.setText("");
        ricePayLabelValue.setText("");
        phonePayLabelValue.setText("");
        clothingPayLabelValue.setText("");
        totalAllowPayLabelValue.setText("");
                
        resetPayrollLabels();
    }
    
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
        Arrays.asList(2022, 2023, 2024).forEach(year -> { 
            yearDropdown.addItem(new ComboItem(year,String.valueOf(year)));

        });
    }
    
    
    private void monthDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthDropdownActionPerformed
        if(monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null ){
            Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
            Integer year = ((ComboItem)yearDropdown.getSelectedItem()).getKey();
            Integer empId = employeeSearchID != null ? employeeSearchID : empAccount.getEmpID();


            if(monthValue != null & year != null){
                List<EmployeeHours> empHours = getEmployeeHours(monthValue,year,empId);
                populateAttendanceTable(empHours);
                updatePayrollLabels(empHours);
            }
        }    
    }//GEN-LAST:event_monthDropdownActionPerformed

    private void yearDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearDropdownActionPerformed
        if(monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null ){
            Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
            Integer year = ((ComboItem)yearDropdown.getSelectedItem()).getKey();
            Integer empId = employeeSearchID != null ? employeeSearchID : empAccount.getEmpID();

            if(monthValue != null & year != null){
                List<EmployeeHours> empHours = getEmployeeHours(monthValue,year,empId);
                populateAttendanceTable(empHours);
                updatePayrollLabels(empHours);
            }
        }    
    }//GEN-LAST:event_yearDropdownActionPerformed
    
    
    private List<EmployeeHours> getEmployeeHours(int month, int year, int empID){
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
    private javax.swing.JLabel allowPayLabel;
    private javax.swing.JPanel attendance;
    private javax.swing.JLabel attendanceLabel1;
    private javax.swing.JTable attendanceTable;
    private javax.swing.JLabel basicSalaryLabel;
    private javax.swing.JLabel basicSalaryLabelValue;
    private javax.swing.JLabel basicSalaryPayLabel;
    private javax.swing.JLabel basicSalaryPayLabelValue;
    private javax.swing.JLabel bdayLabelValue;
    private javax.swing.JTextField biMonthlyTField;
    private javax.swing.JLabel birthdayLabel;
    private javax.swing.JLabel birthdayPayLabel;
    private javax.swing.JLabel birthdayPayLabelValue;
    private javax.swing.JTextField birthdayTField;
    private javax.swing.JButton changePasswordButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel clothingLabel;
    private javax.swing.JLabel clothingLabelValue;
    private javax.swing.JLabel clothingPayLabel;
    private javax.swing.JLabel clothingPayLabelValue;
    private javax.swing.JTextField clothingTField;
    private javax.swing.JLabel computedSalaryLabel;
    private javax.swing.JLabel computedSalaryLabelValue;
    private javax.swing.JPasswordField confirmPasswordTField;
    private javax.swing.JLabel deductionsPayLabel;
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
    private javax.swing.JTextField firstNameTField;
    private javax.swing.JLabel fullNameValue;
    private javax.swing.JLabel fullNameValue2;
    private javax.swing.JLabel govIdsLabel;
    private javax.swing.JLabel grossSalaryPayLabel;
    private javax.swing.JLabel grossSalaryPayLabelValue;
    private javax.swing.JPanel header;
    private javax.swing.JLabel hourlyRatePayLabel;
    private javax.swing.JLabel hourlyRatePayLabelValue;
    private javax.swing.JTextField hourlyTField;
    private javax.swing.JLabel hourlyrateLabel;
    private javax.swing.JLabel hourlyrateLabelValue;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
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
    private javax.swing.JLabel netPayLabel;
    private javax.swing.JLabel netPayLabelValue;
    private javax.swing.JPasswordField newPasswordTField;
    private javax.swing.JLabel pagibigContriPayLabel;
    private javax.swing.JLabel pagibigContriPayLabelValue;
    private javax.swing.JLabel pagibigLabel;
    private javax.swing.JLabel pagibigLabelValue;
    private javax.swing.JTextField pagibigTField;
    private javax.swing.JPasswordField passwordTField;
    private javax.swing.JPanel payroll;
    private javax.swing.JButton payrollButton;
    private javax.swing.JLabel phealthLabel;
    private javax.swing.JLabel philhealthContriPayLabel;
    private javax.swing.JLabel philhealthContriPayLabelValue;
    private javax.swing.JLabel philhealthLabelValue;
    private javax.swing.JTextField philhealthTField;
    private javax.swing.JTextField phoneAllowTField;
    private javax.swing.JLabel phoneAllowanceLabel;
    private javax.swing.JLabel phoneAllowanceValue;
    private javax.swing.JLabel phoneLabelValue;
    private javax.swing.JLabel phoneNumberLabel;
    private javax.swing.JLabel phonePayLabel;
    private javax.swing.JLabel phonePayLabelValue;
    private javax.swing.JTextField phoneTField;
    private javax.swing.JComboBox<ComboItem> positionDropdown;
    private javax.swing.JLabel positionLabelValue;
    private javax.swing.JLabel positionPayLabel;
    private javax.swing.JLabel positionPayLabelValue;
    private javax.swing.JPanel profile;
    private javax.swing.JButton profileButton;
    private javax.swing.JLabel profileLabel;
    private javax.swing.JLabel profilePictureLabel;
    private javax.swing.JLabel riceLabelValue;
    private javax.swing.JLabel ricePayLabel;
    private javax.swing.JLabel ricePayLabelValue;
    private javax.swing.JLabel riceSubsidyLabel;
    private javax.swing.JTextField riceTField;
    private javax.swing.JLabel salaryDetailsLabel;
    private javax.swing.JLabel salaryDetailsPayLabel;
    private javax.swing.JLabel salarySlips;
    private javax.swing.JTextField salaryTField;
    private javax.swing.JButton searchButton;
    private javax.swing.JButton searchButton1;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JTextField searchTextField1;
    private javax.swing.JLabel sssContriPayLabel;
    private javax.swing.JLabel sssContriPayLabelValue;
    private javax.swing.JLabel sssLabel;
    private javax.swing.JLabel sssLabelValue;
    private javax.swing.JTextField sssTField;
    private javax.swing.JComboBox<ComboItem> statusDropdown;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusLabelValue;
    private javax.swing.JLabel statusPayLabel;
    private javax.swing.JLabel statusPayLabelValue;
    private javax.swing.JComboBox<ComboItem> supervisorDropdown;
    private javax.swing.JLabel supervisorLabel;
    private javax.swing.JLabel supervisorLabelValue;
    private javax.swing.JLabel taxPayLabel;
    private javax.swing.JLabel taxPayLabelValue;
    private javax.swing.JLabel taxableIncomePayLabel;
    private javax.swing.JLabel taxableIncomePayLabelValue;
    private javax.swing.JLabel tinLabel;
    private javax.swing.JLabel tinLabelValue;
    private javax.swing.JTextField tinTField;
    private javax.swing.JLabel totalAllowPayLabel;
    private javax.swing.JLabel totalAllowPayLabelValue;
    private javax.swing.JLabel totalDeductionsPayLabel;
    private javax.swing.JLabel totalDeductionsPayLabelValue;
    private javax.swing.JLabel totalHoursPayLabel;
    private javax.swing.JLabel totalHoursPayLabelValue;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernameTField;
    private javax.swing.JButton viewAllButton;
    private javax.swing.JComboBox<ComboItem> yearDropdown;
    // End of variables declaration//GEN-END:variables
}
