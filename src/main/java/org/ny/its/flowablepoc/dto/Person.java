package org.ny.its.flowablepoc.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @NotEmpty(message = "First name is required.") // Server-side validation
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;
    @NotEmpty(message = "Last name is required.") // Must not be null or empty string
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.") // Length constraint
    private String lastName;

    private String email;
    // For gender, assuming it's a dropdown and you want a selection
    @NotEmpty(message = "Please select a gender.") // Ensure a gender is chosen (not the default empty option)
    private String gender;

    // If stored as String (often recommended for IDs like SSN):
    @NotEmpty(message = "SSN is required.")
    @Pattern(regexp = "\\d{5}", message = "SSN must be exactly 5 digits.") // Use regex for format
    private String ssn;

    // Date of Birth
    @NotNull(message = "Date of birth is required.") // Ensure it's not null
    private LocalDate dateofbirth;

    private String incarcerationStatus;

    private String updateIncarcerationStatus;

    private String ssnValid;

    private String updateSSNValid;

    private String ssnInWords;

    // --- Optional: For Gender Dropdown ---
    // You might have a static list or fetch this from a service
    public List<String> getGenderOptions() {
        return List.of("Male", "Female", "Other", "Prefer not to say");
    }

    // --- Optional: For Gender Dropdown ---
    // You might have a static list or fetch this from a service
    public List<String> getIncarcerationOptions() {
        return List.of("Select","Incarcerated", "Not Incarcerated", "On Parole");
    }

    // You might have a static list or fetch this from a service
    public List<String> getSsnValidationOptions() {
        return List.of("Select","true", "false");
    }
}
