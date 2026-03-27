package org.ny.its.flowablepoc.util;

public final class ProcessFlowConstants {

    //HTML View pages
    public static final String REDIRECT_CASE_HOME = "redirect:/case/home";

    // Prevent instantiation
    private ProcessFlowConstants() {
    }

    // Process IDs
    public static final String CASE_REGISTRATION_V5 = "case_regV5";
    public static final String CASE_CREATION_PROCESS = "caseCreationProcess";

    // Task Keys
    public static final String CASE_DETAILS_TASK = "caseDetailsTask";
    public static final String SSN_MANUAL_TASK = "ssnManualTask";
    public static final String DOCCS_MANUAL_TASK = "doccsManualTask";
    public static final String REVIEW_INCARCERATION_TASK = "reviewIncarcerationTask";
    public static final String FINAL_REVIEW_TASK = "finalReviewTask";

    // Process Variables
    public static final String INCARCERATION_STATUS = "incarcerationStatus";
    public static final String SSN_RESPONSE = "ssnResponse";
    public static final String DOCCS_RESPONSE = "doccsResponse";

    //Response Variable Values
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String INVALID = "Invalid";

    public static final Boolean TRUE = Boolean.TRUE;
    public static final Boolean FALSE = Boolean.FALSE;

    // Status values
    public static final String STATUS_INCARCERATED = "Incarcerated";
    public static final String STATUS_NOT_INCARCERATED = "Not Incarcerated";
    public static final String STATUS_ON_PAROLE = "On Parole";

    //Static Fields
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String GENDER = "gender";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String SSN = "ssn";


    public static final String STREET = "street";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String POSTAL_CODE = "postalCode";


}
