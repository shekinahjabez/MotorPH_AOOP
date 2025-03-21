/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.payroll.main;

import com.payroll.subdomain.ComboItem;
import com.payroll.domain.IT;
import com.payroll.domain.Employee;
import com.payroll.domain.LeaveBalance;
import com.payroll.domain.HR;
import com.payroll.domain.HR.LeaveStatus;
import com.payroll.subdomain.LeaveType;
import com.payroll.services.ITService;
import com.payroll.services.EmployeeService;
import com.payroll.services.FinanceService;
import com.payroll.util.DatabaseConnection;
import com.payroll.domain.SalaryCalculation;
import java.awt.CardLayout;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author leniejoice
 */


public class EmployeeDashboard extends javax.swing.JFrame {
    private DatabaseConnection dbConnection;
    private CardLayout cardLayout;
    private IT empAccount;
    private HR leaveDetails;
    private ITService empAccountService;
    private FinanceService payrollService;
    private EmployeeService empService;
    private int currentEmployeeId;
    
    
    
    public EmployeeDashboard(IT empAccount) {
        initComponents();
        cardLayout = (CardLayout)(mphCards.getLayout());
        this.empAccount=empAccount;
        this.currentEmployeeId = empAccount.getEmpDetails().getEmpID(); 
        this.dbConnection = new DatabaseConnection();
        updateUserLabels(empAccount);
        this.empAccountService = new ITService(this.dbConnection);
        this.payrollService = new FinanceService(this.dbConnection);
        this.empService = new EmployeeService(this.dbConnection);
        checkAttendanceStatusForToday();
        loadAllYears();
        loadAllMonths();
        loadAllLeaveTypes();
    }
    public EmployeeDashboard(){
        
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
    
    
    private void updateUserLabels(IT empAccount) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String tin = empAccount.getEmpDetails().getEmpTIN() != null ? empAccount.getEmpDetails().getEmpTIN() : "";
        String phone = empAccount.getEmpDetails().getEmpPhoneNumber() != null ? empAccount.getEmpDetails().getEmpPhoneNumber(): "" ;
        String pagIbig = empAccount.getEmpDetails().getEmpPagibig() != 0 ? String.valueOf(empAccount.getEmpDetails().getEmpPagibig()) : "";
        String philhealth = empAccount.getEmpDetails().getEmpPhilHealth() != 0 ? String.valueOf(empAccount.getEmpDetails().getEmpPhilHealth()) : "";
        String sss = empAccount.getEmpDetails().getEmpSSS() !=null ? empAccount.getEmpDetails().getEmpSSS() : "";
      
        
        usernameLabel.setText("@"+ empAccount.getEmpUserName()); 
        fullNameValue.setText(empAccount.getEmpDetails().getFormattedName());
        fullNameValue2.setText(empAccount.getEmpDetails().getFormattedName());
        empNumValue.setText(String.valueOf(empAccount.getEmpID()));
        empIDLabelValue.setText(String.valueOf(empAccount.getEmpID()));
        addressLabelValue.setText(empAccount.getEmpAddress());
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

        if(empAccount.getEmpDetails().getEmpImmediateSupervisor() != null){
            supervisorLabelValue.setText(empAccount.getEmpDetails().getEmpImmediateSupervisor().getFormattedName());
        }
        if(empAccount.getEmpDetails().getEmpPosition() != null){
            positionLabelValue.setText(empAccount.getEmpDetails().getEmpPosition().getPosition());
        }
        if(empAccount.getEmpDetails().getEmpStatus() != null){
            statusLabelValue.setText(empAccount.getEmpDetails().getEmpStatus().getStatus());
        }
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
        
        ///payroll
        empIDPayLabelValue.setText(String.valueOf(empAccount.getEmpID()));
        namePayLabelValue.setText(empAccount.getEmpDetails().getFormattedName());
        //addressPayLabelValue.setText(empAccount.getEmpAddress());
        
        hourlyRatePayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpHourlyRate()));
        ricePayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpRice()));
        phonePayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpPhone()));
        clothingPayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpClothing()));
        totalAllowPayLabelValue.setText(String.valueOf(SalaryCalculation.getTotalAllowance(empAccount.getEmpDetails())));
        basicSalaryPayLabelValue.setText(String.valueOf(empAccount.getEmpDetails().getEmpBasicSalary()));
        
    }
    private void updatePayrollLabels(List<Employee> employeeHours){
        if(employeeHours.isEmpty()){
            resetPayrollLabels();
        }else{
            totalHoursPayLabelValue.setText(SalaryCalculation.getFormattedTotalHoursWorked(employeeHours));

            double basicSalary = SalaryCalculation.getBasicSalary(employeeHours, empAccount);
            computedSalaryLabelValue.setText(String.format("%.2f",basicSalary));

            double grossPay = SalaryCalculation.getGrossSalary(employeeHours, empAccount);
            grossSalaryPayLabelValue.setText(String.format("%.2f", grossPay));

            double sssContri = payrollService.calculateSssContribution(basicSalary);

            // Contributions
            philhealthContriPayLabelValue.setText(String.format("%.2f", SalaryCalculation.calculatePhilHealthContribution(grossPay)));
            pagibigContriPayLabelValue.setText(String.format("%.2f", SalaryCalculation.calculatePagibigContribution(grossPay)));
            sssContriPayLabelValue.setText(String.format("%.2f", sssContri));
            totalDeductionsPayLabelValue.setText(String.format("%.2f", SalaryCalculation.getTotalDeductions(grossPay,sssContri)));

            // Tax and Net Pay
            taxableIncomePayLabelValue.setText(String.format("%.2f", SalaryCalculation.getTaxableIncome(grossPay,sssContri)));
            taxPayLabelValue.setText(String.format("%.2f", SalaryCalculation.calculateWithholdingTax(grossPay)));
            netPayLabelValue.setText(String.format("%.2f", SalaryCalculation.getNetPay(grossPay,sssContri)));
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
        payrollButton = new javax.swing.JButton();
        leaveManagementButton = new javax.swing.JButton();
        profileButton = new javax.swing.JButton();
        usernameLabel = new javax.swing.JLabel();
        fullNameValue2 = new javax.swing.JLabel();
        empNumValue = new javax.swing.JLabel();
        timeIn = new javax.swing.JButton();
        timeOut = new javax.swing.JButton();
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
        changePassword = new javax.swing.JButton();
        confirmPasswordTField = new javax.swing.JPasswordField();
        newPasswordTField = new javax.swing.JPasswordField();
        existingPasswordTField = new javax.swing.JPasswordField();
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
        jLabel19 = new javax.swing.JLabel();
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
        leaveRequest = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        applyLabel = new javax.swing.JLabel();
        leaveTypeLabel = new javax.swing.JLabel();
        leaveTypeDropdown = new javax.swing.JComboBox<>();
        leaveSubjectLabel = new javax.swing.JLabel();
        leaveSubjectTFieldValue = new javax.swing.JTextField();
        leaveDatesLabel = new javax.swing.JLabel();
        leaveDateFromTFieldValue = new javax.swing.JTextField();
        leaveDateFromLabel = new javax.swing.JLabel();
        reasonLabel = new javax.swing.JLabel();
        reasonTFieldValue = new javax.swing.JTextField();
        applyLeaveButton = new javax.swing.JButton();
        leaveDateToLabel = new javax.swing.JLabel();
        leaveDateToTFieldValue = new javax.swing.JTextField();
        leaveRequestLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        statusLabell = new javax.swing.JLabel();
        totalLeaveLabel = new javax.swing.JLabel();
        totalLeaveTakenLabel = new javax.swing.JLabel();
        totalLeaveBalLabel = new javax.swing.JLabel();
        totalLeaveLabelValue = new javax.swing.JLabel();
        totalLeaveTakenLabelValue = new javax.swing.JLabel();
        totalLeaveBalLabelValue = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        leaveTable = new javax.swing.JTable();
        leaveHistoryLabel = new javax.swing.JLabel();
        withdrawLeaveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        header.setBackground(new java.awt.Color(255, 102, 51));

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
                .addGap(49, 49, 49))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutButton))
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
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addContainerGap())
        );

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
        leaveManagementButton.setText(" Leave Request");
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

        timeIn.setBackground(new java.awt.Color(51, 102, 255));
        timeIn.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        timeIn.setForeground(new java.awt.Color(255, 255, 255));
        timeIn.setText("TIME IN");
        timeIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeInActionPerformed(evt);
            }
        });

        timeOut.setBackground(new java.awt.Color(51, 102, 255));
        timeOut.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        timeOut.setForeground(new java.awt.Color(255, 255, 255));
        timeOut.setText("TIME OUT");
        timeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeOutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout navigationLayout = new javax.swing.GroupLayout(navigation);
        navigation.setLayout(navigationLayout);
        navigationLayout.setHorizontalGroup(
            navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(leaveManagementButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(timeOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(timeIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(navigationLayout.createSequentialGroup()
                .addGroup(navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, navigationLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(profilePictureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addGroup(navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empNumValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fullNameValue2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(usernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(profileButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(payrollButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addComponent(timeIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(timeOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(profileButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(payrollButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leaveManagementButton)
                .addGap(563, 563, 563)
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
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tinLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(basicSalaryLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(421, 421, 421))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clothingLabelValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(phoneAllowanceValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(hourlyrateLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(riceLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(315, 315, 315)
                        .addComponent(jLabel31)
                        .addGap(63, 63, 63))
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
                            .addComponent(sssLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pagibigLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(supervisorLabelValue, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                            .addComponent(riceLabelValue)))
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

        changePassword.setBackground(new java.awt.Color(0, 0, 0));
        changePassword.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        changePassword.setForeground(new java.awt.Color(255, 255, 255));
        changePassword.setText("Submit");
        changePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePasswordActionPerformed(evt);
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
                    .addComponent(changePassword)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel17)
                    .addComponent(jLabel2)
                    .addComponent(newPasswordTField)
                    .addComponent(existingPasswordTField)
                    .addComponent(confirmPasswordTField))
                .addGap(40, 40, 40))
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
                .addComponent(changePassword)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout profileLayout = new javax.swing.GroupLayout(profile);
        profile.setLayout(profileLayout);
        profileLayout.setHorizontalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(profileLabel)
                    .addGroup(profileLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 1046, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        profileLayout.setVerticalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(profileLabel)
                .addGap(18, 18, 18)
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mphCards.add(profile, "card3");

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

        jLabel19.setFont(new java.awt.Font("Century Gothic", 2, 18)); // NOI18N
        jLabel19.setText("Employee Details");

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
                    .addComponent(jLabel19)
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
                            .addComponent(jLabel19)
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

        javax.swing.GroupLayout payrollLayout = new javax.swing.GroupLayout(payroll);
        payroll.setLayout(payrollLayout);
        payrollLayout.setHorizontalGroup(
            payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payrollLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(salarySlips)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        payrollLayout.setVerticalGroup(
            payrollLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(payrollLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(salarySlips)
                .addGap(30, 30, 30)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        mphCards.add(payroll, "card4");

        leaveRequest.setBackground(new java.awt.Color(229, 229, 229));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        applyLabel.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        applyLabel.setText("Apply for Leave");

        leaveTypeLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        leaveTypeLabel.setText("Leave Type");

        leaveTypeDropdown.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        leaveSubjectLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        leaveSubjectLabel.setText("Leave Subject");

        leaveDatesLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        leaveDatesLabel.setText("Leave Dates (YYYY-MM-DD)");

        leaveDateFromTFieldValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leaveDateFromTFieldValueActionPerformed(evt);
            }
        });

        leaveDateFromLabel.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        leaveDateFromLabel.setText("Date From");

        reasonLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        reasonLabel.setText("Reason");

        applyLeaveButton.setBackground(new java.awt.Color(51, 51, 255));
        applyLeaveButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        applyLeaveButton.setForeground(new java.awt.Color(255, 255, 255));
        applyLeaveButton.setText("Apply for Leave");
        applyLeaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyLeaveButtonActionPerformed(evt);
            }
        });

        leaveDateToLabel.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        leaveDateToLabel.setText("Date To");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(applyLeaveButton)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(reasonTFieldValue)
                        .addComponent(leaveSubjectTFieldValue)
                        .addComponent(leaveTypeDropdown, 0, 402, Short.MAX_VALUE)
                        .addComponent(reasonLabel)
                        .addComponent(leaveSubjectLabel)
                        .addComponent(leaveTypeLabel)
                        .addComponent(applyLabel)
                        .addComponent(leaveDatesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel12Layout.createSequentialGroup()
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(leaveDateFromLabel)
                                .addComponent(leaveDateFromTFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(42, 42, 42)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(leaveDateToLabel)
                                .addComponent(leaveDateToTFieldValue)))))
                .addGap(50, 50, 50))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(applyLabel)
                .addGap(31, 31, 31)
                .addComponent(leaveTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(leaveTypeDropdown, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(leaveSubjectLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(leaveSubjectTFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(leaveDatesLabel)
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leaveDateFromLabel)
                    .addComponent(leaveDateToLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(leaveDateToTFieldValue)
                    .addComponent(leaveDateFromTFieldValue, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(reasonLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reasonTFieldValue, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(applyLeaveButton)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        leaveRequestLabel.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        leaveRequestLabel.setText("Leave request");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        statusLabell.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        statusLabell.setText("Status");

        totalLeaveLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        totalLeaveLabel.setText("Total Leave");

        totalLeaveTakenLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        totalLeaveTakenLabel.setText("Total Leave Taken");

        totalLeaveBalLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        totalLeaveBalLabel.setText("Total Leave Available");

        totalLeaveLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        totalLeaveLabelValue.setText("25");

        totalLeaveTakenLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        totalLeaveTakenLabelValue.setText("3");

        totalLeaveBalLabelValue.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        totalLeaveBalLabelValue.setText("22");

        leaveTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Code", "Subject", "Type", "Date From", "Date To", "Total Days", "Reason", "Status"
            }
        ));
        jScrollPane2.setViewportView(leaveTable);

        leaveHistoryLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        leaveHistoryLabel.setText("Leave History");

        withdrawLeaveButton.setBackground(new java.awt.Color(255, 102, 0));
        withdrawLeaveButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        withdrawLeaveButton.setForeground(new java.awt.Color(255, 255, 255));
        withdrawLeaveButton.setText("Withdraw Leave");
        withdrawLeaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawLeaveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(withdrawLeaveButton)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(leaveHistoryLabel)
                        .addComponent(statusLabell)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(totalLeaveBalLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(totalLeaveBalLabelValue))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(totalLeaveLabel)
                                    .addComponent(totalLeaveTakenLabel))
                                .addGap(85, 85, 85)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(totalLeaveLabelValue)
                                    .addComponent(totalLeaveTakenLabelValue))))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(statusLabell)
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLeaveLabel)
                    .addComponent(totalLeaveLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLeaveTakenLabel)
                    .addComponent(totalLeaveTakenLabelValue))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLeaveBalLabel)
                    .addComponent(totalLeaveBalLabelValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(leaveHistoryLabel)
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(withdrawLeaveButton)
                .addGap(13, 13, 13))
        );

        javax.swing.GroupLayout leaveRequestLayout = new javax.swing.GroupLayout(leaveRequest);
        leaveRequest.setLayout(leaveRequestLayout);
        leaveRequestLayout.setHorizontalGroup(
            leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leaveRequestLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leaveRequestLabel)
                    .addGroup(leaveRequestLayout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(57, 57, 57))
        );
        leaveRequestLayout.setVerticalGroup(
            leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leaveRequestLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(leaveRequestLabel)
                .addGap(30, 30, 30)
                .addGroup(leaveRequestLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
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
    refreshLeaveHistoryTable();
    refreshLeaveBalance();
    }//GEN-LAST:event_leaveManagementButtonActionPerformed

    private void payrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payrollButtonActionPerformed
    cardLayout.show(mphCards, "card4"); 
    }//GEN-LAST:event_payrollButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     
    }//GEN-LAST:event_jButton1ActionPerformed

    
      
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

    private void changePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePasswordActionPerformed
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
    }//GEN-LAST:event_changePasswordActionPerformed
    private void validateAttendanceData(List<Employee> empHours) {
        if (empHours.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No attendance records found for the selected month and year.", "No Attendance", JOptionPane.ERROR_MESSAGE);
        } else {
            populateAttendanceTable(empHours);
            updatePayrollLabels(empHours);
        }
    }
    private void monthDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthDropdownActionPerformed
        if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
          Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
          Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();

          if (monthValue != null && year != null) {
              List<Employee> empHours = getEmployeeHours(monthValue, year);
              validateAttendanceData(empHours);
          }
      }
    }//GEN-LAST:event_monthDropdownActionPerformed

    private void yearDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearDropdownActionPerformed
        if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
                Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
                Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();

                if (monthValue != null && year != null) {
                    List<Employee> empHours = getEmployeeHours(monthValue, year);
                    validateAttendanceData(empHours);
                }
            } 
    }//GEN-LAST:event_yearDropdownActionPerformed

    private void populateAttendanceTable(List<Employee> empHours){
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);

        for(Employee employeeHours : empHours) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(employeeHours.getDate());
            rowData.add(employeeHours.getTimeIn());
            rowData.add(employeeHours.getTimeOut());
            rowData.add((employeeHours.getTimeOut() != null) ? employeeHours.getTimeOut() : "Still Active");
            model.addRow(rowData);
        }
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
        Arrays.asList(2022, 2023, 2024,2025).forEach(year -> { 
            yearDropdown.addItem(new ComboItem(year,String.valueOf(year)));

        });
    }
    
    private void loadAllLeaveTypes(){
        List<LeaveType> leaveTypes = empService.getAllLeaveTypes();
        leaveTypeDropdown.addItem(new ComboItem(null,"Select Leave Type"));
        for(LeaveType empLeaveType : leaveTypes){
            leaveTypeDropdown.addItem(new ComboItem(empLeaveType.getId(),empLeaveType.getLeaveType()));
        }
    }     
                                        
    
    private List<Employee> getEmployeeHours(int month, int year){
       Calendar dateFrom = Calendar.getInstance();
       dateFrom.set(Calendar.MONTH,month);
       dateFrom.set(Calendar.YEAR, year);
       dateFrom.set(Calendar.DATE,dateFrom.getActualMinimum(Calendar.DATE));
       
       Calendar dateTo = Calendar.getInstance();
       dateTo.set(Calendar.MONTH,month);
       dateTo.set(Calendar.YEAR, year);
       dateTo.set(Calendar.DATE,dateTo.getActualMaximum(Calendar.DATE));
       
       return payrollService.getEmployeeHours(empAccount.getEmpID(), dateFrom.getTime(),dateTo.getTime());
    }
    private HR updateLeaveDetailValues(){
    
    int empID = empAccount.getEmpID();
    String subject = leaveSubjectTFieldValue.getText().trim() !=null ? leaveSubjectTFieldValue.getText() : "";
    String reason= reasonTFieldValue.getText().trim()!=null ?  reasonTFieldValue.getText().trim() : "";
    
    
    HR leaveDetails = new HR();
    leaveDetails.setEmpID(empID);
    leaveDetails.setSubject(subject);
    leaveDetails.setReason(reason);
    leaveDetails.setStatus(LeaveStatus.PENDING);
        try{
            java.util.Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(leaveDateFromTFieldValue.getText().trim());
            java.util.Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(leaveDateToTFieldValue.getText().trim());
            long time_difference = dateTo.getTime() - dateFrom.getTime();    
            long days_difference = TimeUnit.MILLISECONDS.toDays(time_difference) % 365;
            leaveDetails.setTotalDays((int)days_difference);
            java.sql.Date dateFromSql = new  java.sql.Date(dateFrom.getTime());
            java.sql.Date dateToSql = new  java.sql.Date(dateTo.getTime());
            leaveDetails.setDateFrom(dateFromSql);
            leaveDetails.setDateTo(dateToSql);

        }catch(ParseException ex){

        }
        ComboItem leaveTypeValue = (ComboItem) leaveTypeDropdown.getSelectedItem();

        if(leaveTypeValue.getKey() != null){
             leaveDetails.setLeaveType(empService.getLeaveTypeById(leaveTypeValue.getKey()));
        }
        return leaveDetails;
    }
    
    private void applyLeaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyLeaveButtonActionPerformed
        HR leaveDet = updateLeaveDetailValues(); 
        try {
            validateLeaveRequiredFields(leaveDet); 
            empService.addLeaveRequest(leaveDet); 

            JOptionPane.showMessageDialog(null, "Leave request sent!");

            clearLeaveTextFields();
            refreshLeaveHistoryTable();
            updateLeaveBalance();
            refreshLeaveBalance();

        } catch (RuntimeException e) {
            System.err.println("Leave request validation failed: " + e.getMessage());
        }
        
    }//GEN-LAST:event_applyLeaveButtonActionPerformed
    
    private void refreshLeaveBalance(){
       LeaveBalance leaveBalance = empService.getLeaveBalance(empAccount.getEmpID());
       totalLeaveLabelValue.setText(String.valueOf(leaveBalance.getTotal()));
       totalLeaveTakenLabelValue.setText(String.valueOf(leaveBalance.getTaken()));
       totalLeaveBalLabelValue.setText(String.valueOf(leaveBalance.getAvailable()));
        
    }
    
    private void validateLeaveRequiredFields(HR leaveDetails){
        List<String> errors = new ArrayList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        if(leaveDetails.getLeaveType() == null ){
           errors.add("Leave Type");
        }
        if(StringUtils.isEmpty(leaveDetails.getSubject())){
           errors.add("Leave Subject");
        }
        if(leaveDetails.getDateFrom()== null ){
           errors.add("Date From");
        }
        if(leaveDetails.getDateTo()== null ){
           errors.add("Date To");
        }
        if(StringUtils.isEmpty(leaveDetails.getReason())){
           errors.add("Reason");
        }
        if(!errors.isEmpty()){
            String errorMessage = "These fields are required: \n";
            for(String s: errors){
                errorMessage += s+"\n";
            }
            JOptionPane.showMessageDialog(this, errorMessage);
            throw new RuntimeException();
        }
        
        try {
        dateFormat.parse(leaveDetails.getDateFrom().toString()); // Validates Date From format
        dateFormat.parse(leaveDetails.getDateTo().toString());   // Validates Date To format
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Date From and Date To must be in YYYY-MM-DD format.", 
                    "Date Format Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException();
        }
        
        Date today = new Date(System.currentTimeMillis());

        if (leaveDetails.getDateFrom().before(today)) {
            JOptionPane.showMessageDialog(this, "Leave Date From cannot be in the past.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException();
        }


        if (leaveDetails.getDateFrom().after(leaveDetails.getDateTo())) {
            JOptionPane.showMessageDialog(this, "Leave Date To cannot be earlier than Leave Date From.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException();
        }
    }
   
    
    private void withdrawLeaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawLeaveButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) leaveTable.getModel();
        int selectedIndex = leaveTable.getSelectedRow();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select which leave to withdraw.");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(selectedIndex, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Do you want to withdraw the selected leave request?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            empService.deleteLeaveRequest(id);
            JOptionPane.showMessageDialog(this, "Withdrawal successful");
            
            clearLeaveTextFields();
            refreshLeaveHistoryTable();
            updateLeaveBalance();
            refreshLeaveBalance();
        }                  
    }//GEN-LAST:event_withdrawLeaveButtonActionPerformed
    private void checkAttendanceStatusForToday() {
        try {
            boolean alreadyTimedIn = empService.hasTimeInToday(currentEmployeeId);
            boolean alreadyTimedOut = empService.hasTimeOutToday(currentEmployeeId);

            if (alreadyTimedIn) {
                if (alreadyTimedOut) {
                    timeIn.setEnabled(false);
                    timeOut.setEnabled(false);
                } else {
                    timeIn.setEnabled(false);
                    timeOut.setEnabled(true);
                }
            } else {
                timeIn.setEnabled(true);
                timeOut.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error checking today's attendance: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isWithinAllowedTime(Time startTime, Time endTime) {
        Calendar now = Calendar.getInstance();
        Time currentTime = new Time(now.getTimeInMillis()); // Get current system time

        return !currentTime.before(startTime) && !currentTime.after(endTime);
    }
    
    private void timeInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeInActionPerformed
        if (!isWithinAllowedTime(Time.valueOf("08:00:00"), Time.valueOf("16:00:00"))) {
               JOptionPane.showMessageDialog(this, 
                   "Time-in must be between 8:00 AM and 4:00 PM.", 
                   "Time Restriction", JOptionPane.WARNING_MESSAGE);
               return;
           }

           try {
               Employee attendanceDetails = new Employee();
               attendanceDetails.setEmpID(currentEmployeeId);

               Employee result = empService.timeIn(attendanceDetails);

               if (result.getAttendanceId() > 0) {
                   JOptionPane.showMessageDialog(this, 
                       "Time-in successful for Employee ID: " + result.getEmpID(), 
                       "Success", JOptionPane.INFORMATION_MESSAGE);

                   timeIn.setEnabled(false);
                   timeOut.setEnabled(true);
               } else {
                   JOptionPane.showMessageDialog(this, 
                       "Time-in failed!", 
                       "Error", JOptionPane.ERROR_MESSAGE);
               }

           } catch (Exception ex) {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(this, 
                   "Database error occurred while timing in: " + ex.getMessage(), 
                   "Error", JOptionPane.ERROR_MESSAGE);
           }
    }//GEN-LAST:event_timeInActionPerformed

    private void timeOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeOutActionPerformed
       if (!isWithinAllowedTime(Time.valueOf("09:00:00"), Time.valueOf("17:00:00"))) {
        JOptionPane.showMessageDialog(this, 
            "Time-out must be between 9:00 AM and 5:00 PM.", 
            "Time Restriction", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Employee attendanceDetails = new Employee();
            attendanceDetails.setEmpID(currentEmployeeId);

            empService.timeOut(attendanceDetails);

            JOptionPane.showMessageDialog(this, 
                "Time-out successful for Employee ID: " + attendanceDetails.getEmpID(), 
                "Success", JOptionPane.INFORMATION_MESSAGE);

            timeOut.setEnabled(false);
            timeIn.setEnabled(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "An error occurred while timing out: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_timeOutActionPerformed

    private void leaveDateFromTFieldValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leaveDateFromTFieldValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_leaveDateFromTFieldValueActionPerformed

    private void updateLeaveBalance(){
        List<HR> leaveDetails =  empService.getLeavesByEmployee(empAccount.getEmpID());
        LeaveBalance balance = new LeaveBalance();
        balance.setEmpID(empAccount.getEmpID());
        balance.updateLeaveBalance(leaveDetails);
        empService.updateLeaveBalance(balance);
    }
    
    private void clearLeaveTextFields(){
        leaveSubjectTFieldValue.setText("");
        leaveDateFromTFieldValue.setText("");
        leaveDateToTFieldValue.setText("");
        reasonTFieldValue.setText("");
        leaveTypeDropdown.setSelectedIndex(0);
    
    }
    private void refreshLeaveHistoryTable(){
        List<HR> leaveDetails =  empService.getLeavesByEmployee(empAccount.getEmpID());
        DefaultTableModel model = (DefaultTableModel) leaveTable.getModel();
        model.setRowCount(0);

        for(HR leave : leaveDetails) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(leave.getLeaveId());
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
            java.util.logging.Logger.getLogger(EmployeeDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmployeeDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmployeeDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmployeeDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
    private javax.swing.JLabel addressLabelValue;
    private javax.swing.JLabel allowPayLabel;
    private javax.swing.JLabel applyLabel;
    private javax.swing.JButton applyLeaveButton;
    private javax.swing.JTable attendanceTable;
    private javax.swing.JLabel basicSalaryLabel;
    private javax.swing.JLabel basicSalaryLabelValue;
    private javax.swing.JLabel basicSalaryPayLabel;
    private javax.swing.JLabel basicSalaryPayLabelValue;
    private javax.swing.JLabel bdayLabelValue;
    private javax.swing.JLabel birthdayLabel;
    private javax.swing.JLabel birthdayPayLabel;
    private javax.swing.JLabel birthdayPayLabelValue;
    private javax.swing.JButton changePassword;
    private javax.swing.JLabel clothingLabel;
    private javax.swing.JLabel clothingLabelValue;
    private javax.swing.JLabel clothingPayLabel;
    private javax.swing.JLabel clothingPayLabelValue;
    private javax.swing.JLabel computedSalaryLabel;
    private javax.swing.JLabel computedSalaryLabelValue;
    private javax.swing.JPasswordField confirmPasswordTField;
    private javax.swing.JLabel deductionsPayLabel;
    private javax.swing.JLabel empIDLabelValue;
    private javax.swing.JLabel empIDPayLabel;
    private javax.swing.JLabel empIDPayLabelValue;
    private javax.swing.JLabel empNumValue;
    private javax.swing.JPasswordField existingPasswordTField;
    private javax.swing.JLabel fullNameValue;
    private javax.swing.JLabel fullNameValue2;
    private javax.swing.JLabel grossSalaryPayLabel;
    private javax.swing.JLabel grossSalaryPayLabelValue;
    private javax.swing.JPanel header;
    private javax.swing.JLabel hourlyRatePayLabel;
    private javax.swing.JLabel hourlyRatePayLabelValue;
    private javax.swing.JLabel hourlyrateLabel;
    private javax.swing.JLabel hourlyrateLabelValue;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel leaveDateFromLabel;
    private javax.swing.JTextField leaveDateFromTFieldValue;
    private javax.swing.JLabel leaveDateToLabel;
    private javax.swing.JTextField leaveDateToTFieldValue;
    private javax.swing.JLabel leaveDatesLabel;
    private javax.swing.JLabel leaveHistoryLabel;
    private javax.swing.JButton leaveManagementButton;
    private javax.swing.JPanel leaveRequest;
    private javax.swing.JLabel leaveRequestLabel;
    private javax.swing.JLabel leaveSubjectLabel;
    private javax.swing.JTextField leaveSubjectTFieldValue;
    private javax.swing.JTable leaveTable;
    private javax.swing.JComboBox<ComboItem> leaveTypeDropdown;
    private javax.swing.JLabel leaveTypeLabel;
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
    private javax.swing.JPanel payroll;
    private javax.swing.JButton payrollButton;
    private javax.swing.JLabel phealthLabel;
    private javax.swing.JLabel philhealthContriPayLabel;
    private javax.swing.JLabel philhealthContriPayLabelValue;
    private javax.swing.JLabel philhealthLabelValue;
    private javax.swing.JLabel phoneAllowanceLabel;
    private javax.swing.JLabel phoneAllowanceValue;
    private javax.swing.JLabel phoneLabelValue;
    private javax.swing.JLabel phoneNumberLabel;
    private javax.swing.JLabel phonePayLabel;
    private javax.swing.JLabel phonePayLabelValue;
    private javax.swing.JLabel positionLabelValue;
    private javax.swing.JLabel positionPayLabel;
    private javax.swing.JLabel positionPayLabelValue;
    private javax.swing.JPanel profile;
    private javax.swing.JButton profileButton;
    private javax.swing.JLabel profileLabel;
    private javax.swing.JLabel profilePictureLabel;
    private javax.swing.JLabel reasonLabel;
    private javax.swing.JTextField reasonTFieldValue;
    private javax.swing.JLabel riceLabelValue;
    private javax.swing.JLabel ricePayLabel;
    private javax.swing.JLabel ricePayLabelValue;
    private javax.swing.JLabel riceSubsidyLabel;
    private javax.swing.JLabel salaryDetailsPayLabel;
    private javax.swing.JLabel salarySlips;
    private javax.swing.JLabel sssContriPayLabel;
    private javax.swing.JLabel sssContriPayLabelValue;
    private javax.swing.JLabel sssLabel;
    private javax.swing.JLabel sssLabelValue;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusLabelValue;
    private javax.swing.JLabel statusLabell;
    private javax.swing.JLabel statusPayLabel;
    private javax.swing.JLabel statusPayLabelValue;
    private javax.swing.JLabel supervisorLabel;
    private javax.swing.JLabel supervisorLabelValue;
    private javax.swing.JLabel taxPayLabel;
    private javax.swing.JLabel taxPayLabelValue;
    private javax.swing.JLabel taxableIncomePayLabel;
    private javax.swing.JLabel taxableIncomePayLabelValue;
    private javax.swing.JButton timeIn;
    private javax.swing.JButton timeOut;
    private javax.swing.JLabel tinLabel;
    private javax.swing.JLabel tinLabelValue;
    private javax.swing.JLabel totalAllowPayLabel;
    private javax.swing.JLabel totalAllowPayLabelValue;
    private javax.swing.JLabel totalDeductionsPayLabel;
    private javax.swing.JLabel totalDeductionsPayLabelValue;
    private javax.swing.JLabel totalHoursPayLabel;
    private javax.swing.JLabel totalHoursPayLabelValue;
    private javax.swing.JLabel totalLeaveBalLabel;
    private javax.swing.JLabel totalLeaveBalLabelValue;
    private javax.swing.JLabel totalLeaveLabel;
    private javax.swing.JLabel totalLeaveLabelValue;
    private javax.swing.JLabel totalLeaveTakenLabel;
    private javax.swing.JLabel totalLeaveTakenLabelValue;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JButton withdrawLeaveButton;
    private javax.swing.JComboBox<ComboItem> yearDropdown;
    // End of variables declaration//GEN-END:variables
}
