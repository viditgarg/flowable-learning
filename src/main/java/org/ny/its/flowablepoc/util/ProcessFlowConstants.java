package org.ny.its.flowablepoc.util;

public final class ProcessFlowConstants {

    // Prevent instantiation
    private ProcessFlowConstants() {}

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

    // Status values
    public static final String STATUS_INCARCERATED = "Incarcerated";
    public static final String STATUS_NOT_INCARCERATED = "Not Incarcerated";
    public static final String STATUS_ON_PAROLE = "On Parole";

}
