/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.payroll.main;

import com.payroll.subdomain.ComboItem;
import com.payroll.domain.IT;
import com.payroll.domain.Person;
import com.payroll.domain.Employee;
import com.payroll.services.HRService;
import com.payroll.services.ITService;
import com.payroll.services.FinanceService;
import com.payroll.util.DatabaseConnection;
import com.payroll.domain.SalaryCalculation;
import java.awt.CardLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author leniejoice
 */


public class FinanceDashboard extends javax.swing.JFrame {
    private DatabaseConnection dbConnection;
    private CardLayout cardLayout;
    private IT empAccount;
    private ITService empAccountService;
    private HRService hrService;
    private FinanceService payrollService;
    private Integer employeeSearchID;
    
    public FinanceDashboard(IT empAccount) {
        initComponents();
        cardLayout = (CardLayout)(mphCards.getLayout());
        this.empAccount=empAccount;
        this.dbConnection = new DatabaseConnection();
        updateUserLabels(empAccount);
        this.empAccountService = new ITService(this.dbConnection);
        this.hrService = new HRService(this.dbConnection);  
        this.payrollService = new FinanceService(this.dbConnection);
        loadAllYears();
        loadAllMonths();
        
    }
    public FinanceDashboard(){
        this(null);
        
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
            hourlyRatePayLabelValue.setText(String.format("%.2f", empDetails.getEmpHourlyRate()));
            ricePayLabelValue.setText(String.format("%.2f", empDetails.getEmpRice()));
            phonePayLabelValue.setText(String.format("%.2f", empDetails.getEmpPhone()));
            clothingPayLabelValue.setText(String.format("%.2f", empDetails.getEmpClothing()));

            totalAllowPayLabelValue.setText(String.format("%.2f", SalaryCalculation.getTotalAllowance(empDetails)));
            basicSalaryPayLabelValue.setText(String.format("%.2f", empDetails.getEmpBasicSalary()));

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
                birthdayPayLabelValue.setText(formattedBirthday);
            } else {
                bdayLabelValue.setText("N/A");
                birthdayPayLabelValue.setText("N/A");
            }
        }
      
    }
    private void updatePayrollEmpLabels(IT empAccount){
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
        payManagementButton = new javax.swing.JButton();
        payrollButton = new javax.swing.JButton();
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
        updateButton = new javax.swing.JButton();
        empFirstNameLabel = new javax.swing.JLabel();
        empLastNameLabel = new javax.swing.JLabel();
        empDetailsLabel = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
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
        hourlyTField = new javax.swing.JTextField();
        phoneAllowTField = new javax.swing.JTextField();
        clothingTField = new javax.swing.JTextField();
        biMonthlyTField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        employeeIDTField = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();
        salaryDetailsLabel1 = new javax.swing.JLabel();
        firstNameTField = new javax.swing.JLabel();
        lastNameTField = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        searchTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        header.setBackground(new java.awt.Color(0, 0, 102));

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

        jButton1.setBackground(new java.awt.Color(0, 0, 102));
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

        payManagementButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        payManagementButton.setForeground(new java.awt.Color(255, 255, 255));
        payManagementButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/money-bag-16.png"))); // NOI18N
        payManagementButton.setText("Payroll Management");
        payManagementButton.setContentAreaFilled(false);
        payManagementButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        payManagementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payManagementButtonActionPerformed(evt);
            }
        });

        payrollButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        payrollButton.setForeground(new java.awt.Color(255, 255, 255));
        payrollButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/wallet-16.png"))); // NOI18N
        payrollButton.setText("Payroll Details");
        payrollButton.setContentAreaFilled(false);
        payrollButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        payrollButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollButtonActionPerformed(evt);
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
            .addComponent(payManagementButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(payManagementButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 629, Short.MAX_VALUE)
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

        changePasswordButton.setBackground(new java.awt.Color(0, 0, 102));
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

        searchButton1.setBackground(new java.awt.Color(0, 0, 102));
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
        empSectionLabel.setText("Payroll Management");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        updateButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        empFirstNameLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empFirstNameLabel.setText("First Name");

        empLastNameLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empLastNameLabel.setText("Last Name");

        empDetailsLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        empDetailsLabel.setText("Employee Details");

        clearButton.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        clearButton.setText("Clear");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

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

        pagibigTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagibigTFieldActionPerformed(evt);
            }
        });

        salaryDetailsLabel.setFont(new java.awt.Font("Century Gothic", 2, 12)); // NOI18N
        salaryDetailsLabel.setText("Allowances");

        empBiMonthLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empBiMonthLabel.setText("Bi-Monthly");

        empRiceLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empRiceLabel.setText("Rice Subsidy");

        empPhoneAllowLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empPhoneAllowLabel.setText("Phone Allow.");

        empClothAllowLabel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        empClothAllowLabel.setText("Clothing Allow.");

        clothingTField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clothingTFieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel3.setText("Employee ID");

        employeeIDTField.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N

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

        salaryDetailsLabel1.setFont(new java.awt.Font("Century Gothic", 2, 12)); // NOI18N
        salaryDetailsLabel1.setText("Salary Details");

        firstNameTField.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        lastNameTField.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 321, Short.MAX_VALUE)
                        .addComponent(updateButton)
                        .addGap(33, 33, 33))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(empSalaryLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empHourlyLabel)
                            .addComponent(empBiMonthLabel)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(empTinLabel)
                                    .addComponent(empSssLabel))
                                .addGap(68, 68, 68)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(tinTField, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(sssTField, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pagibigTField, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(govIdsLabel)
                            .addComponent(empDetailsLabel)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(empLastNameLabel)
                                    .addComponent(jLabel3)
                                    .addComponent(empFirstNameLabel))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(employeeIDTField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(116, 116, 116)
                                        .addComponent(clearButton))
                                    .addComponent(firstNameTField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lastNameTField)))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(salaryDetailsLabel1)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(empPagibigLabel)
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                            .addComponent(empPhilhealthLabel)
                                            .addGap(18, 18, 18)
                                            .addComponent(philhealthTField, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(salaryDetailsLabel)
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                            .addComponent(empClothAllowLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(clothingTField, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                                        .addGroup(jPanel10Layout.createSequentialGroup()
                                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(empRiceLabel)
                                                .addComponent(empPhoneAllowLabel))
                                            .addGap(18, 18, 18)
                                            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(phoneAllowTField)
                                                .addComponent(riceTField))))
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addGap(109, 109, 109)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(hourlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(salaryTField, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(biMonthlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 688, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(empDetailsLabel)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(clearButton)
                                        .addGap(49, 49, 49))
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(employeeIDTField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(firstNameTField)
                                        .addGap(14, 14, 14))))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(empFirstNameLabel)
                                .addGap(14, 14, 14)))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empLastNameLabel)
                            .addComponent(lastNameTField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(govIdsLabel)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empSssLabel)
                            .addComponent(sssTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(empTinLabel)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(empPagibigLabel)
                                    .addComponent(pagibigTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(tinTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(empPhilhealthLabel)
                            .addComponent(philhealthTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(salaryDetailsLabel)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(empRiceLabel)
                                    .addComponent(riceTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(empPhoneAllowLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(empClothAllowLabel)
                                        .addGap(26, 26, 26)
                                        .addComponent(salaryDetailsLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(salaryTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(empSalaryLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(hourlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(empHourlyLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(empBiMonthLabel)
                                            .addComponent(biMonthlyTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(85, 85, 85))
                                    .addGroup(jPanel10Layout.createSequentialGroup()
                                        .addComponent(phoneAllowTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(clothingTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(updateButton)
                                .addGap(30, 30, 30))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
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

    private void payrollButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payrollButtonActionPerformed
        cardLayout.show(mphCards, "card4"); 
    }//GEN-LAST:event_payrollButtonActionPerformed

    private void payManagementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payManagementButtonActionPerformed
        cardLayout.show(mphCards, "card5"); 
        viewAllPayrollRecords();
    }//GEN-LAST:event_payManagementButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
     
    }//GEN-LAST:event_jButton1ActionPerformed

    private void refreshTable(){
        viewAllPayrollRecords();
    }
    
    private void loadEmployeeValues(int empID){
        Person empDetails = hrService.getByEmpID(empID);
        employeeIDTField.setText(String.valueOf(empDetails.getEmpID()));
        lastNameTField.setText(empDetails.getLastName());
        firstNameTField.setText(empDetails.getFirstName());
        sssTField.setText(empDetails.getEmpSSS());
        philhealthTField.setText(String.valueOf(empDetails.getEmpPhilHealth()));
        tinTField.setText(String.valueOf(empDetails.getEmpTIN()));
        pagibigTField.setText(String.valueOf(empDetails.getEmpPagibig()));
        salaryTField.setText(String.valueOf(empDetails. getEmpBasicSalary()));
        hourlyTField.setText(String.valueOf(empDetails.getEmpHourlyRate()));
        biMonthlyTField.setText(String.valueOf(empDetails.getEmpMonthlyRate()));
        riceTField.setText(String.valueOf(empDetails.getEmpRice()));
        phoneAllowTField.setText(String.valueOf(empDetails.getEmpPhone()));
        clothingTField.setText(String.valueOf(empDetails.getEmpClothing()));
    }
    
    
    private Person updateEmpDetailValues(){
        String lastname = lastNameTField.getText().trim() !=null ? lastNameTField.getText() : "";
        String firstname= firstNameTField.getText().trim() !=null ?  firstNameTField.getText().trim() : "";
        //String birthday = birthdayTField.getText().trim()) !=null ? Date(birthdayTField.getText().trim()) : "";
       
        double salary = !salaryTField.getText().trim().equals("") ? Double.parseDouble(salaryTField.getText().trim()) : 0;
        double hourlyRate = !hourlyTField.getText().trim().equals("") ? Double.parseDouble(hourlyTField.getText().trim()) : 0;
        double biMonthly = !biMonthlyTField.getText().trim().equals("") ?  Double.parseDouble(biMonthlyTField.getText().trim()): 0;
        double rice = !riceTField.getText().trim().equals("") ? Double.parseDouble(riceTField.getText().trim()): 0;
        double phoneAllow= !phoneAllowTField.getText().trim().equals("") ? Double.parseDouble(phoneAllowTField.getText().trim()): 0;
        double clothing = !clothingTField.getText().trim().equals("")? Double.parseDouble(clothingTField.getText().trim()): 0;
        long pagibig = !pagibigTField.getText().trim().equals("")? Long.valueOf(pagibigTField.getText().trim()): 0;
        String sss = sssTField.getText().trim() !=null ? sssTField.getText().trim() :"";
        String tin = tinTField.getText().trim() !=null ? tinTField.getText().trim():"";
        long philhealth = !philhealthTField.getText().equals("") ? Long.valueOf(philhealthTField.getText().trim()): 0;
    
        
        Person empDetails = new Employee();
        
        empDetails.setLastName(lastname);
        empDetails.setFirstName(firstname);

        if(!employeeIDTField.getText().trim().equals("")){
            empDetails.setEmpID(Integer.parseInt(employeeIDTField.getText().trim()));
            empDetails.setEmpID(Integer.parseInt(employeeIDTField.getText().trim()));
        }
        
        empDetails.setEmpBasicSalary(salary);
        empDetails.setEmpHourlyRate(hourlyRate);
        empDetails.setEmpMonthlyRate(biMonthly); 
        empDetails.setEmpRice(rice);
        empDetails.setEmpPhone(phoneAllow);
        empDetails.setEmpClothing(clothing);
        empDetails.setEmpPagibig(pagibig);
        empDetails.setEmpSSS(sss);
        empDetails.setEmpTIN(tin);
        empDetails.setEmpPhilHealth(philhealth);
        
        
        return empDetails;
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

    private void populateAttendanceTable(List<Employee> empHours){
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);

        for(Employee employeeHours : empHours) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(employeeHours.getDate());
            rowData.add(employeeHours.getTimeIn());
            rowData.add(employeeHours.getTimeOut());
            rowData.add(employeeHours.getFormattedHoursWorked());
            model.addRow(rowData);
        }
    }
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
        employeeSearchID = (empId.isEmpty()) ? null : Integer.parseInt(empId);

        if (employeeSearchID != null) {
            IT empAccount = empAccountService.getByEmpID(employeeSearchID);

            if (empAccount != null) {
                updatePayrollEmpLabels(empAccount);

                if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
                    Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
                    Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();

                    fetchAndDisplayEmployeeHours(employeeSearchID, monthValue, year);
                }     
            } else {
                JOptionPane.showMessageDialog(this, "Employee Not Found!", "Error", JOptionPane.ERROR_MESSAGE);
            }   
        } else {
            JOptionPane.showMessageDialog(this, "Please input Employee ID!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_searchButton1ActionPerformed
    private void fetchAndDisplayEmployeeHours(Integer empId, Integer monthValue, Integer year) {
        if (empId != null && monthValue != null && year != null) {
            List<Employee> empHours = getEmployeeHours(monthValue, year, empId);

            if (empHours.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No records found for the selected month and year.", 
                        "No Data", JOptionPane.WARNING_MESSAGE);
            } else {
                populateAttendanceTable(empHours);
                updatePayrollLabels(empHours);
            }
        }
    }
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
        Arrays.asList(2022, 2023, 2024,2025).forEach(year -> { 
            yearDropdown.addItem(new ComboItem(year,String.valueOf(year)));

        });
    }

    private void monthDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthDropdownActionPerformed
        if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
                Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
                Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();
                Integer empId = (employeeSearchID != null) ? employeeSearchID : empAccount.getEmpID();

                fetchAndDisplayEmployeeHours(empId, monthValue, year);
            }      
    }//GEN-LAST:event_monthDropdownActionPerformed

    private void yearDropdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearDropdownActionPerformed
        if (monthDropdown.getSelectedItem() != null && yearDropdown.getSelectedItem() != null) {
            Integer monthValue = ((ComboItem) monthDropdown.getSelectedItem()).getKey();
            Integer year = ((ComboItem) yearDropdown.getSelectedItem()).getKey();
            Integer empId = (employeeSearchID != null) ? employeeSearchID : empAccount.getEmpID();

            fetchAndDisplayEmployeeHours(empId, monthValue, year);
        }    
    }//GEN-LAST:event_yearDropdownActionPerformed
    
    private void viewAllPayrollRecords(){
        List<Person> allEmployee =  hrService.getAllEmployee();
        DefaultTableModel model = (DefaultTableModel) employeeTable.getModel();
        model.setRowCount(0);

        for(Person empDetails : allEmployee) {
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
    }
    private void employeeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeeTableMouseClicked

        DefaultTableModel model  = (DefaultTableModel) employeeTable.getModel();
        int selectedIndex = employeeTable.getSelectedRow();
        int empID = Integer.parseInt(model.getValueAt(selectedIndex,0).toString());
        loadEmployeeValues(empID);
    }//GEN-LAST:event_employeeTableMouseClicked

    private void clothingTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clothingTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clothingTFieldActionPerformed

    private void sssTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sssTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sssTFieldActionPerformed

    private void riceTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_riceTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_riceTFieldActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

        lastNameTField.setText("");
        firstNameTField.setText("");
        sssTField.setText("");
        tinTField.setText("");
        pagibigTField.setText("");
        philhealthTField.setText("");
        salaryTField.setText("");
        hourlyTField.setText("");
        riceTField.setText("");
        phoneAllowTField.setText("");
        clothingTField.setText("");
        biMonthlyTField.setText("");
        employeeIDTField.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try {
            Person empDetails = updateEmpDetailValues();
            payrollService.updatePayrollDetails(empDetails);

            refreshTable();
            clearPayrollLabels();
            if (getEmpAccount().getEmpDetails().getEmpID() == empAccount.getEmpID()) {
                updateUserLabels(empAccount);
                 }
                 JOptionPane.showMessageDialog(null, "Payroll Details has been successfully updated", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
             } catch (Exception e) {
                 JOptionPane.showMessageDialog(null, "Error updating payroll record: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
             }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void pagibigTFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagibigTFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pagibigTFieldActionPerformed
    
    
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
            java.util.logging.Logger.getLogger(FinanceDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FinanceDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FinanceDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FinanceDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FinanceDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addressLabelValue;
    private javax.swing.JLabel allowPayLabel;
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
    private javax.swing.JLabel empBiMonthLabel;
    private javax.swing.JLabel empClothAllowLabel;
    private javax.swing.JLabel empDetailsLabel;
    private javax.swing.JLabel empFirstNameLabel;
    private javax.swing.JLabel empHourlyLabel;
    private javax.swing.JLabel empIDLabelValue;
    private javax.swing.JLabel empIDPayLabel;
    private javax.swing.JLabel empIDPayLabelValue;
    private javax.swing.JLabel empLastNameLabel;
    private javax.swing.JPanel empManagement;
    private javax.swing.JLabel empNumValue;
    private javax.swing.JLabel empPagibigLabel;
    private javax.swing.JLabel empPhilhealthLabel;
    private javax.swing.JLabel empPhoneAllowLabel;
    private javax.swing.JLabel empRiceLabel;
    private javax.swing.JLabel empSalaryLabel;
    private javax.swing.JLabel empSectionLabel;
    private javax.swing.JLabel empSssLabel;
    private javax.swing.JLabel empTinLabel;
    private javax.swing.JLabel employeeIDTField;
    private javax.swing.JTable employeeTable;
    private javax.swing.JPasswordField existingPasswordTField;
    private javax.swing.JLabel firstNameTField;
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lastNameTField;
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
    private javax.swing.JButton payManagementButton;
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
    private javax.swing.JLabel salaryDetailsLabel1;
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
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusLabelValue;
    private javax.swing.JLabel statusPayLabel;
    private javax.swing.JLabel statusPayLabelValue;
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
    private javax.swing.JComboBox<ComboItem> yearDropdown;
    // End of variables declaration//GEN-END:variables
}
